package codeemoji.core.collector;

import codeemoji.core.external.CEExternalAnalyzer;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJavaFile;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CECollector<H extends PsiElement, A extends PsiElement> extends CEInlayBuilder implements InlayHintsCollector {

    protected CECollector(Editor editor, SettingsKey<?> settingsKey) {
        super(editor, settingsKey);
    }

    public abstract PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink);

    //TODO: ideally just this second one should be abstract. obe above should be split into a method that gathers stuff fed to create inlay
    @Nullable
    protected abstract InlayPresentation createInlayFor(@NotNull H element);

    @Override
    public final boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (isEnabled()) {
            if (psiElement instanceof PsiJavaFile) {
                psiElement.accept(createElementVisitor(editor, inlayHintsSink));
            }
        }
        return false;
    }

    protected int calcOffset(@Nullable A element) {
        if (null != element) {
            return element.getTextOffset() + element.getTextLength();
        }
        return 0;
    }

    protected boolean isEnabled() {
        return true;
    }

    public final void addInlayInline(@Nullable A element, @NotNull InlayHintsSink sink, @NotNull InlayPresentation inlay) {
        if (null != element) {
            sink.addInlineElement(calcOffset(element), false, inlay, false);
        }
    }

    public final void addInlayBlock(@Nullable A element, @NotNull InlayHintsSink sink, InlayPresentation inlay) {
        if (null != element) {
            var indentFactor = EditorUtil.getPlainSpaceWidth(getEditor());
            var indent = EditorUtil.getTabSize(getEditor()) * indentFactor;
            inlay = getFactory().inset(inlay, indent, 0, 0, 0);
            sink.addBlockElement(element.getTextOffset(), true, true, 0, inlay);
        }
    }

    //helper function. we are not feeding this all the time into craete inlay
    protected @NotNull Map<?, ?> getExternalInfo(@Nullable H element) {
        Map<?, ?> result = new HashMap<>();
        if (element != null) {
            CEExternalAnalyzer.getInstance().buildExternalInfo(result, element);
        }
        return result;
    }

}