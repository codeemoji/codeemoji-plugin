package codeemoji.core.collector.project;

import codeemoji.core.collector.CECollector;
import codeemoji.core.collector.config.CEConfigFile;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.collector.config.CERuleFeature.ANNOTATIONS;
import static codeemoji.core.collector.project.ProjectRuleSymbol.ANNOTATIONS_SYMBOL;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract sealed class CEProjectCollector<H extends PsiModifierListOwner, A extends PsiElement> extends CECollector<A>
        implements CEProjectInterface<H, A> permits CEProjectClassCollector, CEProjectMethodCollector, CEProjectVariableCollector {

    protected final CEConfigFile configFile;
    protected final String mainKeyId;
    private final String annotationsKey;
    private final CESymbol annotationsSymbol;

    protected CEProjectCollector(@NotNull Editor editor, @NotNull String mainKeyId) {
        super(editor);
        this.configFile = new CEConfigFile(editor.getProject());
        this.mainKeyId = "inlay." + mainKeyId;
        annotationsKey = getMainKeyId() + "." + ANNOTATIONS.getValue() + ".tooltip";
        annotationsSymbol = ANNOTATIONS_SYMBOL;
    }

    @SuppressWarnings("unused")
    @Override
    public Object readConfig(String key) {
        return configFile.getConfigs().get(key);
    }

    @SuppressWarnings("unused")
    public abstract void processHint(@NotNull A addHintElement, @NotNull H evaluationElement, @NotNull InlayHintsSink sink);

    @Override
    public void addInlayAnnotationsFR(@NotNull A addHintElement, @NotNull List<String> hintValues, @NotNull InlayHintsSink sink) {
        if (!hintValues.isEmpty()) {
            var inlay = buildInlayWithEmoji(getAnnotationsSymbol(), getAnnotationsKey(), String.valueOf(hintValues));
            addInlayInline(addHintElement, sink, inlay);
        }
    }
}