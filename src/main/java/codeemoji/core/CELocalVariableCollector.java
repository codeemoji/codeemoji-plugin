package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

public abstract class CELocalVariableCollector extends CECollector<PsiElement, PsiElement> {

    public CELocalVariableCollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor, keyId);
    }

    @Override
    public boolean collectForPreviewEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) {
        if (element instanceof PsiLocalVariable variable) {
            PsiElement elem = variable.getNameIdentifier();
            if (elem != null) {
                execute(elem, sink);
            }
        } else if (element instanceof PsiReferenceExpression reference) {
            PsiElement elem = reference.getQualifier();
            if (elem != null) {
                execute(elem, sink);
            }
        }
        return false;
    }

    @Override
    public boolean collectForRegularEditor(@NotNull PsiElement element, @NotNull InlayHintsSink sink) {
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLocalVariable(@NotNull PsiLocalVariable variable) {
                execute(variable.getNameIdentifier(), sink);
                visitReferencesForElement(variable);
                super.visitLocalVariable(variable);
            }

            private void visitReferencesForElement(@NotNull PsiElement element) {
                GlobalSearchScope scope = GlobalSearchScope.fileScope(element.getContainingFile());
                PsiReference[] refs = ReferencesSearch.search(element, scope, false).toArray(PsiReference.EMPTY_ARRAY);
                for (PsiReference ref : refs) {
                    execute(ref.getElement(), sink);
                }
            }
        });
        return false;
    }
}