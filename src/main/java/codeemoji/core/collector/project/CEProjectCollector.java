package codeemoji.core.collector.project;

import codeemoji.core.collector.CECollector;
import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.config.CEConfigFile;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static codeemoji.core.collector.project.ProjectRuleSymbol.ANNOTATIONS_SYMBOL;
import static codeemoji.core.config.CERuleFeature.ANNOTATIONS;

@Getter
public abstract class CEProjectCollector<H extends PsiModifierListOwner, A extends PsiElement> extends CECollector<H, A>
        implements CEProject<H, A> {

    protected final @NotNull CEConfigFile configFile;
    protected final @NotNull String mainKeyId;
    private final @NotNull String annotationsKey;
    private final @NotNull CESymbol annotationsSymbol;

    CEProjectCollector(@NotNull Editor editor, @NotNull String key, @NotNull String mainKeyId) {
        super(editor, key);
      this.  configFile = new CEConfigFile(editor.getProject());
        this.mainKeyId = "inlay." + mainKeyId;
        annotationsKey = getMainKeyId() + "." + ANNOTATIONS.getValue() + ".tooltip";
        annotationsSymbol = ANNOTATIONS_SYMBOL;
    }

    //No-op
    @Override
    protected @Nullable InlayVisuals createInlayFor(@NotNull H element) {
        return null;
    }

    @SuppressWarnings("unused")
    @Override
    public Object readConfig(String key) {
        return configFile.getConfigs().get(key);
    }

    @SuppressWarnings("unused")
    protected abstract void processHint(@NotNull A addHintElement, @NotNull H evaluationElement, @NotNull InlayTreeSink sink);

    @Override
    public void addInlayAnnotationsFR(@NotNull A addHintElement, @NotNull List<String> hintValues, @NotNull InlayTreeSink sink) {
        if (!hintValues.isEmpty()) {
            var inlay = buildInlayWithEmoji(getAnnotationsSymbol(), getAnnotationsKey(), String.valueOf(hintValues));
            addInlayInline(addHintElement, sink, inlay);
        }
    }
}