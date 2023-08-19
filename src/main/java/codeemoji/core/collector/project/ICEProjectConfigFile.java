package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEElementRule;
import codeemoji.core.collector.project.config.CEFeatureRule;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface ICEProjectConfigFile {

    Map<CEFeatureRule, List<String>> getRules(@NotNull CEElementRule elementRule);

    Object getConfig(String key);
}
