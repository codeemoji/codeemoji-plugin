package codeemoji.core.collector.project;

import codeemoji.core.collector.CECollectorImpl;
import codeemoji.core.collector.project.config.CEConfigFile;
import codeemoji.core.collector.project.config.CEElementRule;
import codeemoji.core.collector.project.config.CEFeatureRule;
import codeemoji.core.collector.project.config.CEProjectRule;
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

import static codeemoji.core.collector.project.config.CEFeatureRule.ANNOTATIONS;

@Getter
public abstract class CEProjectCollector<H extends PsiModifierListOwner, A extends PsiElement> extends CECollectorImpl<A>
        implements ICEProjectAnnotations<H, A> {

    protected final CEConfigFile configFile;
    protected final String mainKeyId;
    private final String annotationsKey;
    private final CESymbol annotationsSymbol;

    protected CEProjectCollector(@NotNull Editor editor, @NotNull String mainKeyId) {
        super(editor);
        this.configFile = new CEConfigFile(editor);
        this.mainKeyId = "inlay." + mainKeyId;
        annotationsKey = getMainKeyId() + "." + ANNOTATIONS.getValue() + ".tooltip";
        annotationsSymbol = new CESymbol();
    }

    @Override
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