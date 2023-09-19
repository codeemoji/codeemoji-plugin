package codeemoji.core.config;

import com.google.gson.GsonBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class CEConfigFile {

    public static final String FILE = "codeemoji.json";
    public static final String JSON_HEADER = "project_rules";
    private static final Logger LOG = Logger.getInstance(CEConfigFile.class);
    private final Map<String, Object> configs = new HashMap<>();
    private final List<CERule> rules = new ArrayList<>();

    public CEConfigFile(@Nullable Project project) {
        try {
            var contentRoots = ProjectRootManager.getInstance(Objects.requireNonNull(project)).getContentRoots();
            for (var baseDir : contentRoots) {
                var file = baseDir.findFileByRelativePath(FILE);
                if (null != file) {
                    try (var is = file.getInputStream()) {
                        try (Reader inputStreamReader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

                            var gson = new GsonBuilder()
                                    .registerTypeAdapter(CERuleElement.class, new CERuleElement.EnumDeserializer())
                                    .registerTypeAdapter(CERuleFeature.class, new CERuleFeature.EnumDeserializer())
                                    .create();

                            @SuppressWarnings("unchecked") Map<String, Object> map = gson.fromJson(inputStreamReader, Map.class);
                            for (var entry : map.entrySet()) {
                                if (Objects.equals(entry.getKey(), JSON_HEADER)) {
                                    var readRules = gson.fromJson(String.valueOf(entry.getValue()), CERule[].class);
                                    Collections.addAll(rules, readRules);
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
