package codeemoji.inlay.external;

import codeemoji.core.external.CEExternalService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Service
public final class MyExternalService implements CEExternalService<VirtualFile, Object> {

    // TODO remove hard-coded variables
    private static final String OSS_INDEX_API_URL = "https://ossindex.sonatype.org/api/v3/component-report";
    private static final String NIST_NVD_API_URL = "https://services.nvd.nist.gov/rest/json/cves/2.0";
    private static final String NIST_API_TOKEN = "624e5c6d-6f7d-4c3e-b7ad-7352e2958ef6";
    private static final String OSS_API_TOKEN = "d8b039a32f7113c9d23e5baa798322a1e92c2202";
    private static final int NIST_BATCH_SIZE = 50;

    private static final Logger LOG = Logger.getInstance(MyExternalService.class);

    private final Map<VirtualFile, Object> persistedData = new HashMap<>();
    private Map<DependencyInfo, List<VulnerabilityInfo>> vulnerabilityMap = new HashMap<>();
    private final DependencyExtractor dependencyExtractor = new DependencyExtractor();
    private final NistVulnerabilityScanner nistVulnerabilityScanner = new NistVulnerabilityScanner(NIST_NVD_API_URL, NIST_API_TOKEN);
    private final OSSVulnerabilityScanner ossVulnerabilityScanner = new OSSVulnerabilityScanner(OSS_INDEX_API_URL, OSS_API_TOKEN);

    private List<JSONObject> lastScannedDependencies;
    private DependencyInfo[] dependencyInfos;
    private Library[] scannedDependencies;

    @Override
    public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        scannedDependencies = getProjectLibraries(project);
        dependencyInfos = Arrays.stream(scannedDependencies)
                .map(dep -> {
                    try {
                        return dependencyExtractor.getDependecyInfo(dep);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Error processing dependency: " + dep.getName() + ". Error: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(DependencyInfo[]::new);
        vulnerabilityMap = ossVulnerabilityScanner.scanVulnerability(dependencyInfos);
    }



    public Library[] getProjectLibraries(Project project) {
        LibraryTable librarytable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        Library[] librariesList = librarytable.getLibraries();
        return librariesList;
    }

    @Override
    public Map<VirtualFile, Object> getPersistedData() {
        return persistedData;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void buildInfo(@NotNull Map infoResult, @Nullable PsiElement element) {
        if (element != null && element.getProject() != null) {
            Library[] currentDependencies = getProjectLibraries(element.getProject());

            if (Arrays.equals(scannedDependencies, currentDependencies)) {
                scannedDependencies = currentDependencies;
                dependencyInfos = Arrays.stream(scannedDependencies)
                        .map(dep -> {
                            try {
                                return dependencyExtractor.getDependecyInfo(dep);
                            } catch (IllegalArgumentException e) {
                                System.err.println("Error processing dependency: " + dep.getName() + ". Error: " + e.getMessage());
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .toArray(DependencyInfo[]::new);
                vulnerabilityMap = ossVulnerabilityScanner.scanVulnerability(dependencyInfos);
            }
        }
        for (Map.Entry<DependencyInfo, List<VulnerabilityInfo>> entry : vulnerabilityMap.entrySet()) {
            infoResult.put(entry.getKey(), entry.getValue());
        }

    }

    private List<JSONObject> getNewDependencies(List<JSONObject> currentDependencies) {
        return currentDependencies.stream()
                .filter(dep -> !lastScannedDependencies.stream().anyMatch(lastDep -> lastDep.similar(dep)))
                .collect(Collectors.toList());
    }

    @Nullable
    public Object retrieveData(VirtualFile file) {
        return persistedData.get(file);
    }
}