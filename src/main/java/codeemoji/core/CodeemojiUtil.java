package codeemoji.core;

import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class CodeemojiUtil {

    public static boolean isPreviewEditor(@NotNull Editor editor) {
        return editor.getEditorKind().name().equalsIgnoreCase("UNTYPED");
    }

    private static @NotNull String generateEmoji(int codePoint, boolean addColor) {
        char[] c0 = Character.toChars(codePoint);
        if (!addColor) {
            return new String(c0);
        }
        char[] c1 = Character.toChars(0x0FE0F);
        char[] result = Arrays.copyOf(c0, c0.length + c1.length);
        System.arraycopy(c1, 0, result, c0.length, c1.length);
        return new String(result);
    }


    public static @NotNull InlayPresentation configureInlayHint(PresentationFactory factory, String tooltipText, int codePoint, boolean addColor) {
        var inlay = factory.text(CodeemojiUtil.generateEmoji(codePoint, addColor));
        inlay = factory.roundWithBackgroundAndSmallInset(inlay);
        inlay = factory.withTooltip(tooltipText, inlay);
        return inlay;
    }
}
