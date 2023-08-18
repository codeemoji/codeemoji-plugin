package codeemoji.core.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.psi.PsiModifier.*;

public final class CEUtils {

    private CEUtils() {
    }

    public static boolean isNotPreviewEditor(@NotNull Editor editor) {
        return !editor.getEditorKind().name().equalsIgnoreCase("UNTYPED");
    }

    public static boolean isIterableType(PsiTypeElement typeElement) {
        try {
            PsiType fieldType = Objects.requireNonNull(typeElement).getType();
            if (fieldType instanceof PsiClassType psiType) {
                PsiClass psiTypeClass = Objects.requireNonNull(psiType.resolve());
                String qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
                return isIterableType(qualifiedName, psiTypeClass);
            }
        } catch (RuntimeException ignored) {
        }
        return false;
    }

    private static boolean isIterableType(@NotNull String qualifiedName, @NotNull PsiClass psiTypeClass) {
        try {
            Class<?> typeClass = Class.forName(qualifiedName);
            return Iterable.class.isAssignableFrom(typeClass);
        } catch (RuntimeException | ClassNotFoundException ignored) {
            Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
            for (Project proj : openProjects) {
                GlobalSearchScope scope = psiTypeClass.getResolveScope();
                PsiClass psiUserClass = JavaPsiFacade.getInstance(proj).findClass(qualifiedName, scope);
                PsiClassType iteratorType = JavaPsiFacade.getElementFactory(proj).createTypeByFQClassName("java.lang.Iterable", scope);
                PsiClass iteratorClass = iteratorType.resolve();
                if (iteratorClass != null && psiUserClass != null && psiUserClass.isInheritor(iteratorClass, true)) {
                    return true;
                }
            }
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean sameNameAsType(PsiTypeElement typeElement, String fieldName) {
        if (fieldName != null) {
            try {
                String typeName = Objects.requireNonNull(typeElement).getType().getPresentableText();
                int index = typeName.indexOf("<");
                if (index > 0) {
                    typeName = typeName.substring(0, index);
                }
                typeName = calcLastWordCapitalized(typeName);
                fieldName = calcLastWordCapitalized(fieldName);
                return fieldName.equalsIgnoreCase(typeName);
            } catch (RuntimeException ignored) {
            }
        }
        return false;
    }

    private static String calcLastWordCapitalized(@NotNull String word) {
        String[] words = word.split("(?=[A-Z])");
        return words[words.length - 1];
    }

    public static boolean isPluralForm(String name) {
        if (name != null && name.trim().length() > 1) {
            String word = identifyLastWordWithUpperCase(name);
            if (isSuffixWhiteList(name)) {
                return false;
            }
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
        ClassLoader classLoader = CEUtils.class.getClassLoader();
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
        String[] pluralPatterns = {
                ".*s$", ".*[aeiou]ys$", ".*[^s]ses$", ".*[^z]zes$", ".*[^i]xes$",
                ".*[cs]hes$", ".*[^aeiou]ies$", ".*[^aeiou]ices$", ".*[aeiou]es$",
                ".*[^aeiou]ves$", ".*[^aeiou]a$", ".*[^aeiou]i$", ".*[^aeiou]ae$"
        };
        for (String pattern : pluralPatterns) {
            Pattern pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
            Matcher mat = pat.matcher(word);
            if (mat.matches()) {
                return true;
            }
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
    public static boolean isGenericType(@NotNull PsiElement element, PsiTypeElement typeElement) {
        PsiTypeParameterList tpl = searchGenericTypesList(element);
        if (tpl != null) {
            PsiTypeParameter[] tps = tpl.getTypeParameters();
            for (PsiTypeParameter tp : tps) {
                if (typeElement.getText().equalsIgnoreCase(tp.getText())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Nullable
    private static PsiTypeParameterList searchGenericTypesList(@NotNull PsiElement son) {
        PsiTypeParameterList result = null;
        PsiElement father = son.getParent();
        if (father instanceof PsiClass clazz) {
            result = clazz.getTypeParameterList();
        } else if (father instanceof PsiParameterList plist) {
            PsiElement gramFather = plist.getParent();
            if (gramFather instanceof PsiMethod method) {
                PsiElement greatGramFather = method.getParent();
                if (greatGramFather instanceof PsiClass clazz) {
                    result = clazz.getTypeParameterList();
                }
            }
        }
        return result;
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

    public static boolean checkDefaultModifier(@NotNull PsiModifierList psiModifierList) {
        return !(psiModifierList.hasModifierProperty(PUBLIC) ||
                psiModifierList.hasModifierProperty(PROTECTED) ||
                psiModifierList.hasModifierProperty(PRIVATE));
    }

    public static boolean hasAUniqueQualifier(@NotNull PsiReferenceExpression expression) {
        return !expression.getText().contains(".");
    }

    private static boolean isSuffixWhiteList(@NotNull String name) {
        String formattedName = name.trim().toLowerCase();
        if (formattedName.isEmpty()) {
            return false;
        }
        //TODO: implement configuration options
        String[] whiteListWords = {"class", "list", "is"};
        for (String word : whiteListWords) {
            if (formattedName.endsWith(word)) {
                return true;
            }
        }
        return false;
    }

    public static @Nullable String resolveQualifiedName(@NotNull PsiClassType psiType) {
        try {
            PsiClass psiTypeClass = Objects.requireNonNull(psiType.resolve());
            String qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
            return resolveQualifiedName(qualifiedName, psiTypeClass);
        } catch (RuntimeException ignored) {
            return psiType.getName();
        }
    }

    private static @Nullable String resolveQualifiedName(@NotNull String qualifiedName, @NotNull PsiClass psiTypeClass) {
        try {
            Class<?> typeClass = Class.forName(qualifiedName);
            return typeClass.getCanonicalName();
        } catch (RuntimeException | ClassNotFoundException ignored) {
            Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
            for (Project proj : openProjects) {
                GlobalSearchScope scope = psiTypeClass.getResolveScope();
                PsiClass psiUserClass = JavaPsiFacade.getInstance(proj).findClass(qualifiedName, scope);
                if (psiUserClass != null) {
                    return psiUserClass.getQualifiedName();
                }
            }
            return null;
        }
    }
}
