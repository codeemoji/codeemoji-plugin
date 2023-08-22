package codeemoji.core.collector.project;

import codeemoji.core.collector.CECollectorImpl;
import codeemoji.core.collector.project.config.CEConfigFile;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.collector.project.ProjectRuleSymbol.ANNOTATIONS_SYMBOL;
import static codeemoji.core.collector.project.config.CERuleFeature.ANNOTATIONS;

@Getter
public abstract class CEProjectCollector<H extends PsiModifierListOwner, A extends PsiElement> extends CECollectorImpl<A>
        implements CEIProjectAnnotations<H, A> {

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

    @Override
    public Object readConfig(String key) {
        return configFile.getConfigs().get(key);
    }

    public abstract void processHint(@NotNull A addHintElement, @NotNull H evaluationElement, @NotNull InlayHintsSink sink);

    public void addInlayAnnotationsFR(@NotNull A addHintElement, @NotNull List<String> hintValues, @NotNull InlayHintsSink sink) {
        if (!hintValues.isEmpty()) {
            InlayPresentation inlay = buildInlay(getAnnotationsSymbol(), getAnnotationsKey(), String.valueOf(hintValues));
            addInlay(addHintElement, sink, inlay);
        }
    }
}