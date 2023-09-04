package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEReferenceClassCollector extends CESimpleCollector<PsiClass, PsiElement> {

    protected CEReferenceClassCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public final boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        var reference = expression.getReference();
                        if (reference != null) {
                            var resolveElement = reference.resolve();
                            if (resolveElement instanceof PsiClass clazz && needsHint(clazz)) {
                                addInlay(expression, inlayHintsSink);
                            }
                        }
                    }
                    super.visitReferenceExpression(expression);
                }

                @Override
                public void visitVariable(@NotNull PsiVariable variable) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        var typeElement = variable.getTypeElement();
                        if (typeElement != null && !typeElement.isInferredType()
                                && typeElement.getType() instanceof PsiClassType classType) {
                            var clazz = classType.resolve();
                            if (clazz != null && (needsHint(clazz))) {
                                addInlay(variable, inlayHintsSink);

                            }

                        }
                    }
                    super.visitVariable(variable);
                }

                @Override
                public void visitClass(@NotNull PsiClass psiClass) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        visitClassForRefs(psiClass.getExtendsList());
                        visitClassForRefs(psiClass.getImplementsList());
                    }
                    super.visitClass(psiClass);
                }

                private void visitClassForRefs(@Nullable PsiReferenceList list) {
                    if (list != null) {
                        for (var ref : list.getReferenceElements()) {
                            var resolveElement = ref.resolve();
                            if (resolveElement instanceof PsiClass clazz && (needsHint(clazz))) {
                                addInlay(ref, inlayHintsSink);

                            }
                        }
                    }
                }
            });
        }
        return false;
    }

    @Override
    public int calcOffset(@Nullable PsiElement element) {
        if (element instanceof PsiVariable variable) {
            var varName = variable.getNameIdentifier();
            if (varName != null) {
                return varName.getTextOffset() - 1;
            }
        }
        return super.calcOffset(element);
    }
}