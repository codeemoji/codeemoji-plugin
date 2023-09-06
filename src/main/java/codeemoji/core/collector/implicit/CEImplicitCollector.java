package codeemoji.core.collector.implicit;

import codeemoji.core.collector.CECollectorBlock;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static codeemoji.core.collector.implicit.CEImplicit.CEImplicitAnnotation;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CEImplicitCollector extends CECollectorBlock<PsiElement> {

    private final CEImplicit implicit;

    public CEImplicitCollector(@NotNull Editor editor, CEImplicit implicit) {
        super(editor);
        this.implicit = implicit;
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitField(@NotNull PsiField field) {
                    if (hasImplicit(field.getContainingClass())) {
                        processAnnotations(field, implicit.getForFields(), field.getAnnotations(), inlayHintsSink);
                    }
                    super.visitField(field);
                }

                @Override
                public void visitMethod(@NotNull PsiMethod method) {
                    if (hasImplicit(method.getContainingClass())) {
                        processAnnotations(method, implicit.getForMethods(), method.getAnnotations(), inlayHintsSink);
                    }
                    super.visitMethod(method);
                }

                private void processAnnotations(@NotNull PsiElement element, @NotNull List<CEImplicitAnnotation> listForProcess,
                                                @NotNull PsiAnnotation[] annotations, @NotNull InlayHintsSink sink) {
                    for (var ia : listForProcess) {
                        var hasAnnotation = false;
                        for (var annotation : annotations) {
                            if (ia.getName().equalsIgnoreCase(annotation.getQualifiedName())) {
                                hasAnnotation = true;
                                break;
                            }
                        }
                        if (!hasAnnotation) {
                            var inlay = buildInlay(ia.getSymbol(), "inlay.implicitannotations.tooltip", null);
                            addInlay(element, sink, inlay);
                        }
                    }
                }

                private boolean hasImplicit(PsiClass containingClass) {
                    if (containingClass != null) {
                        var annotations = containingClass.getAnnotations();
                        for (PsiAnnotation annotation : annotations) {
                            if (implicit.getBaseName().equalsIgnoreCase(annotation.getQualifiedName())) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
        }
        return false;
    }
}