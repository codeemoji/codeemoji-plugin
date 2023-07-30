package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

public abstract class CELocalVariableCollector extends CECollector<PsiElement, PsiElement> {

    public CELocalVariableCollector(Editor editor, String keyId) {
        super(editor, keyId);
    }

    public CELocalVariableCollector(Editor editor, String keyId, CEInlay ceInlay) {
        super(editor, keyId, ceInlay);
    }

    public CELocalVariableCollector(Editor editor, String keyId, int codePoint) {
        super(editor, keyId, codePoint);
    }

    public CELocalVariableCollector(Editor editor, String keyId, CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public final boolean collectInPreviewEditor(PsiElement element, InlayHintsSink sink) throws RuntimeException {
        if (element instanceof PsiLocalVariable variable) {
            if (checkAddInlay(variable.getNameIdentifier())) {
                addInlayOnEditor(variable.getNameIdentifier(), sink);
            }
        } else if (element instanceof PsiReferenceExpression reference) {
            if (checkAddInlay(reference.getQualifier())) {
                addInlayOnEditor(reference.getQualifier(), sink);
            }
        }
        return false;
    }

    @Override
    public final boolean collectInDefaultEditor(@NotNull PsiElement element, InlayHintsSink sink) {
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLocalVariable(@NotNull PsiLocalVariable variable) {
                if (checkAddInlay(variable.getNameIdentifier())) {
                    addInlayOnEditor(variable.getNameIdentifier(), sink);
                    visitReferencesForElement(variable);
                }
                super.visitLocalVariable(variable);
            }

            private void visitReferencesForElement(@NotNull PsiElement element) {
                GlobalSearchScope scope = GlobalSearchScope.fileScope(element.getContainingFile());
                PsiReference[] refs = ReferencesSearch.search(element, scope, false).toArray(PsiReference.EMPTY_ARRAY);
                for (PsiReference ref : refs) {
                    addInlayOnEditor(ref.getElement(), sink);
                }
            }
        });
        return false;
    }
}