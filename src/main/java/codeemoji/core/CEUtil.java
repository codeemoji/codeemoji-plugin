package codeemoji.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CEUtil {

    public static boolean isNotPreviewEditor(@NotNull Editor editor) {
        return !editor.getEditorKind().name().equalsIgnoreCase("UNTYPED");
    }

    @Contract("_, _, _ -> new")
    public static @NotNull String generateEmoji(int codePoint, int modifier, boolean addColor) {
        char[] codePointChars = Character.toChars(codePoint);
        char[] withoutColorChars = codePointChars;
        if (modifier > 0) {
            char[] modifierChars = Character.toChars(modifier);
            withoutColorChars = Arrays.copyOf(codePointChars, codePointChars.length + modifierChars.length);
            System.arraycopy(modifierChars, 0, withoutColorChars, codePointChars.length, modifierChars.length);
        }
        if (addColor) {
            char[] addColorChars = Character.toChars(0x0FE0F);
            char[] withColorChars = Arrays.copyOf(withoutColorChars, withoutColorChars.length + addColorChars.length);
            System.arraycopy(addColorChars, 0, withColorChars, withoutColorChars.length, addColorChars.length);
            return new String(withColorChars);
        }
        return new String(withoutColorChars);
    }

    public static boolean isIterableType(PsiTypeElement typeElement) {
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

    public static boolean isArrayType(PsiTypeElement typeElement) {
        try {
            String returnClassSimpleName = Objects.requireNonNull(typeElement).getText();
            return returnClassSimpleName.contains("[]");
        } catch (RuntimeException ignored) {
        }
        return false;
    }

    public static boolean sameNameAsType(PsiTypeElement typeElement, String fieldName) {
        if (fieldName != null) {
            try {
                String typeName = Objects.requireNonNull(typeElement).getType().getPresentableText();
                int index = typeName.indexOf("<");
                if (index > 0) {
                    typeName = typeName.substring(0, index);
                }
                return fieldName.equalsIgnoreCase(typeName);
            } catch (RuntimeException ignored) {
            }
        }
        return false;
    }

    public static boolean isPluralForm(String name) {
        if (name != null && name.trim().length() > 1) {
            String word = identifyLastWordWithUpperCase(name);
            if (isIrregularPluralForm(word)) {
                return true;
            } else return isCommonPluralForm(word);
        }
        return false;
    }

    private static String identifyLastWordWithUpperCase(String name) {
        String result = null;
        Pattern pattern = Pattern.compile("\\b[A-Z][a-zA-Z]*\\b");
        Matcher matcher = pattern.matcher(name);
        while (matcher.find()) {
            result = matcher.group();
        }
        return (result != null) ? result : name;
    }

    private static boolean isIrregularPluralForm(String word) {
        ClassLoader classLoader = CEUtil.class.getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("irregular_plural.json")) {
            if (is != null) {
                Reader reader = new InputStreamReader(is);
                JsonElement je = new Gson().fromJson(reader, JsonObject.class).get(word.trim().toLowerCase());
                if (je != null) {
                    return je.getAsString() != null;
                }
            }
        } catch (RuntimeException | IOException ignored) {
        }
        return false;
    }

    private static boolean isCommonPluralForm(String word) {
        if (isSuffixWhiteList(word)) {
            return false;
        }
        String[] pluralPatterns = {
                ".*s$", ".*[aeiou]ys$", ".*[^s]ses$", ".*[^z]zes$", ".*[^i]xes$",
                ".*[cs]hes$", ".*[^aeiou]ies$", ".*[^aeiou]ices$", ".*[aeiou]es$",
                ".*[^aeiou]ves$", ".*[^aeiou]a$", ".*[^aeiou]i$", ".*[^aeiou]ae$"
        };
        for (String pattern : pluralPatterns) {
            Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher mat = pat.matcher(word);
            return mat.matches();
        }
        return false;
    }

    public static boolean containsOnlySpecialCharacters(String name) {
        String regex = "^[^a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isGenericType(PsiTypeElement typeElement) {
        //TODO: Implement
        return false;
    }

    public static boolean isNumericType(@NotNull PsiTypeElement typeElement) {
        PsiType type = typeElement.getType();
        return PsiTypes.intType().isAssignableFrom(type)
                || PsiTypes.longType().isAssignableFrom(type)
                || PsiTypes.floatType().isAssignableFrom(type)
                || PsiTypes.doubleType().isAssignableFrom(type)
                || PsiTypes.byteType().isAssignableFrom(type)
                || PsiTypes.shortType().isAssignableFrom(type);
    }

    public static @Nullable PsiElement identifyFirstQualifier(@NotNull PsiElement element) {
        PsiElement child = element.getFirstChild();
        if (child != null && (!(child instanceof PsiReferenceExpression) || child.getChildren().length > 0)) {
            if (child instanceof PsiReferenceParameterList) {
                return element;
            }
            return identifyFirstQualifier(child);
        }
        return child;
    }

    public static boolean hasAUniqueQualifier(@NotNull PsiReferenceExpression expression) {
        return !expression.getText().contains(".");
    }

    private static boolean isSuffixWhiteList(@NotNull String name) {
        String formattedName = name.trim().toLowerCase();
        if (formattedName.isEmpty()) {
            return false;
        }
        //TODO: implement configuration option
        String[] whiteListWords = {"class", "list", "is"};
        for (String word : whiteListWords) {
            if (formattedName.endsWith(word)) {
                return true;
            }
        }
        return false;
    }
}
