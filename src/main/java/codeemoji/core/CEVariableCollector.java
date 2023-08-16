package codeemoji.core;

import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@Getter
public abstract class CEVariableCollector extends CECollector<PsiVariable, PsiElement> {

    private final boolean enabledForField = true;
    private final boolean enabledForParam = true;
    private final boolean enabledForLocalVariable = true;

    public CEVariableCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitField(@NotNull PsiField field) {
                    if (isEnabledForField()) {
                        collectForField(field, editor, inlayHintsSink);
                    }
                    super.visitField(field);
                }

                @Override
                public void visitParameter(@NotNull PsiParameter parameter) {
                    if (isEnabledForParam()) {
                        collectForParam(parameter, editor, inlayHintsSink);
                    }
                    super.visitParameter(parameter);
                }

                @Override
                public void visitLocalVariable(@NotNull PsiLocalVariable localVariable) {
                    if (isEnabledForLocalVariable()) {
                        collectForLocal(localVariable, editor, inlayHintsSink);
                    }
                    super.visitLocalVariable(localVariable);
                }
            });
        }
        return false;
    }

    public void collectForField(@NotNull PsiField field, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        process(field, editor, sink);
    }

    public void collectForParam(@NotNull PsiParameter param, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        process(param, editor, sink);
    }

    public void collectForLocal(@NotNull PsiLocalVariable local, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        process(local, editor, sink);
    }

    private void process(@NotNull PsiVariable variable, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        if (isHintable(variable)) {
            addInlayOnEditor(variable.getNameIdentifier(), sink);
            if (CEUtils.isNotPreviewEditor(editor)) {
                GlobalSearchScope scope = GlobalSearchScope.fileScope(variable.getContainingFile());
                PsiReference[] refs = ReferencesSearch.search(variable, scope, false).toArray(PsiReference.EMPTY_ARRAY);
                for (PsiReference ref : refs) {
                    addInlayOnEditor(ref.getElement(), sink);
                }
            } else {
                processReferencesInPreviewEditor(variable, sink);
            }
        }
    }

    private void processReferencesInPreviewEditor(@NotNull PsiVariable variable, @NotNull InlayHintsSink inlayHintsSink) {
        variable.getContainingFile().accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                if (CEUtils.hasAUniqueQualifier(expression)) {
                    if (Objects.equals(expression.getText(), variable.getName())) {
                        addInlayOnEditor(expression, inlayHintsSink);
                    }
                }
                super.visitReferenceExpression(expression);
            }
        });
    }

    public int calcOffset(@Nullable PsiElement element) {
        if (element != null) {
            int length = element.getTextLength();
            String attr = "this.";
            if (element.getText().contains(attr)) {
                length -= attr.length();
            }
            return element.getTextOffset() + length;
        }
        return 0;
    }
}