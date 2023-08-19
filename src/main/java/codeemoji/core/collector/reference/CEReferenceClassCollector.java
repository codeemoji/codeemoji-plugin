package codeemoji.core.collector.reference;

import codeemoji.core.collector.CESingleCollector;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CEReferenceClassCollector extends CESingleCollector<PsiClass, PsiElement> {

    protected CEReferenceClassCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        PsiReference reference = expression.getReference();
                        if (reference != null) {
                            PsiElement resolveElement = reference.resolve();
                            if (resolveElement instanceof PsiClass clazz && needsHint(clazz)) {
                                addInlay(expression, inlayHintsSink);
                            }
                        }
                    }
                    super.visitReferenceExpression(expression);
                }

                @Override
                public void visitVariable(@NotNull PsiVariable variable) {
                    PsiTypeElement typeElement = variable.getTypeElement();
                    if (typeElement != null && !typeElement.isInferredType()
                            && typeElement.getType() instanceof PsiClassType classType) {
                        PsiClass clazz = classType.resolve();
                        if (clazz != null && (needsHint(clazz))) {
                            addInlay(variable, inlayHintsSink);

                        }

                    }
                    super.visitVariable(variable);
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