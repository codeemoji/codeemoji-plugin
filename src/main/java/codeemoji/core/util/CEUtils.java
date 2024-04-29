package codeemoji.core.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.intellij.psi.PsiModifier.*;

public enum CEUtils {
    ;

    private static final Logger LOG = Logger.getInstance(CEUtils.class);

    public static boolean isNotPreviewEditor(@NotNull Editor editor) {
        return !"UNTYPED".equalsIgnoreCase(editor.getEditorKind().name());
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
    public static boolean sameNameAsType(PsiTypeElement typeElement, @Nullable String fieldName) {
        if (null != fieldName) {
            try {
                var typeName = Objects.requireNonNull(typeElement).getType().getPresentableText();
                var index = typeName.indexOf("<");
                if (0 < index) {
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

    public static boolean isPluralForm(@Nullable String name) {
        if (null != name && 1 < name.trim().length()) {
            var word = calcLastWordCapitalized(name);
            if (isSuffixWhiteList(name)) {
                return false;
            }
            if (isIrregularPluralForm(word)) {
                return true;
            } else return isCommonPluralForm(word);
        }
        return false;
    }

    private static boolean isIrregularPluralForm(@NotNull String word) {
        var classLoader = CEUtils.class.getClassLoader();
        try (var is = classLoader.getResourceAsStream("irregular_plural.json")) {
            if (null != is) {
                Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                var je = new Gson().fromJson(reader, JsonObject.class).get(word.trim().toLowerCase());
                if (null != je) {
                    return null != je.getAsString();
                }
            }
        } catch (RuntimeException | IOException ex) {
            LOG.info(ex);
        }
        return false;
    }

    private static boolean isCommonPluralForm(@NotNull CharSequence word) {
        final var pattern = ".*s$";
        var pat = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        var mat = pat.matcher(word);
        return mat.matches();
    }

    public static boolean containsOnlySpecialCharacters(@NotNull CharSequence name) {
        final var regex = "^[^a-zA-Z0-9]+$";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(name);
        return matcher.matches();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isGenericType(@NotNull PsiElement element, @Nullable PsiTypeElement typeElement) {
        var tpl = searchGenericTypesList(element);
        if (null != tpl) {
            var tps = tpl.getTypeParameters();
            for (var tp : tps) {
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
        if (null != child && (!(child instanceof PsiReferenceExpression) || 0 < child.getChildren().length)) {
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
        var classLoader = CEUtils.class.getClassLoader();
        try (var is = classLoader.getResourceAsStream("whitelist_plural.json")) {
            if (null != is) {
                Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                var je = new Gson().fromJson(reader, JsonObject.class);
                if (null != je) {
                    var suffixes = je.getAsJsonArray("suffixes");
                    for (var suffix : suffixes.asList()) {
                        if (formattedName.endsWith(suffix.getAsString())) {
                            return true;
                        }
                    }

                }
            }
        } catch (RuntimeException | IOException ex) {
            LOG.info(ex);
        }
        return false;
    }

    public static @Nullable String resolveQualifiedName(@Nullable PsiClassType psiType) {
        try {
            var psiTypeClass = Objects.requireNonNull(psiType != null ? psiType.resolve() : null);
            var qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
            return resolveQualifiedName(qualifiedName, psiTypeClass);
        } catch (RuntimeException ignored) {
            if (psiType != null) {
                try {
                    return psiType.getName();
                } catch (IllegalStateException ignored1) {
                }
            }
            return null;
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
                if (null != psiUserClass) {
                    return psiUserClass.getQualifiedName();
                }
            }
            return null;
        }
    }

    public static boolean isConstantName(@NotNull PsiVariable element) {
        return isConstant(element) && Objects.equals(element.getType().getPresentableText(), "String");
    }

    public static boolean isConstant(@NotNull PsiModifierListOwner element) {
        var modifierList = element.getModifierList();
        return null != modifierList &&
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
                if (null != parentClass && null != psiUserClass && psiUserClass.isInheritor(parentClass, true)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDateDBType(@NotNull PsiType psiType) {
        if (psiType instanceof PsiClassType classType) {
            var psiClass = classType.resolve();
            if (null != psiClass) {
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
                psiType.equalsToText("byte[]") || "java.lang.Byte[]".equalsIgnoreCase(psiType.getCanonicalText(false)) ||
                psiType.equalsToText("char[]") || "java.lang.Char[]".equalsIgnoreCase(psiType.getCanonicalText(false))) {
            return true;
        } else if (psiType instanceof PsiClassType classType) {
            var psiClass = classType.resolve();
            if (null != psiClass) {
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
            if (null != psiClass) {
                return psiClass.isEnum();
            }
        }
        return false;
    }

    public static boolean isSerializableType(PsiType type) {
        if (type instanceof PsiClassType classType) {
            var psiClass = classType.resolve();
            if (null != psiClass) {
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
        if (null != superClass) {
            return hasSerializableInterface(superClass);
        }
        return false;
    }

    public static String uncapitalizeAsProperty(String str) {
        if (notHasLength(str) || (1 < str.length() && Character.isUpperCase(str.charAt(0)) &&
                Character.isUpperCase(str.charAt(1)))) {
            return str;
        }
        return changeFirstCharacterCase(str, false);
    }

    public static boolean notHasLength(@Nullable CharSequence charSequence) {
        return (null == charSequence || charSequence.isEmpty());
    }

    private static String changeFirstCharacterCase(String str, @SuppressWarnings("SameParameterValue") boolean capitalize) {
        if (notHasLength(str)) {
            return str;
        }
        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        } else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars);
    }

    public static @NotNull JPanel createBasicInnerPanel(@NotNull String titleKey, int rows, int cols) {
        return createBasicInnerPanel(titleKey, rows, cols, 7, 0, 7, 0, 0, 0, null, null);
    }

    public static @NotNull JPanel createBasicInnerPanel(@NotNull String titleKey, int rows, int cols,
                                                        int borderTop, int borderLeft, int borderBottom, int borderRight,
                                                        int justification, int position, Font font, Color color) {
        var result = new JPanel(new GridLayout(rows, cols));
        var title = CEBundle.getString(titleKey);
        result.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(borderTop, borderLeft, borderBottom, borderRight),
                title, justification, position, font, color));
        return result;
    }

    public static List<VirtualFile> getSourceRootsInProject(@NotNull Project project){
        return Arrays.stream(ModuleManager.getInstance(project).getModules()).map(module -> ModuleRootManager.getInstance(module).getSourceRoots()).flatMap(Arrays::stream).toList();
    }

    public static int calculateMethodBodyLineCount(PsiMethod method){
        Document documentOfMethod = method.getContainingFile().getViewProvider().getDocument();
        int methodBodyLineCount = 0;

        if(method.getBody() != null && !method.getBody().isEmpty()){
            PsiElement leftParenthesis = method.getBody().getChildren()[0];
            PsiElement rightParenthesis = method.getBody().getChildren()[method.getBody().getChildren().length-1];

            methodBodyLineCount = documentOfMethod.getLineNumber(method.getBody().getTextRange().getEndOffset()) - documentOfMethod.getLineNumber(method.getBody().getTextRange().getStartOffset()) - 1;

            if(documentOfMethod.getLineNumber(leftParenthesis.getTextOffset()) == documentOfMethod.getLineNumber(method.getBody().getStatements()[0].getTextOffset())){
                methodBodyLineCount = methodBodyLineCount + 1;
            }
            if(documentOfMethod.getLineNumber(rightParenthesis.getTextOffset()) == documentOfMethod.getLineNumber(method.getBody().getStatements()[method.getBody().getStatements().length - 1].getTextOffset())){
                methodBodyLineCount = methodBodyLineCount + 1;
            }
        }

        return methodBodyLineCount;
    }

    public static int calculateCommentPaddingLinesInMethod(PsiMethod method){
        Document documentOfMethod = method.getContainingFile().getViewProvider().getDocument();
        return Arrays.stream(PsiTreeUtil.collectElements(method.getBody(), element -> element instanceof PsiComment))
                .mapToInt(commentElement -> {
                    int commentPaddingLines = documentOfMethod.getLineNumber(method.getTextOffset()) == documentOfMethod.getLineNumber(commentElement.getTextOffset()) ? 0 : calculateLinesOfPsiElement(commentElement);
                    return (method.getBody() != null && !method.getBody().isEmpty()) ? commentPaddingLines : 0;
                })
                .sum();
    }

    public static <E extends PsiElement> int calculateLinesOfPsiElement(@NotNull E element){
        Document documentOfMethod = element.getContainingFile().getViewProvider().getDocument();
        return 1 + (documentOfMethod.getLineNumber(element.getTextOffset() + element.getTextLength()) - documentOfMethod.getLineNumber(element.getTextOffset()));
    }

}
