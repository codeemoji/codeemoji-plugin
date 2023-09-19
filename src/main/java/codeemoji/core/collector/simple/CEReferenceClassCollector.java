package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.PsiVariable;
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
                        if (null != reference) {
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
                        if (null != typeElement && !typeElement.isInferredType()
                                && typeElement.getType() instanceof PsiClassType classType) {
                            var clazz = classType.resolve();
                            if (null != clazz && (needsHint(clazz))) {
                                addInlay(variable, inlayHintsSink);

                            }

                        }
                    }
                    super.visitVariable(variable);
                }

                @Override
                public void visitClass(@NotNull PsiClass aClass) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        visitClassForRefs(aClass.getExtendsList());
                        visitClassForRefs(aClass.getImplementsList());
                    }
                    super.visitClass(aClass);
                }

                private void visitClassForRefs(@Nullable PsiReferenceList list) {
                    if (null != list) {
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
            if (null != varName) {
                return varName.getTextOffset() - 1;
            }
        }
        return super.calcOffset(element);
    }
}