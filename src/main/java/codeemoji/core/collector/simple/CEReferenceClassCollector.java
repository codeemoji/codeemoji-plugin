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
public abstract non-sealed class CEReferenceClassCollector extends CECollectorSimple<PsiClass, PsiElement> {

    protected CEReferenceClassCollector(@NotNull final Editor editor, @NotNull final String keyId, @Nullable final CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public final boolean processCollect(@NotNull final PsiElement psiElement, @NotNull final Editor editor, @NotNull final InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull final PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        final var reference = expression.getReference();
                        if (null != reference) {
                            final var resolveElement = reference.resolve();
                            if (resolveElement instanceof final PsiClass clazz && needsHint(clazz)) {
                                addInlay(expression, inlayHintsSink);
                            }
                        }
                    }
                    super.visitReferenceExpression(expression);
                }

                @Override
                public void visitVariable(@NotNull final PsiVariable variable) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        final var typeElement = variable.getTypeElement();
                        if (null != typeElement && !typeElement.isInferredType()
                                && typeElement.getType() instanceof final PsiClassType classType) {
                            final var clazz = classType.resolve();
                            if (null != clazz && (needsHint(clazz))) {
                                addInlay(variable, inlayHintsSink);

                            }

                        }
                    }
                    super.visitVariable(variable);
                }

                @Override
                public void visitClass(@NotNull final PsiClass psiClass) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        this.visitClassForRefs(psiClass.getExtendsList());
                        this.visitClassForRefs(psiClass.getImplementsList());
                    }
                    super.visitClass(psiClass);
                }

                private void visitClassForRefs(@Nullable final PsiReferenceList list) {
                    if (null != list) {
                        for (final var ref : list.getReferenceElements()) {
                            final var resolveElement = ref.resolve();
                            if (resolveElement instanceof final PsiClass clazz && (needsHint(clazz))) {
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
    public int calcOffset(@Nullable final PsiElement element) {
        if (element instanceof final PsiVariable variable) {
            final var varName = variable.getNameIdentifier();
            if (null != varName) {
                return varName.getTextOffset() - 1;
            }
        }
        return super.calcOffset(element);
    }
}