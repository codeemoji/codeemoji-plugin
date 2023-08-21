package codeemoji.core.collector.project.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
public class CEConfigFile {

    public static final String FILE = "codeemoji.json";
    public static final String JSON_HEADER = "project_rules";
    private static final Logger LOG = Logger.getInstance(CEConfigFile.class);
    private final Map<String, Object> configs = new HashMap<>();
    private final List<CERule> rules = new ArrayList<>();

    public CEConfigFile(@Nullable Project project) {
        try {
            VirtualFile[] contentRoots = ProjectRootManager.getInstance(Objects.requireNonNull(project)).getContentRoots();
            for (VirtualFile baseDir : contentRoots) {
                VirtualFile file = baseDir.findFileByRelativePath(FILE);
                if (file != null) {
                    try (InputStream is = file.getInputStream()) {
                        try (Reader inputStreamReader = new InputStreamReader(is)) {

                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(CERuleElement.class, new CERuleElement.EnumDeserializer())
                                    .registerTypeAdapter(CERuleFeature.class, new CERuleFeature.EnumDeserializer())
                                    .create();

                            @SuppressWarnings("unchecked") Map<String, Object> map = gson.fromJson(inputStreamReader, Map.class);
                            for (Map.Entry<String, Object> entry : map.entrySet()) {
                                if (Objects.equals(entry.getKey(), JSON_HEADER)) {
                                    CERule[] readRules = gson.fromJson(String.valueOf(entry.getValue()), CERule[].class);
                                    Collections.addAll(this.rules, readRules);
                                } else {
                                    configs.put(entry.getKey(), entry.getValue());
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
