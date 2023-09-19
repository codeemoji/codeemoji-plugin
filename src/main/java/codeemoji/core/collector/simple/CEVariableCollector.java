package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiLocalVariable;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
@Setter
@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEVariableCollector extends CESimpleCollector<PsiVariable, PsiElement> {

    private boolean enabledForField;
    private boolean enabledForParam;
    private boolean enabledForLocalVariable;

    protected CEVariableCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
        enabledForField = true;
        enabledForParam = true;
        enabledForLocalVariable = true;
    }

    @Override
    public final boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitField(@NotNull PsiField field) {
                    if (isEnabledForField()) {
                        process(field, editor, inlayHintsSink);
                    }
                    super.visitField(field);
                }

                @Override
                public void visitParameter(@NotNull PsiParameter parameter) {
                    if (isEnabledForParam()) {
                        process(parameter, editor, inlayHintsSink);
                    }
                    super.visitParameter(parameter);
                }

                @Override
                public void visitLocalVariable(@NotNull PsiLocalVariable variable) {
                    if (isEnabledForLocalVariable()) {
                        process(variable, editor, inlayHintsSink);
                    }
                    super.visitLocalVariable(variable);
                }

                private void process(@NotNull PsiVariable variable, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
                    if (needsHint(variable, processExternalInfo(variable))) {
                        addInlay(variable.getNameIdentifier(), sink);
                        if (CEUtils.isNotPreviewEditor(editor)) {
                            var scope = GlobalSearchScope.fileScope(variable.getContainingFile());
                            var refs = ReferencesSearch.search(variable, scope, false).toArray(PsiReference.EMPTY_ARRAY);
                            for (var ref : refs) {
                                addInlay(ref.getElement(), sink);
                            }
                        } else {
                            processReferencesInPreviewEditor(variable, sink);
                        }
                    }
                }

                private void processReferencesInPreviewEditor(@NotNull PsiNamedElement variable, @NotNull InlayHintsSink inlayHintsSink) {
                    variable.getContainingFile().accept(new JavaRecursiveElementVisitor() {
                        @Override
                        public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                            if (CEUtils.hasAUniqueQualifier(expression)
                                    && Objects.equals(expression.getText(), variable.getName())) {
                                addInlay(expression, inlayHintsSink);
                            }
                            super.visitReferenceExpression(expression);
                        }
                    });
                }
            });
        }
        return false;
    }

    @Override
    public int calcOffset(@Nullable PsiElement element) {
        if (null != element) {
            var length = element.getTextLength();
            final var attr = "this.";
            if (element.getText().contains(attr)) {
                length -= attr.length();
            }
            return element.getTextOffset() + length;
        }
        return 0;
    }
}