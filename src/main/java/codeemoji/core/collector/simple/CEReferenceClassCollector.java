package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEReferenceClassCollector extends CESimpleCollector<PsiClass, PsiElement> {

    protected CEReferenceClassCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key,
                                        @NotNull String tooltipKey, @Nullable CESymbol symbol) {
        super(editor, key, tooltipKey, symbol);
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                if (CEUtils.isNotPreviewEditor(editor)) {
                    var reference = expression.getReference();
                    if (null != reference) {
                        var resolveElement = reference.resolve();
                        if (resolveElement instanceof PsiClass clazz) {
                            var inlay = createInlayFor(clazz);
                            if (inlay != null) {
                                addInlayInline(expression, inlayHintsSink, inlay);
                            }
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
                        if (null != clazz) {
                            var inlay = createInlayFor(clazz);
                            if (inlay != null) {
                                addInlayInline(variable, inlayHintsSink, inlay);
                            }
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
                        if (resolveElement instanceof PsiClass clazz) {
                            var inlay = createInlayFor(clazz);
                            if (inlay != null) {
                                addInlayInline(ref, inlayHintsSink, inlay);
                            }
                        }
                    }
                }
            }
        };
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