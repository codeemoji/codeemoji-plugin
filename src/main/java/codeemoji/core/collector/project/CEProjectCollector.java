package codeemoji.core.collector.project;

import codeemoji.core.collector.CECollector;
import codeemoji.core.collector.project.config.CEConfigFile;
import codeemoji.core.collector.project.config.CEElementRule;
import codeemoji.core.collector.project.config.CEFeatureRule;
import codeemoji.core.collector.project.config.CEProjectRule;
import codeemoji.core.util.CEEnumUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJvmModifiersOwner;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CEProjectCollector<H extends PsiJvmModifiersOwner, A extends PsiElement> extends CECollector<A> {

    CEConfigFile configFile;

    public CEProjectCollector(@NotNull Editor editor) {
        super(editor);
        this.configFile = new CEConfigFile(editor);
    }

    public Map<CEFeatureRule, List<String>> getRules(@NotNull CEElementRule elementRule) {
        Map<CEFeatureRule, List<String>> result = new HashMap<>();
        for (CEProjectRule rule : configFile.getProjectRules()) {
            CEElementRule element = CEEnumUtils.getEnumByValue(CEElementRule.class, rule.element());
            CEFeatureRule feature = CEEnumUtils.getEnumByValue(CEFeatureRule.class, rule.feature());
            if (element != null && feature != null) {
                if (element.equals(elementRule)) {
                    result.put(feature, rule.values());
                }
            }
        }
        return result;
    }

    public Object getConfig(String key) {
        return configFile.getProjectConfigs().get(key);
    }

    public @NotNull List<String> checkHintableAnnotations(@NotNull H element, @NotNull List<String> featureValues) {
        List<String> result = new ArrayList<>();
        PsiAnnotation[] annotations = element.getAnnotations();
        for (PsiAnnotation type : annotations) {
            for (String value : featureValues) {
                String qualifiedName = type.getQualifiedName();
                if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                    result.add(value);
                }
            }
        }
        return result;
    }

}