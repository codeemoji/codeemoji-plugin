package codeemoji.core.collector.project.config;

import com.google.gson.Gson;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@Getter
public class CEProjectConfigFile {

    public static final String PROJECT_CONFIG_FILE = "codeemoji.json";
    public static final String PROJECT_RULES = "project_rules";
    private static final Logger LOG = Logger.getInstance(CEProjectConfigFile.class);
    private final Map<String, Object> projectConfigs = new HashMap<>();
    private final List<CEProjectRule> projectRules = new ArrayList<>();

    public CEProjectConfigFile(@Nullable Project project) {
        try {
            VirtualFile[] contentRoots = ProjectRootManager.getInstance(Objects.requireNonNull(project)).getContentRoots();
            for (VirtualFile baseDir : contentRoots) {
                VirtualFile file = baseDir.findFileByRelativePath(PROJECT_CONFIG_FILE);
                if (file != null) {
                    try (InputStream is = file.getInputStream()) {
                        try (Reader inputStreamReader = new InputStreamReader(is)) {
                            Gson gson = new Gson();
                            @SuppressWarnings("unchecked") Map<String, Object> map = gson.fromJson(inputStreamReader, Map.class);
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if (Objects.equals(entry.getKey(), PROJECT_RULES)) {
                                    CEProjectRule[] rules = gson.fromJson(String.valueOf(entry.getValue()), CEProjectRule[].class);
                                    Collections.addAll(projectRules, rules);
                                } else {
                                    projectConfigs.put(entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    }
                    break;
                }
            }
        } catch (RuntimeException | IOException ex) {
            LOG.info(ex);
        }
    }

}
