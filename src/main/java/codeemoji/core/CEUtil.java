package codeemoji.core;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class CEUtil {

    public static boolean isPreviewEditor(@NotNull Editor editor) {
        return editor.getEditorKind().name().equalsIgnoreCase("UNTYPED");
    }

    public static @NotNull String generateEmoji(int codePoint, int modifier, boolean addColor) {
        char[] codePointChar = Character.toChars(codePoint);
        char[] withoutColor = codePointChar;
        if (modifier > 0) {
            char[] modifierChar = Character.toChars(modifier);
            withoutColor = Arrays.copyOf(codePointChar, codePointChar.length + modifierChar.length);
            System.arraycopy(modifierChar, 0, withoutColor, codePointChar.length, modifierChar.length);
        }
        if (addColor) {
            char[] addColorChar = Character.toChars(0x0FE0F);
            char[] withColor = Arrays.copyOf(withoutColor, withoutColor.length + addColorChar.length);
            System.arraycopy(addColorChar, 0, withColor, withoutColor.length, addColorChar.length);
            return new String(withColor);
        }
        return new String(withoutColor);
    }

    public static boolean isIterableType(PsiTypeElement typeElement, @NotNull PsiJavaFile javaFile) {
        try {
            String simpleName = Objects.requireNonNull(typeElement).getText();
            PsiImportList importList = javaFile.getImportList();
            PsiImportStatementBase[] imps = Objects.requireNonNull(importList).getAllImportStatements();
            for (PsiImportStatementBase imp : imps) {
                PsiJavaCodeReferenceElement refElement = Objects.requireNonNull(imp.getImportReference());
                String refName = refElement.getReferenceName();
                String qualifiedName = refElement.getQualifiedName();
                int genericType = simpleName.indexOf("<");
                if (genericType >= 0) {
                    simpleName = simpleName.substring(0, genericType);
                }
                if (Objects.equals(refName, simpleName)) {
                    if (Iterable.class.isAssignableFrom(Class.forName(qualifiedName))) {
                        return true;
                    }
                    break;
                }
            }
        } catch (RuntimeException | ClassNotFoundException ignored) {
        }
        return false;
    }

    public static boolean isArrayType(@Nullable PsiTypeElement typeElement) {
        try {
            String returnClassSimpleName = Objects.requireNonNull(typeElement).getText();
            return returnClassSimpleName.contains("[]");
        } catch (RuntimeException ignored) {
        }
        return false;
    }
}
