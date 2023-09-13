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

    List<String> getBaseNames();

    @Nullable String createAttributes(@NotNull PsiMember member, @NotNull PsiAnnotation annotation);

    @Nullable
    default String updateAttributes(@NotNull final PsiMember member, @NotNull final PsiAnnotation annotation, @NotNull final String attributeName) {
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
    default boolean isDeactivatedFor(@NotNull final PsiMember member) {
        for (final var annotation : member.getAnnotations()) {
            final var annotationName = annotation.getQualifiedName();
            if (null != annotationName) {
                for (final var caseDeactivated : this.getDeactivatedCases()) {
                    if (annotationName.equalsIgnoreCase(caseDeactivated)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    default boolean isDeactivatedInType(final PsiType type) {
        if (type instanceof final PsiClassType classType) {
            try {
                PsiClass result = null;
                final var psiTypeClass = Objects.requireNonNull(classType.resolve());
                final var qualifiedName = Objects.requireNonNull(psiTypeClass.getQualifiedName());
                try {
                    Class.forName(qualifiedName);
                    result = psiTypeClass;
                } catch (final ClassNotFoundException ignored) {
                }
                if (null == result) {
                    final var openProjects = ProjectManager.getInstance().getOpenProjects();
                    for (final var proj : openProjects) {
                        final var scope = psiTypeClass.getResolveScope();
                        final var aClass = JavaPsiFacade.getInstance(proj).findClass(qualifiedName, scope);
                        if (null != aClass) {
                            result = aClass;
                            break;
                        }
                    }
                }
                if (null != result) {
                    for (final var annotation : result.getAnnotations()) {
                        final var annotationName = annotation.getQualifiedName();
                        if (null != annotationName) {
                            for (final var caseDeactivatedInType : this.getDeactivatedInTypeCases()) {
                                if (annotationName.equalsIgnoreCase(caseDeactivatedInType)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            } catch (final RuntimeException ignored) {
            }
        }
        return false;
    }

    default @Nullable String formatAttributes(@NotNull final PsiAnnotation annotation, final CEImplicitAttribute @NotNull ... attributes) {
        StringBuilder finalResult = null;
        final List<String> resultAttributes = new ArrayList<>();
        for (final var attributeForCheck : attributes) {
            if (null != attributeForCheck.value()) {
                final var hasAttribute = null != annotation.findAttribute(attributeForCheck.name());
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
}
