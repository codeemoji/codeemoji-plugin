package codeemoji.core.collector.project;

import codeemoji.core.collector.CECollectorImpl;
import codeemoji.core.collector.project.config.CEProjectConfigFile;
import codeemoji.core.collector.project.config.CEProjectRule;
import codeemoji.core.collector.project.config.CEProjectRuleElement;
import codeemoji.core.collector.project.config.CEProjectRuleFeature;
import codeemoji.core.util.CEEnumUtils;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static codeemoji.core.collector.project.config.CEProjectRuleFeature.ANNOTATIONS;

@Getter
public abstract class CEProjectCollector<H extends PsiModifierListOwner, A extends PsiElement> extends CECollectorImpl<A>
        implements CEIProjectAnnotations<H, A> {

    protected final CEProjectConfigFile configFile;
    protected final String mainKeyId;
    private final String annotationsKey;
    private final CESymbol annotationsSymbol;

    protected CEProjectCollector(@NotNull Editor editor, @NotNull String mainKeyId) {
        super(editor);
        this.configFile = new CEProjectConfigFile(editor.getProject());
        this.mainKeyId = "inlay." + mainKeyId;
        annotationsKey = getMainKeyId() + "." + ANNOTATIONS.getValue() + ".tooltip";
        annotationsSymbol = new CESymbol();
    }

    @Override
    public Map<CEProjectRuleFeature, List<String>> readRules(@NotNull CEProjectRuleElement elementRule) {
        Map<CEProjectRuleFeature, List<String>> result = new EnumMap<>(CEProjectRuleFeature.class);
        for (CEProjectRule rule : configFile.getProjectRules()) {
            CEProjectRuleElement element = CEEnumUtils.getEnumByValue(CEProjectRuleElement.class, rule.element());
            CEProjectRuleFeature feature = CEEnumUtils.getEnumByValue(CEProjectRuleFeature.class, rule.feature());
            if (element != null && feature != null && (element.equals(elementRule))) {
                result.put(feature, rule.values());
            }
        }
        return result;
    }

    @Override
    public Object getConfig(String key) {
        return configFile.getProjectConfigs().get(key);
    }

    public abstract void processHint(@NotNull A addHintElement, @NotNull H evaluationElement, @NotNull InlayHintsSink sink);

    public void addInlayAnnotationsFR(@NotNull A addHintElement, @NotNull List<String> hintValues, @NotNull InlayHintsSink sink) {
        if (!hintValues.isEmpty()) {
            InlayPresentation inlay = buildInlay(getAnnotationsSymbol(), getAnnotationsKey(), String.valueOf(hintValues));
            addInlay(addHintElement, sink, inlay);
        }
    }
}