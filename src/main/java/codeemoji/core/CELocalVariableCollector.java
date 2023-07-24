package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

public abstract class CELocalVariableCollector extends CECollector {

    public CELocalVariableCollector(@NotNull Editor editor) {
        super(editor);
    }

    @Override
    public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        if (CEUtil.isPreviewEditor(editor)) {
            return collectForPreviewEditor(element, sink);
        }
        element.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitLocalVariable(@NotNull PsiLocalVariable variable) {
                processInlayHint(variable.getNameIdentifier(), sink);
                visitReferencesForElement(variable);
                super.visitLocalVariable(variable);
            }

            private void visitReferencesForElement(@NotNull PsiElement element) {
                GlobalSearchScope scope = GlobalSearchScope.fileScope(element.getContainingFile());
                PsiReference[] refs = ReferencesSearch.search(element, scope, false).toArray(PsiReference.EMPTY_ARRAY);
                for (PsiReference ref : refs) {
                    processInlayHint(ref.getElement(), sink);
                }
            }
        });
        return false;
    }

    @Override
    public boolean collectForPreviewEditor(PsiElement element, InlayHintsSink sink) {
        if (element instanceof PsiLocalVariable variable) {
            PsiElement elem = variable.getNameIdentifier();
            if (elem != null) {
                processInlayHint(elem, sink);
            }
        } else if (element instanceof PsiReferenceExpression reference) {
            PsiElement elem = reference.getQualifier();
            if (elem != null) {
                processInlayHint(elem, sink);
            }
        }
        return false;
    }


}