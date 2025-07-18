package codeemoji.core.collector;

import codeemoji.core.external.CEExternalAnalyzer;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.declarative.InlineInlayPosition;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJavaFile;
import kotlin.Unit;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class CECollector<H extends PsiElement, A extends PsiElement> implements SharedBypassCollector {

    private final Editor editor;
    private final String key;

    protected CECollector(Editor editor, String key) {
        this.editor = editor;
        this.key = key;
    }

    public abstract PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink);

    @Nullable
    protected abstract InlayVisuals createInlayFor(@NotNull H element);

    @Override
    public void collectFromElement(@NotNull PsiElement psiElement, @NotNull InlayTreeSink inlayTreeSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(createElementVisitor(getEditor(), inlayTreeSink));
        }
    }

    public final void addInlayInline(@Nullable A element, @NotNull InlayTreeSink sink, @NotNull InlayVisuals inlay) {
        if (null != element) {
            // Add inlay to editor here
            sink.addPresentation(
                    new InlineInlayPosition(
                            calcOffset(element),
                            true, 0),
                    List.of(),
                    inlay.tooltip(),
                    inlay.hasBackground(),
                    builder -> {
                        builder.text(inlay.text(), null);
                        return Unit.INSTANCE;
                    }
            );

        }
    }


    protected int calcOffset(@Nullable A element) {
        if (null != element) {
            return element.getTextOffset() + element.getTextLength();
        }
        return 0;
    }


    public final void addInlayBlock(@Nullable A element, @NotNull InlayTreeSink sink, InlayVisuals inlay) {
        if (null != element) {
            var indentFactor = EditorUtil.getPlainSpaceWidth(getEditor());
            var indent = EditorUtil.getTabSize(getEditor()) * indentFactor;
            // inlay = getFactory().inset(inlay, indent, 0, 0, 0);

            //TODO: check this
            sink.addPresentation(
                    new InlineInlayPosition(element.getTextRange().getEndOffset() - indent, true, 0),
                    List.of(),
                    inlay.tooltip(),
                    inlay.hasBackground(),
                    builder -> {
                        builder.text(inlay.text(), null);
                        return Unit.INSTANCE;
                    }
            );
        }
    }

    //helper function. we are not feeding this all the time into create inlay
    protected @NotNull Map<?, ?> getExternalInfo(@NotNull H element) {
        Map<?, ?> result = new HashMap<>();
        CEExternalAnalyzer.getInstance().buildExternalInfo(result, element);
        return result;
    }

}