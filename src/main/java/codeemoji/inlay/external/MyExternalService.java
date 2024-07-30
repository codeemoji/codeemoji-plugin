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

    // TODO remove hardcoded variables
    private static final int NIST_BATCH_SIZE = 50;

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
        scanVulnerabilitiesInBatches(lastScannedDependencies);
    }

    private void scanVulnerabilitiesInBatches(List<JSONObject> dependencies) {
        for (int i = 0; i < dependencies.size(); i += NIST_BATCH_SIZE) {
            List<JSONObject> batch = dependencies.subList(i, Math.min(i + NIST_BATCH_SIZE, dependencies.size()));
            scanVulnerabilities(batch);
            if (i + NIST_BATCH_SIZE < dependencies.size()) {
                try {
                    Thread.sleep(30000); // Wait for 30 seconds before the next batch - NIST directives with API_KEY
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOG.error("Interrupted while waiting between batches", e);
                }
            }
        }
    }

    private void scanVulnerabilities(List<JSONObject> dependencies) {
        List<CompletableFuture<JSONObject>> futures = dependencies.stream()
                .map(vulnerabilityScanner::scanDependencyAsyncNist)
                .collect(Collectors.toList());

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (int i = 0; i < dependencies.size(); i++) {
            JSONObject lib = dependencies.get(i);
            JSONObject vulnerabilityReport = futures.get(i).join();
            try {
                JSONArray vulnerabilities = vulnerabilityReport.getJSONArray("vulnerabilities");
                if (vulnerabilities.length() > 0) {
                    vulnerabilitiesMap.put(lib, vulnerabilityReport);
                }
            } catch (Exception e) {
                LOG.error("Error processing vulnerability report", e);
            }
        }
    }

    @Override
    public Map<VirtualFile, Object> getPersistedData() {
        return persistedData;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void buildInfo(@NotNull Map infoResult, @Nullable PsiElement element) {
        if (element != null) {
            List<JSONObject> currentDependencies = dependencyExtractor.extractProjectDependencies(element.getProject());
            List<JSONObject> newDependencies = getNewDependencies(currentDependencies);

            if (!newDependencies.isEmpty()) {
                LOG.info("New or changed dependencies detected. Scanning " + newDependencies.size() + " dependencies.");
                scanVulnerabilitiesInBatches(newDependencies);
                lastScannedDependencies = currentDependencies;
            }

            infoResult.putAll(vulnerabilitiesMap);
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