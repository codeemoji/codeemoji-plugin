package codeemoji.inlay.external;

import codeemoji.core.external.CEExternalService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Service
public final class MyExternalService implements CEExternalService<VirtualFile, Object> {

    // TODO remove hard-coded variables
    private static final String OSS_INDEX_API_URL = "https://ossindex.sonatype.org/api/v3/component-report";
    private static final String OSS_API_TOKEN = "d8b039a32f7113c9d23e5baa798322a1e92c2202";
    private static final int OSS_BATCH_SIZE = 128;

    private static final String NIST_NVD_API_URL = "https://services.nvd.nist.gov/rest/json/cves/2.0";
    private static final String NIST_API_TOKEN = "624e5c6d-6f7d-4c3e-b7ad-7352e2958ef6";
    private static final int NIST_BATCH_SIZE = 50;

    private static final String OSV_API_URL = "https://api.osv.dev";

    private final Map<VirtualFile, Object> persistedData = new HashMap<>();
    private Map<DependencyInfo, List<VulnerabilityInfo>> vulnerabilityMap = new HashMap<>();
    private final DependencyExtractor dependencyExtractor = new DependencyExtractor();
    private final NistVulnerabilityScanner nistVulnerabilityScanner = new NistVulnerabilityScanner(NIST_NVD_API_URL, NIST_API_TOKEN, NIST_BATCH_SIZE);
    private final OSSVulnerabilityScanner ossVulnerabilityScanner = new OSSVulnerabilityScanner(OSS_INDEX_API_URL, OSS_API_TOKEN, OSS_BATCH_SIZE);
    private final OSVVulnerabilityScanner OSVVulnerabilityScanner = new OSVVulnerabilityScanner(OSV_API_URL, "", 0);
    private DependencyInfo[] dependencyInfos;

    @Override
    public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        dependencyInfos =  getProjectLibraries(project);
        // vulnerabilityMap = ossVulnerabilityScanner.scanVulnerability(dependencyInfos);
        // vulnerabilityMap = nistVulnerabilityScanner.scanVulnerability((dependencyInfos));
        vulnerabilityMap = OSVVulnerabilityScanner.scanVulnerability((dependencyInfos));
    }

    public DependencyInfo[] getProjectLibraries(Project project) {
        LibraryTable librarytable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        Library[] librariesList = librarytable.getLibraries();
        return Arrays.stream(librariesList)
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
    }

    @Override
    public Map<VirtualFile, Object> getPersistedData() {
        return persistedData;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void buildInfo(@NotNull Map infoResult, @Nullable PsiElement element) {
        if (element != null && element.getProject() != null) {
            DependencyInfo[] currentDependencies = getProjectLibraries(element.getProject());
            boolean changes = !Arrays.equals(dependencyInfos, currentDependencies);
            if (changes) {
                dependencyInfos = currentDependencies;
                // vulnerabilityMap = ossVulnerabilityScanner.scanVulnerability(dependencyInfos);
                vulnerabilityMap = OSVVulnerabilityScanner.scanVulnerability((dependencyInfos));
            }
        }
        Iterator<Map.Entry<DependencyInfo, List<VulnerabilityInfo>>> iterator = vulnerabilityMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<DependencyInfo, List<VulnerabilityInfo>> entry = iterator.next();
            infoResult.put(entry.getKey(), entry.getValue());
        }


    }

    @Nullable
    public Object retrieveData(VirtualFile file) {
        return persistedData.get(file);
    }
}