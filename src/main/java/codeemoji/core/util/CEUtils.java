package codeemoji.core.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.intellij.psi.PsiModifier.*;

public final class CEUtils {

    private static final Logger LOG = Logger.getInstance(CEUtils.class);

    private CEUtils() {
    }

    public static boolean isNotPreviewEditor(@NotNull Editor editor) {
        return !editor.getEditorKind().name().equalsIgnoreCase("UNTYPED");
    }

    public static boolean isArrayType(PsiTypeElement typeElement) {
        try {
            var returnClassSimpleName = Objects.requireNonNull(typeElement).getText();
            return returnClassSimpleName.contains("[]");
        } catch (RuntimeException ex) {
            LOG.info(ex);
        }
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean sameNameAsType(PsiTypeElement typeElement, String fieldName) {
        if (fieldName != null) {
            try {
                var typeName = Objects.requireNonNull(typeElement).getType().getPresentableText();
                var index = typeName.indexOf("<");
                if (index > 0) {
                    typeName = typeName.substring(0, index);
                }
                typeName = calcLastWordCapitalized(typeName);
                fieldName = calcLastWordCapitalized(fieldName);
                return fieldName.equalsIgnoreCase(typeName);
            } catch (RuntimeException ex) {
                LOG.info(ex);
            }
        }
        return false;
    }

    private static String calcLastWordCapitalized(@NotNull String word) {
        var words = word.split("(?=[A-Z])");
        return words[words.length - 1];
    }

    public static boolean isPluralForm(String name) {
        if (name != null && name.trim().length() > 1) {
            var word = identifyLastWordWithUpperCase(name);
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
        var pattern = Pattern.compile("\\b[A-Z][a-zA-Z]*\\b");
        var matcher = pattern.matcher(name);
        while (matcher.find()) {
            result = matcher.group();
        }
        return (result != null) ? result : name;
    }

    private static boolean isIrregularPluralForm(String word) {
        var classLoader = CEUtils.class.getClassLoader();
        try (var is = classLoader.getResourceAsStream("irregular_plural.json")) {
            if (is != null) {
                Reader reader = new InputStreamReader(is);
                var je = new Gson().fromJson(reader, JsonObject.class).get(word.trim().toLowerCase());
                if (je != null) {
                    return je.getAsString() != null;
                }
            }
        } catch (RuntimeException | IOException ex) {
            LOG.info(ex);
        }
        return false;
    }

    private static boolean isCommonPluralForm(String word) {
        var pattern = ".*s$";
        var pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        var mat = pat.matcher(word);
        return mat.matches();
    }

    public static boolean containsOnlySpecialCharacters(String name) {
        var regex = "^[^a-zA-Z0-9]+$";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(name);
        return matcher.matches();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isGenericType(@NotNull PsiElement element, @Nullable PsiTypeElement typeElement) {
        var tpl = searchGenericTypesList(element);
        if (tpl != null) {
            var tps = tpl.getTypeParameters();
            for (var tp : tps) {
                if (typeElement != null) {
                    if (typeElement.getText().equalsIgnoreCase(tp.getText())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    private static PsiTypeParameterList searchGenericTypesList(@NotNull PsiElement son) {
        PsiTypeParameterList result = null;
        var father = son.getParent();
        if (father instanceof PsiClass clazz) {
            result = clazz.getTypeParameterList();
        } else if (father instanceof PsiParameterList plist) {
            var gramFather = plist.getParent();
            if (gramFather instanceof PsiMethod method) {
                var greatGramFather = method.getParent();
                if (greatGramFather instanceof PsiClass clazz) {
                    result = clazz.getTypeParameterList();
                }
            }
        }
        return result;
    }

    public static boolean isNumericType(@NotNull PsiTypeElement typeElement) {
        var type = typeElement.getType();
        return PsiTypes.intType().isAssignableFrom(type)
                || PsiTypes.longType().isAssignableFrom(type)
                || PsiTypes.floatType().isAssignableFrom(type)
                || PsiTypes.doubleType().isAssignableFrom(type)
                || PsiTypes.byteType().isAssignableFrom(type)
                || PsiTypes.shortType().isAssignableFrom(type);
    }

    @SuppressWarnings("unused")
    public static @Nullable PsiElement identifyFirstQualifier(@NotNull PsiElement element) {
        var child = element.getFirstChild();
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
        var formattedName = name.trim().toLowerCase();
        if (formattedName.isEmpty()) {
            return false;
        }
        //TODO: implement configuration options
        var whiteListWords = new String[]{"class", "list", "is"};
        for (var word : whiteListWords) {
            if (formattedName.endsWith(word)) {
                return true;
            }
        }
        return false;
    }

    public static @Nullable String resolveQualifiedName(@NotNull PsiClassType psiType) {
        try {
            var psiTypeClass = Objects.requireNonNull(psiType.resolve());
            var qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
            return resolveQualifiedName(qualifiedName, psiTypeClass);
        } catch (RuntimeException ignored) {
            return psiType.getName();
        }
    }

    private static @Nullable String resolveQualifiedName(@NotNull String qualifiedName, @NotNull PsiClass psiTypeClass) {
        try {
            var typeClass = Class.forName(qualifiedName);
            return typeClass.getCanonicalName();
        } catch (RuntimeException | ClassNotFoundException ignored) {
            var openProjects = ProjectManager.getInstance().getOpenProjects();
            for (var proj : openProjects) {
                var scope = psiTypeClass.getResolveScope();
                var psiUserClass = JavaPsiFacade.getInstance(proj).findClass(qualifiedName, scope);
                if (psiUserClass != null) {
                    return psiUserClass.getQualifiedName();
                }
            }
            return null;
        }
    }

    public static boolean isConstantName(@NotNull PsiVariable element) {
        return isConstant(element) && Objects.equals(element.getType().getPresentableText(), "String");
    }

    public static boolean isConstant(@NotNull PsiVariable element) {
        var modifierList = element.getModifierList();
        return modifierList != null &&
                modifierList.hasExplicitModifier(STATIC) &&
                modifierList.hasExplicitModifier(FINAL);
    }

    public static boolean isIterableType(PsiTypeElement typeElement) {
        return checkParentType(typeElement, Iterable.class);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isMappableType(PsiTypeElement typeElement) {
        return checkParentType(typeElement, Map.class);
    }

    private static boolean checkParentType(PsiTypeElement typeElement, @NotNull Class<?> parentTypeClass) {
        try {
            var fieldType = Objects.requireNonNull(typeElement).getType();
            if (fieldType instanceof PsiClassType psiType) {
                var psiTypeClass = Objects.requireNonNull(psiType.resolve());
                var qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
                return isParentType(qualifiedName, psiTypeClass, parentTypeClass);
            }
        } catch (RuntimeException ex) {
            LOG.info(ex);
        }
        return false;
    }

    private static boolean isParentType(@NotNull String qualifiedName, @NotNull PsiClass psiTypeClass, @NotNull Class<?> parentTypeClass) {
        try {
            var typeClass = Class.forName(qualifiedName);
            return parentTypeClass.isAssignableFrom(typeClass);
        } catch (RuntimeException | ClassNotFoundException ignored) {
            var openProjects = ProjectManager.getInstance().getOpenProjects();
            for (var proj : openProjects) {
                var scope = psiTypeClass.getResolveScope();
                var psiUserClass = JavaPsiFacade.getInstance(proj).findClass(qualifiedName, scope);
                var parentType = JavaPsiFacade.getElementFactory(proj).createTypeByFQClassName(parentTypeClass.getCanonicalName(), scope);
                var parentClass = parentType.resolve();
                if (parentClass != null && psiUserClass != null && psiUserClass.isInheritor(parentClass, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDateDBType(@NotNull PsiType psiType) {
        if (psiType instanceof PsiClassType) {
            var psiClass = ((PsiClassType) psiType).resolve();
            if (psiClass != null) {
                var className = psiClass.getQualifiedName();
                return isKnownDateDBType(className);
            }
        }
        return false;
    }

    private static boolean isKnownDateDBType(String className) {
        var dateTypes = new String[]{
                "java.util.Date",
                "java.util.Calendar",
                "java.sql.Date",
                "java.sql.Time",
                "java.sql.Timestamp"
        };
        for (var dateType : dateTypes) {
            if (dateType.equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrimitiveOrWrapperType(@NotNull PsiType psiType) {
        if (psiType instanceof PsiPrimitiveType ||
                psiType.equalsToText("byte[]") || psiType.getCanonicalText(false).equalsIgnoreCase("java.lang.Byte[]") ||
                psiType.equalsToText("char[]") || psiType.getCanonicalText(false).equalsIgnoreCase("java.lang.Char[]")) {
            return true;
        } else if (psiType instanceof PsiClassType classType) {
            var psiClass = classType.resolve();
            if (psiClass != null) {
                var className = psiClass.getQualifiedName();
                return isKnownPrimitiveOrWrapperType(className);
            }
        }
        return false;
    }

    private static boolean isKnownPrimitiveOrWrapperType(String className) {
        var primitiveAndWrapperTypes = new String[]{
                "int", "java.lang.Integer",
                "boolean", "java.lang.Boolean",
                "char", "java.lang.Character",
                "byte", "java.lang.Byte",
                "short", "java.lang.Short",
                "long", "java.lang.Long",
                "float", "java.lang.Float",
                "double", "java.lang.Double"
        };
        for (var indice = 0; indice < primitiveAndWrapperTypes.length; indice += 2) {
            var primitiveType = primitiveAndWrapperTypes[indice];
            var wrapperType = primitiveAndWrapperTypes[indice + 1];
            if (primitiveType.equals(className) || wrapperType.equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEnumType(PsiType type) {
        if (type instanceof PsiClassType classType) {
            var psiClass = classType.resolve();
            if (psiClass != null) {
                return psiClass.isEnum();
            }
        }
        return false;
    }

    public static boolean isSerializableType(PsiType type) {
        if (type instanceof PsiClassType classType) {
            var psiClass = classType.resolve();
            if (psiClass != null) {
                return hasSerializableInterface(psiClass);
            }
        }
        return false;
    }

    private static boolean hasSerializableInterface(@NotNull PsiClass psiClass) {
        var interfaces = psiClass.getInterfaces();
        for (var anInterface : interfaces) {
            if ("java.io.Serializable".equals(anInterface.getQualifiedName())) {
                return true;
            }
        }
        var superClass = psiClass.getSuperClass();
        if (superClass != null) {
            return hasSerializableInterface(superClass);
        }
        return false;
    }
}
