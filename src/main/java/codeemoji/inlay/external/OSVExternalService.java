package codeemoji.inlay.external;

import codeemoji.core.external.CEExternalService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class OSVExternalService implements CEExternalService<VirtualFile, Object> {
    private static final String OSV_API_URL = "https://api.osv.dev";

    private final Map<VirtualFile, Object> persistedData = new HashMap<>();
    private Map<DependencyInfo, List<VulnerabilityInfo>> vulnerabilityMap = new HashMap<>();
    private final DependencyExtractor dependencyExtractor = new DependencyExtractor();
    private final OSVVulnerabilityScanner osvVulnerabilityScanner = new OSVVulnerabilityScanner(OSV_API_URL, "", 0);
    private DependencyInfo[] dependencyInfos;

    @Override
    public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        dependencyInfos =  getProjectLibraries(project);
        vulnerabilityMap = osvVulnerabilityScanner.scanVulnerability((dependencyInfos));
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
                vulnerabilityMap = osvVulnerabilityScanner.scanVulnerability((dependencyInfos));
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
