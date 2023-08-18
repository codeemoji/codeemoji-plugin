package codeemoji.core.collector.project.config;

import com.google.gson.Gson;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@Getter
public class CEConfigFile {

    public static final String PROJECT_CONFIG_FILE = "codeemoji.json";
    public static final String PROJECT_RULES = "project_rules";

    private final Map<String, Object> projectConfigs = new HashMap<>();
    private final List<CEProjectRule> projectRules = new ArrayList<>();

    public CEConfigFile(@NotNull Editor editor) {
        read(editor);
    }

    private void read(@NotNull Editor editor) {
        try {
            VirtualFile baseDir = editor.getProject().getBaseDir();
            VirtualFile file = baseDir.findFileByRelativePath(PROJECT_CONFIG_FILE);
            if (file != null) {
                try (InputStream is = file.getInputStream()) {
                    try (Reader isr = new InputStreamReader(is)) {
                        Gson gson = new Gson();
                        @SuppressWarnings("unchecked") Map<String, Object> map = gson.fromJson(isr, Map.class);
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
            }
        } catch (RuntimeException | IOException ex) {
            ex.printStackTrace();
        }
    }

}
