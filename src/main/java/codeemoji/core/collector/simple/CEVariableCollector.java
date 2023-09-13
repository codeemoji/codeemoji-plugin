package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
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
public abstract non-sealed class CEVariableCollector extends CECollectorSimple<PsiVariable, PsiElement> {

    private boolean enabledForField;
    private boolean enabledForParam;
    private boolean enabledForLocalVariable;

    protected CEVariableCollector(@NotNull final Editor editor, @NotNull final String keyId, @Nullable final CESymbol symbol) {
        super(editor, keyId, symbol);
        this.enabledForField = true;
        this.enabledForParam = true;
        this.enabledForLocalVariable = true;
    }

    @Override
    public final boolean processCollect(@NotNull final PsiElement psiElement, @NotNull final Editor editor, @NotNull final InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitField(@NotNull final PsiField field) {
                    if (CEVariableCollector.this.isEnabledForField()) {
                        CEVariableCollector.this.process(field, editor, inlayHintsSink);
                    }
                    super.visitField(field);
                }

                @Override
                public void visitParameter(@NotNull final PsiParameter parameter) {
                    if (CEVariableCollector.this.isEnabledForParam()) {
                        CEVariableCollector.this.process(parameter, editor, inlayHintsSink);
                    }
                    super.visitParameter(parameter);
                }

                @Override
                public void visitLocalVariable(@NotNull final PsiLocalVariable localVariable) {
                    if (CEVariableCollector.this.isEnabledForLocalVariable()) {
                        CEVariableCollector.this.process(localVariable, editor, inlayHintsSink);
                    }
                    super.visitLocalVariable(localVariable);
                }
            });
        }
        return false;
    }

    private void process(@NotNull final PsiVariable variable, @NotNull final Editor editor, @NotNull final InlayHintsSink sink) {
        if (this.needsHint(variable)) {
            this.addInlay(variable.getNameIdentifier(), sink);
            if (CEUtils.isNotPreviewEditor(editor)) {
                final var scope = GlobalSearchScope.fileScope(variable.getContainingFile());
                final var refs = ReferencesSearch.search(variable, scope, false).toArray(PsiReference.EMPTY_ARRAY);
                for (final var ref : refs) {
                    this.addInlay(ref.getElement(), sink);
                }
            } else {
                this.processReferencesInPreviewEditor(variable, sink);
            }
        }
    }

    private void processReferencesInPreviewEditor(@NotNull final PsiVariable variable, @NotNull final InlayHintsSink inlayHintsSink) {
        variable.getContainingFile().accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(@NotNull final PsiReferenceExpression expression) {
                if (CEUtils.hasAUniqueQualifier(expression)
                        && Objects.equals(expression.getText(), variable.getName())) {
                    CEVariableCollector.this.addInlay(expression, inlayHintsSink);
                }
                super.visitReferenceExpression(expression);
            }
        });
    }

    @Override
    public int calcOffset(@Nullable final PsiElement element) {
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