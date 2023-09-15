package codeemoji.core.collector.implicit;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public interface CEImplicitInterface {

    @NotNull
    String getBaseName();

    @Nullable
    String createAnnotationFor(@NotNull PsiMember member);

    @Nullable
    String createAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation annotation);

    @Nullable
    default String updateAttributesFor(@NotNull PsiMember member, @NotNull PsiAnnotation annotation, @NotNull String attributeName) {
        return null;
    }

    default @Nullable String formatAttributes(@NotNull PsiAnnotation annotation, CEImplicitAttribute @NotNull ... attributes) {
        StringBuilder finalResult = null;
        List<String> resultAttributes = new ArrayList<>();
        for (var attributeForCheck : attributes) {
            if (null != attributeForCheck.value()) {
                var hasAttribute = null != annotation.findAttribute(attributeForCheck.name());
                if (!hasAttribute) {
                    var prefix = "";
                    if (attributeForCheck.textual()) {
                        prefix = "\"";
                    }
                    resultAttributes.add(attributeForCheck.name() + " = " + prefix + attributeForCheck.value() + prefix);
                }
            }
        }
        if (!resultAttributes.isEmpty()) {
            finalResult = new StringBuilder();
            for (var ind = 0; ind < resultAttributes.size(); ind++) {
                finalResult.append(resultAttributes.get(ind));
                if (ind + 1 < resultAttributes.size()) {
                    finalResult.append(", ");
                }
            }
            if (annotation.getAttributes().isEmpty()) {
                if (!annotation.getText().contains("(") && !annotation.getText().contains(")")) {
                    finalResult = new StringBuilder("(" + finalResult + ")");
                }
            } else {
                finalResult.insert(0, ", ");
            }
        }
        return null != finalResult ? finalResult.toString() : null;
    }

    @NotNull
    default List<String> getDeactivatedCases() {
        return new ArrayList<>();
    }

    @NotNull
    default List<String> getDeactivatedInTypeCases() {
        return new ArrayList<>();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean isDeactivatedFor(@NotNull PsiMember member) {
        for (var annotation : member.getAnnotations()) {
            var annotationName = annotation.getQualifiedName();
            if (null != annotationName) {
                for (var caseDeactivated : getDeactivatedCases()) {
                    if (annotationName.equalsIgnoreCase(caseDeactivated)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean isDeactivatedInType(PsiType type) {
        if (type instanceof PsiClassType classType) {
            try {
                PsiClass result = null;
                var psiTypeClass = Objects.requireNonNull(classType.resolve());
                var qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
                try {
                    Class.forName(qualifiedName);
                    result = psiTypeClass;
                } catch (ClassNotFoundException ignored) {
                }
                if (null == result) {
                    var openProjects = ProjectManager.getInstance().getOpenProjects();
                    for (var proj : openProjects) {
                        var scope = psiTypeClass.getResolveScope();
                        var aClass = JavaPsiFacade.getInstance(proj).findClass(qualifiedName, scope);
                        if (null != aClass) {
                            result = aClass;
                            break;
                        }
                    }
                }
                if (null != result) {
                    for (var annotation : result.getAnnotations()) {
                        var annotationName = annotation.getQualifiedName();
                        if (null != annotationName) {
                            for (var caseDeactivatedInType : getDeactivatedInTypeCases()) {
                                if (annotationName.equalsIgnoreCase(caseDeactivatedInType)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            } catch (RuntimeException ignored) {
            }
        }
        return false;
    }

}
