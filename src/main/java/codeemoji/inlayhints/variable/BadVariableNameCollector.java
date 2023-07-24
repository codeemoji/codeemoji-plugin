package codeemoji.inlayhints.variable;

import codeemoji.core.CodeemojiUtil;
import com.intellij.codeInsight.hints.FactoryInlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BadVariableNameCollector extends FactoryInlayHintsCollector {

    private final BadVariableNameState state;
    private final InlayPresentation inlay;

    public BadVariableNameCollector(@NotNull Editor editor, @NotNull BadVariableNameState state) {
        super(editor);
        PresentationFactory factory = getFactory();
        this.state = state;
        this.inlay = CodeemojiUtil.configureInlayHint(factory, BadVariableNameConfig.NAME, 0x1F90F, false);
    }

    @Override
    public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        if (CodeemojiUtil.isPreviewEditor(editor)) {
            return collectForPreviewEditor(element, editor, sink);
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


    private boolean collectForPreviewEditor(PsiElement element, Editor editor, InlayHintsSink sink) {
        if (element instanceof PsiLocalVariable variable) {
            processInlayHint(variable.getNameIdentifier(), sink);
        } else if (element instanceof PsiReferenceExpression reference) {
            processInlayHint(reference.getQualifier(), sink);
        }
        return false;
    }

    private void processInlayHint(@Nullable PsiElement element, InlayHintsSink sink) {
        if (element != null) {
            int textSize = element.getTextLength();
            if (state.getNumberOfLetters() >= textSize) {
                sink.addInlineElement(element.getTextOffset() + textSize, false, inlay, false);
            }
        }
    }
}