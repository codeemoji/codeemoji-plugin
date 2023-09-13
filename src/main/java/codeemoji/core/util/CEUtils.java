package codeemoji.core.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReferenceParameterList;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.PsiTypeParameterList;
import com.intellij.psi.PsiTypes;
import com.intellij.psi.PsiVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.intellij.psi.PsiModifier.FINAL;
import static com.intellij.psi.PsiModifier.PRIVATE;
import static com.intellij.psi.PsiModifier.PROTECTED;
import static com.intellij.psi.PsiModifier.PUBLIC;
import static com.intellij.psi.PsiModifier.STATIC;

public enum CEUtils {
    ;

    private static final Logger LOG = Logger.getInstance(CEUtils.class);

    public static boolean isNotPreviewEditor(@NotNull final Editor editor) {
        return !"UNTYPED".equalsIgnoreCase(editor.getEditorKind().name());
    }

    public static boolean isArrayType(final PsiTypeElement typeElement) {
        try {
            final var returnClassSimpleName = Objects.requireNonNull(typeElement).getText();
            return returnClassSimpleName.contains("[]");
        } catch (final RuntimeException ex) {
            CEUtils.LOG.info(ex);
        }
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean sameNameAsType(final PsiTypeElement typeElement, @Nullable String fieldName) {
        if (null != fieldName) {
            try {
                var typeName = Objects.requireNonNull(typeElement).getType().getPresentableText();
                final var index = typeName.indexOf("<");
                if (0 < index) {
                    typeName = typeName.substring(0, index);
                }
                typeName = CEUtils.calcLastWordCapitalized(typeName);
                fieldName = CEUtils.calcLastWordCapitalized(fieldName);
                return fieldName.equalsIgnoreCase(typeName);
            } catch (final RuntimeException ex) {
                CEUtils.LOG.info(ex);
            }
        }
        return false;
    }

    private static String calcLastWordCapitalized(@NotNull final String word) {
        final var words = word.split("(?=[A-Z])");
        return words[words.length - 1];
    }

    public static boolean isPluralForm(@Nullable final String name) {
        if (null != name && 1 < name.trim().length()) {
            final var word = CEUtils.identifyLastWordWithUpperCase(name);
            if (CEUtils.isSuffixWhiteList(name)) {
                return false;
            }
            if (CEUtils.isIrregularPluralForm(word)) {
                return true;
            } else return CEUtils.isCommonPluralForm(word);
        }
        return false;
    }

    private static @NotNull String identifyLastWordWithUpperCase(@NotNull final String name) {
        String result = null;
        final var pattern = Pattern.compile("\\b[A-Z][a-zA-Z]*\\b");
        final var matcher = pattern.matcher(name);
        while (matcher.find()) {
            result = matcher.group();
        }
        return (null != result) ? result : name;
    }

    private static boolean isIrregularPluralForm(@NotNull final String word) {
        final var classLoader = CEUtils.class.getClassLoader();
        try (final var is = classLoader.getResourceAsStream("irregular_plural.json")) {
            if (null != is) {
                final Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                final var je = new Gson().fromJson(reader, JsonObject.class).get(word.trim().toLowerCase());
                if (null != je) {
                    return null != je.getAsString();
                }
            }
        } catch (final RuntimeException | IOException ex) {
            CEUtils.LOG.info(ex);
        }
        return false;
    }

    private static boolean isCommonPluralForm(@NotNull final CharSequence word) {
        final var pattern = ".*s$";
        final var pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        final var mat = pat.matcher(word);
        return mat.matches();
    }

    public static boolean containsOnlySpecialCharacters(@NotNull final CharSequence name) {
        final var regex = "^[^a-zA-Z0-9]+$";
        final var pattern = Pattern.compile(regex);
        final var matcher = pattern.matcher(name);
        return matcher.matches();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isGenericType(@NotNull final PsiElement element, @Nullable final PsiTypeElement typeElement) {
        final var tpl = CEUtils.searchGenericTypesList(element);
        if (null != tpl) {
            final var tps = tpl.getTypeParameters();
            for (final var tp : tps) {
                if (null != typeElement) {
                    if (typeElement.getText().equalsIgnoreCase(tp.getText())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Nullable
    private static PsiTypeParameterList searchGenericTypesList(@NotNull final PsiElement son) {
        PsiTypeParameterList result = null;
        final var father = son.getParent();
        if (father instanceof final PsiClass clazz) {
            result = clazz.getTypeParameterList();
        } else if (father instanceof final PsiParameterList plist) {
            final var gramFather = plist.getParent();
            if (gramFather instanceof final PsiMethod method) {
                final var greatGramFather = method.getParent();
                if (greatGramFather instanceof final PsiClass clazz) {
                    result = clazz.getTypeParameterList();
                }
            }
        }
        return result;
    }

    public static boolean isNumericType(@NotNull final PsiTypeElement typeElement) {
        final var type = typeElement.getType();
        return PsiTypes.intType().isAssignableFrom(type)
                || PsiTypes.longType().isAssignableFrom(type)
                || PsiTypes.floatType().isAssignableFrom(type)
                || PsiTypes.doubleType().isAssignableFrom(type)
                || PsiTypes.byteType().isAssignableFrom(type)
                || PsiTypes.shortType().isAssignableFrom(type);
    }

    @SuppressWarnings("unused")
    public static @Nullable PsiElement identifyFirstQualifier(@NotNull final PsiElement element) {
        final var child = element.getFirstChild();
        if (null != child && (!(child instanceof PsiReferenceExpression) || 0 < child.getChildren().length)) {
            if (child instanceof PsiReferenceParameterList) {
                return element;
            }
            return CEUtils.identifyFirstQualifier(child);
        }
        return child;
    }

    public static boolean checkDefaultModifier(@NotNull final PsiModifierList psiModifierList) {
        return !(psiModifierList.hasModifierProperty(PUBLIC) ||
                psiModifierList.hasModifierProperty(PROTECTED) ||
                psiModifierList.hasModifierProperty(PRIVATE));
    }

    public static boolean hasAUniqueQualifier(@NotNull final PsiReferenceExpression expression) {
        return !expression.getText().contains(".");
    }

    private static boolean isSuffixWhiteList(@NotNull final String name) {
        final var formattedName = name.trim().toLowerCase();
        if (formattedName.isEmpty()) {
            return false;
        }
        final var classLoader = CEUtils.class.getClassLoader();
        try (final var is = classLoader.getResourceAsStream("suffix_whitelist_plural.json")) {
            if (null != is) {
                final Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                final var je = new Gson().fromJson(reader, JsonObject.class);
                if (null != je) {
                    var suffixes = je.getAsJsonArray("suffixes");
                    for (final var suffix : suffixes.asList()) {
                        if (formattedName.endsWith(suffix.getAsString())) {
                            return true;
                        }
                    }

                }
            }
        } catch (final RuntimeException | IOException ex) {
            CEUtils.LOG.info(ex);
        }
        return false;
    }

    public static @Nullable String resolveQualifiedName(@NotNull final PsiClassType psiType) {
        try {
            final var psiTypeClass = Objects.requireNonNull(psiType.resolve());
            final var qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
            return CEUtils.resolveQualifiedName(qualifiedName, psiTypeClass);
        } catch (final RuntimeException ignored) {
            return psiType.getName();
        }
    }

    private static @Nullable String resolveQualifiedName(@NotNull final String qualifiedName, @NotNull final PsiClass psiTypeClass) {
        try {
            final var typeClass = Class.forName(qualifiedName);
            return typeClass.getCanonicalName();
        } catch (final RuntimeException | ClassNotFoundException ignored) {
            final var openProjects = ProjectManager.getInstance().getOpenProjects();
            for (final var proj : openProjects) {
                final var scope = psiTypeClass.getResolveScope();
                final var psiUserClass = JavaPsiFacade.getInstance(proj).findClass(qualifiedName, scope);
                if (null != psiUserClass) {
                    return psiUserClass.getQualifiedName();
                }
            }
            return null;
        }
    }

    public static boolean isConstantName(@NotNull final PsiVariable element) {
        return CEUtils.isConstant(element) && Objects.equals(element.getType().getPresentableText(), "String");
    }

    public static boolean isConstant(@NotNull final PsiModifierListOwner element) {
        final var modifierList = element.getModifierList();
        return null != modifierList &&
                modifierList.hasExplicitModifier(STATIC) &&
                modifierList.hasExplicitModifier(FINAL);
    }

    public static boolean isIterableType(final PsiTypeElement typeElement) {
        return CEUtils.checkParentType(typeElement, Iterable.class);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isMappableType(final PsiTypeElement typeElement) {
        return CEUtils.checkParentType(typeElement, Map.class);
    }

    private static boolean checkParentType(final PsiTypeElement typeElement, @NotNull final Class<?> parentTypeClass) {
        try {
            final var fieldType = Objects.requireNonNull(typeElement).getType();
            if (fieldType instanceof final PsiClassType psiType) {
                final var psiTypeClass = Objects.requireNonNull(psiType.resolve());
                final var qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
                return CEUtils.isParentType(qualifiedName, psiTypeClass, parentTypeClass);
            }
        } catch (final RuntimeException ex) {
            CEUtils.LOG.info(ex);
        }
        return false;
    }

    private static boolean isParentType(@NotNull final String qualifiedName, @NotNull final PsiClass psiTypeClass, @NotNull final Class<?> parentTypeClass) {
        try {
            final var typeClass = Class.forName(qualifiedName);
            return parentTypeClass.isAssignableFrom(typeClass);
        } catch (final RuntimeException | ClassNotFoundException ignored) {
            final var openProjects = ProjectManager.getInstance().getOpenProjects();
            for (final var proj : openProjects) {
                final var scope = psiTypeClass.getResolveScope();
                final var psiUserClass = JavaPsiFacade.getInstance(proj).findClass(qualifiedName, scope);
                final var parentType = JavaPsiFacade.getElementFactory(proj).createTypeByFQClassName(parentTypeClass.getCanonicalName(), scope);
                final var parentClass = parentType.resolve();
                if (null != parentClass && null != psiUserClass && psiUserClass.isInheritor(parentClass, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDateDBType(@NotNull final PsiType psiType) {
        if (psiType instanceof PsiClassType classType) {
            final var psiClass = classType.resolve();
            if (null != psiClass) {
                final var className = psiClass.getQualifiedName();
                return CEUtils.isKnownDateDBType(className);
            }
        }
        return false;
    }

    private static boolean isKnownDateDBType(final String className) {
        final var dateTypes = new String[]{
                "java.util.Date",
                "java.util.Calendar",
                "java.sql.Date",
                "java.sql.Time",
                "java.sql.Timestamp"
        };
        for (final var dateType : dateTypes) {
            if (dateType.equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPrimitiveOrWrapperType(@NotNull final PsiType psiType) {
        if (psiType instanceof PsiPrimitiveType ||
                psiType.equalsToText("byte[]") || "java.lang.Byte[]".equalsIgnoreCase(psiType.getCanonicalText(false)) ||
                psiType.equalsToText("char[]") || "java.lang.Char[]".equalsIgnoreCase(psiType.getCanonicalText(false))) {
            return true;
        } else if (psiType instanceof final PsiClassType classType) {
            final var psiClass = classType.resolve();
            if (null != psiClass) {
                final var className = psiClass.getQualifiedName();
                return CEUtils.isKnownPrimitiveOrWrapperType(className);
            }
        }
        return false;
    }

    private static boolean isKnownPrimitiveOrWrapperType(final String className) {
        final var primitiveAndWrapperTypes = new String[]{
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
            final var primitiveType = primitiveAndWrapperTypes[indice];
            final var wrapperType = primitiveAndWrapperTypes[indice + 1];
            if (primitiveType.equals(className) || wrapperType.equals(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEnumType(final PsiType type) {
        if (type instanceof final PsiClassType classType) {
            final var psiClass = classType.resolve();
            if (null != psiClass) {
                return psiClass.isEnum();
            }
        }
        return false;
    }

    public static boolean isSerializableType(final PsiType type) {
        if (type instanceof final PsiClassType classType) {
            final var psiClass = classType.resolve();
            if (null != psiClass) {
                return CEUtils.hasSerializableInterface(psiClass);
            }
        }
        return false;
    }

    private static boolean hasSerializableInterface(@NotNull final PsiClass psiClass) {
        final var interfaces = psiClass.getInterfaces();
        for (final var anInterface : interfaces) {
            if ("java.io.Serializable".equals(anInterface.getQualifiedName())) {
                return true;
            }
        }
        final var superClass = psiClass.getSuperClass();
        if (null != superClass) {
            return CEUtils.hasSerializableInterface(superClass);
        }
        return false;
    }
}
