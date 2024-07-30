package codeemoji.inlay.external;

import codeemoji.core.external.CEExternalService;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public final class MyExternalService implements CEExternalService<VirtualFile, Object> {

    private static final Logger LOG = Logger.getInstance(MyExternalService.class);

    private final Map<VirtualFile, Object> persistedData = new HashMap<>();
    private final Map<JSONObject, JSONObject> vulnerabilitiesMap = new HashMap<>();
    private final DependencyExtractor dependencyExtractor = new DependencyExtractor();
    private final VulnerabilityScanner vulnerabilityScanner = new VulnerabilityScanner();

    private List<JSONObject> lastScannedDependencies;

    @Override
    public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        lastScannedDependencies = dependencyExtractor.extractProjectDependencies(project);
        scanVulnerabilities();
    }

    public void scanVulnerabilities() {

        List<CompletableFuture<JSONObject>> futures = lastScannedDependencies.stream()
                .map(vulnerabilityScanner::scanDependencyAsyncNist)
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (int i = 0; i < lastScannedDependencies.size(); i++) {
            JSONObject lib = lastScannedDependencies.get(i);
            JSONObject vulnerabilityReport = futures.get(i).join();
            try {
                JSONArray vulnerabilities = vulnerabilityReport.getJSONArray("vulnerabilities");
                // saving lib only if vulnerabilities are present
                if (vulnerabilities.length() > 0) {
                    vulnerabilitiesMap.put(lib, vulnerabilityReport);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<VirtualFile, Object> getPersistedData() {
        return persistedData;
    }

    private boolean hasDependenciesChanged(@Nullable PsiElement element) {
        if (element == null) {
            return false;
        }
        Project project = element.getProject();
        if (project == null) {
            return false;
        }
        List<JSONObject> currentDependencies = dependencyExtractor.extractProjectDependencies(element.getProject());

        if (currentDependencies.size() != lastScannedDependencies.size()) {
            return true;
        }
        // TODO evaluate optimization
        for (int i = 0; i < currentDependencies.size(); i++) {
            JSONObject obj1 = currentDependencies.get(i);
            JSONObject obj2 = lastScannedDependencies.get(i);
            if (!obj1.similar(obj2)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void buildInfo(@NotNull Map infoResult, @Nullable PsiElement element) {
        if (hasDependenciesChanged(element)) {
            LOG.info("Project dependencies have changed. Rescanning vulnerabilities.");
            lastScannedDependencies = dependencyExtractor.extractProjectDependencies(element.getProject());
            scanVulnerabilities();
        }

        if (element != null) {
            // Retrieves preprocessed persistent values
            Object data = retrieveData(element.getProject().getWorkspaceFile());
            infoResult.putAll(vulnerabilitiesMap);
        }
    }

    @Nullable
    public Object retrieveData(VirtualFile file) {
        return persistedData.get(file);
    }
}