package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEProjectRuleElement;
import codeemoji.core.collector.project.config.CEProjectRuleFeature;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface CEIProjectConfigFile {

    Map<CEProjectRuleFeature, List<String>> readRules(@NotNull CEProjectRuleElement elementRule);

    Object getConfig(String key);
}
