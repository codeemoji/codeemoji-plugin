package codeemoji.core.collector.implicit;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public interface CEImplicitInterface {

    List<String> getBaseNames();

    @Nullable String createAttributes(@NotNull PsiMember member, @NotNull PsiAnnotation annotation);

    @Nullable
    default String updateAttributes(@NotNull PsiMember member, @NotNull PsiAnnotation annotation, @NotNull String attributeName) {
        return null;
    }

    @Nullable String buildAnnotationFor(@NotNull PsiMember member);

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
            if (annotationName != null) {
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
                if (result == null) {
                    var openProjects = ProjectManager.getInstance().getOpenProjects();
                    for (var proj : openProjects) {
                        var scope = psiTypeClass.getResolveScope();
                        var aClass = JavaPsiFacade.getInstance(proj).findClass(qualifiedName, scope);
                        if (aClass != null) {
                            result = aClass;
                            break;
                        }
                    }
                }
                if (result != null) {
                    for (var annotation : result.getAnnotations()) {
                        var annotationName = annotation.getQualifiedName();
                        if (annotationName != null) {
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

    default @Nullable String formatAttributes(@NotNull PsiAnnotation annotation, CEImplicitAttribute @NotNull ... attributes) {
        StringBuilder finalResult = null;
        List<String> resultAttributes = new ArrayList<>();
        for (var attributeForCheck : attributes) {
            if (attributeForCheck.value() != null) {
                var hasAttribute = annotation.findAttribute(attributeForCheck.name()) != null;
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
        return finalResult != null ? finalResult.toString() : null;
    }
}
