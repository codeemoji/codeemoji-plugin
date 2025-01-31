package codeemoji.core.collector;

import codeemoji.core.external.CEExternalAnalyzer;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.declarative.*;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiJavaFile;
import kotlin.Unit;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
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
        //TODO: review and see if passing editor is needed. also check for null
        if (isEnabled()) {
            if (psiElement instanceof PsiJavaFile) {
                psiElement.accept(createElementVisitor(getEditor(), inlayTreeSink));
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

    public final void addInlayInline(@Nullable A element, @NotNull InlayTreeSink sink, @NotNull InlayVisuals inlay) {
        if (null != element) {
            int lineNumber = getEditor().getDocument().getLineNumber(element.getTextRange().getEndOffset());
            var localVariable = element.getParent();

            //TODO: implement
            sink.addPresentation(
                    new EndOfLinePosition(lineNumber),
                    List.of(new InlayPayload("variablePayload", new StringInlayActionPayload("Go to Settings"))),

                    CEBundle.getString(inlay.tooltip()),  // Tooltip
                    true,  // Has background
                    builder -> {
                        builder.text("a" + inlay.text(),
                                null);
                        return Unit.INSTANCE;
                    } // Presentation content
            );
            //sink.addInlineElement(calcOffset(element), false, text, false);
        }
    }



    public final void addInlayBlock(@Nullable A element, @NotNull InlayTreeSink sink, InlayVisuals inlay) {
        if (null != element) {
            var indentFactor = EditorUtil.getPlainSpaceWidth(getEditor());
            var indent = EditorUtil.getTabSize(getEditor()) * indentFactor;
           // inlay = getFactory().inset(inlay, indent, 0, 0, 0);

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

    //TODO: merge and refactor these

    public InlayVisuals buildInlayWithEmoji(@NotNull CESymbol symbol, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return formatInlay(symbol.getEmoji(), symbol.isWithBackground(), keyTooltip, suffixTooltip);
    }

    protected @NotNull InlayVisuals buildInlayWithText(@NotNull String fullText, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return formatInlay(fullText, true, keyTooltip, suffixTooltip);
    }

    // is tooltip suffix needed?
    private @NotNull InlayVisuals formatInlay(@NotNull String symbol, boolean background,
                                              @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        var tooltip = getTooltip(keyTooltip);
        if (null != suffixTooltip) {
            tooltip += " " + suffixTooltip;
        }
        return InlayVisuals.of(symbol, tooltip, background);
    }

    private static String getTooltip(@NotNull String key) {
        try {
            return CEBundle.getString(key);
        } catch (RuntimeException ex) {
            return key;
        }
    }

}