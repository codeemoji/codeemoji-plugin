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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private final Map<JSONObject, List<VulnerabilityInfo>> vulnerabilityMap = new HashMap<>();
    private final DependencyExtractor dependencyExtractor = new DependencyExtractor();
    private final VulnerabilityScanner vulnerabilityScanner = new VulnerabilityScanner();

    private List<JSONObject> lastScannedDependencies;

    @Override
    public void preProcess(@NotNull Project project) {
        persistedData.put(project.getWorkspaceFile(), null);
        lastScannedDependencies = dependencyExtractor.extractProjectDependencies(project);
        scanVulnerabilitiesInBatches(lastScannedDependencies);
        Library[] dependenciesOSS = getProjectLibraries(project);
        scanVulnerabilitiesOSS(dependenciesOSS);
    }

    public void scanVulnerabilitiesOSS(Library[] librariesList) {
        List<String> libraryCoordinates = new ArrayList<>();

        for (Library lib : librariesList) {
            try {
                String library = dependencyExtractor.parseDependencyToString(lib);
                libraryCoordinates.add("pkg:maven/" + library);
            } catch (IllegalArgumentException e) {
                System.err.println("Error processing library: " + lib.getName() + ". Error: " + e.getMessage());
            }
        }

        CompletableFuture<JSONArray> future = vulnerabilityScanner.scanDependenciesAsyncOSS(libraryCoordinates);

        future.thenAccept(this::processResults)
                .exceptionally(this::handleError);
    }

    private void processResults(JSONArray results) {
        for (int i = 0; i < results.length(); i++) {
            JSONObject dependency = results.getJSONObject(i);
            String coordinate = dependency.getString("coordinates");
            JSONArray vulnerabilities = dependency.getJSONArray("vulnerabilities");

            System.out.println("Dependency: " + coordinate);
            System.out.println("Vulnerabilities found: " + vulnerabilities.length());

            for (int j = 0; j < vulnerabilities.length(); j++) {
                JSONObject vulnerability = vulnerabilities.getJSONObject(j);
                System.out.println("  - " + vulnerability.getString("title"));
            }
        }
    }

    private Void handleError(Throwable throwable) {
        System.err.println("Error occurred while scanning dependencies: " + throwable.getMessage());
        throwable.printStackTrace();
        return null;
    }

    public Library[] getProjectLibraries(Project project) {
        LibraryTable librarytable = LibraryTablesRegistrar.getInstance().getLibraryTable(project);
        Library[] librariesList = librarytable.getLibraries();
        return librariesList;
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
                List<VulnerabilityInfo> vulnerabilities = parseVulnerabilities(vulnerabilityReport);
                if (!vulnerabilities.isEmpty()) {
                    vulnerabilityMap.put(lib, vulnerabilities);
                }
            } catch (Exception e) {
                LOG.error("Error processing vulnerability report", e);
            }
        }
    }

    private List<VulnerabilityInfo> parseVulnerabilities(JSONObject vulnerabilityReport) {
        List<VulnerabilityInfo> vulnerabilities = new ArrayList<>();
        JSONArray vulnerabilitiesArray = vulnerabilityReport.getJSONArray("vulnerabilities");

        for (int i = 0; i < vulnerabilitiesArray.length(); i++) {
            JSONObject vuln = vulnerabilitiesArray.getJSONObject(i);
            JSONObject cveInfo = vuln.getJSONObject("cve");

            String cve = cveInfo.getString("id");

            String description = "";
            JSONArray descriptions = cveInfo.getJSONArray("descriptions");
            for (int j = 0; j < descriptions.length(); j++) {
                JSONObject desc = descriptions.getJSONObject(j);
                if (desc.getString("lang").equals("en")) {
                    description = desc.getString("value");
                    break;
                }
            }

            String severity = "UNKNOWN";
            if (cveInfo.has("metrics")) {
                JSONObject metrics = cveInfo.getJSONObject("metrics");
                if (metrics.has("cvssMetricV31")) {
                    JSONArray cvssV31 = metrics.getJSONArray("cvssMetricV31");
                    if (cvssV31.length() > 0) {
                        JSONObject cvssData = cvssV31.getJSONObject(0).getJSONObject("cvssData");
                        severity = cvssData.getString("baseSeverity");
                    }
                }
            }

            vulnerabilities.add(new VulnerabilityInfo(cve, description, severity));
        }

        return vulnerabilities;
    }

    @Override
    public Map<VirtualFile, Object> getPersistedData() {
        return persistedData;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void buildInfo(@NotNull Map infoResult, @Nullable PsiElement element) {
        if (element != null && element.getProject() != null) {
            List<JSONObject> currentDependencies = dependencyExtractor.extractProjectDependencies(element.getProject());
            List<JSONObject> newDependencies = getNewDependencies(currentDependencies);

            if (!newDependencies.isEmpty()) {
                lastScannedDependencies = currentDependencies;
                scanVulnerabilitiesInBatches(newDependencies);
            }
        }
        for (Map.Entry<JSONObject, List<VulnerabilityInfo>> entry : vulnerabilityMap.entrySet()) {
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