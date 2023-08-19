package codeemoji.core.collector.project;

import codeemoji.core.collector.CECollector;
import codeemoji.core.collector.project.config.CEConfigFile;
import codeemoji.core.collector.project.config.CEElementRule;
import codeemoji.core.collector.project.config.CEFeatureRule;
import codeemoji.core.collector.project.config.CEProjectRule;
import codeemoji.core.util.CEEnumUtils;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static codeemoji.core.collector.project.config.CEFeatureRule.ANNOTATIONS;

@Getter
public abstract class CEProjectCollector<H extends PsiModifierListOwner, A extends PsiElement> extends CECollector<A> {

    protected final CEConfigFile configFile;
    private final String tooltipKeyAnnotations;
    private final CESymbol symbolAnnotations;

    protected CEProjectCollector(@NotNull Editor editor) {
        super(editor);
        this.configFile = new CEConfigFile(editor);
        tooltipKeyAnnotations = "";
        symbolAnnotations = new CESymbol();
    }

    public Map<CEFeatureRule, List<String>> getRules(@NotNull CEElementRule elementRule) {
        Map<CEFeatureRule, List<String>> result = new EnumMap<>(CEFeatureRule.class);
        for (CEProjectRule rule : configFile.getProjectRules()) {
            CEElementRule element = CEEnumUtils.getEnumByValue(CEElementRule.class, rule.element());
            CEFeatureRule feature = CEEnumUtils.getEnumByValue(CEFeatureRule.class, rule.feature());
            if (element != null && feature != null && (element.equals(elementRule))) {
                result.put(feature, rule.values());
            }
        }
        return result;
    }

    public Object getConfig(String key) {
        return configFile.getProjectConfigs().get(key);
    }

    public void processHintAnnotations(@NotNull CEElementRule elementRule, @NotNull A hintElement, @NotNull H evaluationElement, @NotNull InlayHintsSink sink) {
        Map<CEFeatureRule, List<String>> rules = getRules(elementRule);
        List<String> featureValues = rules.get(ANNOTATIONS);
        List<String> hintValues = new ArrayList<>();
        PsiAnnotation[] annotations = evaluationElement.getAnnotations();
        for (PsiAnnotation type : annotations) {
            for (String value : featureValues) {
                String qualifiedName = type.getQualifiedName();
                if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                    hintValues.add(value);
                }
            }
        }
        if (!hintValues.isEmpty()) {
            InlayPresentation inlay = buildInlay(getSymbolAnnotations(), getTooltipKeyAnnotations(), String.valueOf(hintValues));
            addInlay(hintElement, sink, inlay);
        }
    }

    public abstract void checkHint(@NotNull A hintElement, @NotNull H evaluationElement, @NotNull InlayHintsSink sink);

}