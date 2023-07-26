package codeemoji.core;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

import static codeemoji.core.CESymbol.COLOR_BACKGROUND;

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
            char[] addColorChar = Character.toChars(COLOR_BACKGROUND.getValue());
            char[] withColor = Arrays.copyOf(withoutColor, withoutColor.length + addColorChar.length);
            System.arraycopy(addColorChar, 0, withColor, withoutColor.length, addColorChar.length);
            return new String(withColor);
        }
        return new String(withoutColor);
    }

    public static boolean isIterableType(@Nullable PsiTypeElement typeElement) {
        try {
            PsiType fieldType = Objects.requireNonNull(typeElement).getType();
            if (fieldType instanceof PsiClassType psiType) {
                PsiClass psiTypeClass = Objects.requireNonNull(psiType.resolve());
                String qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
                try {
                    Class<?> typeClass = Class.forName(qualifiedName);
                    return Iterable.class.isAssignableFrom(typeClass);
                } catch (RuntimeException | ClassNotFoundException ignored) {
                    Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
                    for (Project proj : openProjects) {
                        Project project = typeElement.getProject();
                        GlobalSearchScope scope = psiTypeClass.getResolveScope();
                        PsiClass psiUserClass = JavaPsiFacade.getInstance(project).findClass(qualifiedName, scope);
                        PsiClassType iteratorType = JavaPsiFacade.getElementFactory(project).createTypeByFQClassName("java.lang.Iterable", scope);
                        PsiClass iteratorClass = iteratorType.resolve();
                        if (iteratorClass != null && psiUserClass != null && psiUserClass.isInheritor(iteratorClass, true)) {
                            return true;
                        }
                    }
                }
            }
        } catch (RuntimeException ignored) {
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
