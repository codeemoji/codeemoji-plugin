package codeemoji.core.collector;

import codeemoji.core.external.CEExternalAnalyzer;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.declarative.EndOfLinePosition;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJavaFile;
import kotlin.Unit;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CECollector<H extends PsiElement, A extends PsiElement> extends CEInlayBuilder implements SharedBypassCollector {

    protected CECollector(Editor editor, SettingsKey<?> settingsKey) {
        super(editor, settingsKey);
    }

    public abstract PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink);

    //TODO: ideally just this second one should be abstract. obe above should be split into a method that gathers stuff fed to create inlay
    @Nullable
    protected abstract InlayPresentation createInlayFor(@NotNull H element);

    @Override
    public void collectFromElement(@NotNull PsiElement psiElement, @NotNull InlayTreeSink inlayTreeSink) {
        Editor editor = FileEditorManager.getInstance(psiElement.getProject()).getSelectedTextEditor();
        //TODO: review and see if passing editor is needed. also check for null
        if (isEnabled()) {
            if (psiElement instanceof PsiJavaFile) {
                psiElement.accept(createElementVisitor(editor, inlayTreeSink));
            }
        }
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

    public final void addInlayInline(@Nullable A element, @NotNull InlayTreeSink sink, @NotNull InlayPresentation inlay) {
        if (null != element) {
            //TODO: implement
            sink.addPresentation(
                    new EndOfLinePosition(0),
                    null,  // No payloads
                    "Parameter: " + inlay.toString(),  // Tooltip
                    true,  // Has background
                    builder -> {
                        builder.text("a" + inlay.toString(),
                                null);
                        return Unit.INSTANCE;
                    } // Presentation content
            );
            //sink.addInlineElement(calcOffset(element), false, inlay, false);
        }
    }

    public final void addInlayBlock(@Nullable A element, @NotNull InlayTreeSink sink, InlayPresentation inlay) {
        if (null != element) {
            var indentFactor = EditorUtil.getPlainSpaceWidth(getEditor());
            var indent = EditorUtil.getTabSize(getEditor()) * indentFactor;
            inlay = getFactory().inset(inlay, indent, 0, 0, 0);

            //TODO: implement
            //sink.addBlockElement(element.getTextOffset(), true, true, 0, inlay);
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