package codeemoji.core;

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

    private final boolean activeField = true;
    private final boolean activeParam = true;
    private final boolean activeLocal = true;

    public CEVariableCollector(@NotNull Editor editor, @NotNull String keyId) {
        super(editor, keyId, new CESymbol());
    }

    public CEVariableCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
    }

    @Override
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile file) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitField(@NotNull PsiField field) {
                    if (isActiveField()) {
                        collectForField(file, field, editor, inlayHintsSink);
                    }
                    super.visitField(field);
                }

                @Override
                public void visitParameter(@NotNull PsiParameter parameter) {
                    if (isActiveParam()) {
                        collectForParam(file, parameter, editor, inlayHintsSink);
                    }
                    super.visitParameter(parameter);
                }

                @Override
                public void visitLocalVariable(@NotNull PsiLocalVariable localVariable) {
                    if (isActiveLocal()) {
                        collectForLocal(file, localVariable, editor, inlayHintsSink);
                    }
                    super.visitLocalVariable(localVariable);
                }
            });
        }
        return false;
    }

    public void collectForField(@NotNull PsiJavaFile file, @NotNull PsiField field, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        process(file, field, editor, sink);
    }

    public void collectForParam(@NotNull PsiJavaFile file, @NotNull PsiParameter param, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        process(file, param, editor, sink);
    }

    public void collectForLocal(@NotNull PsiJavaFile file, @NotNull PsiLocalVariable local, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        process(file, local, editor, sink);
    }

    private void process(@NotNull PsiJavaFile file, @NotNull PsiVariable variable, @NotNull Editor editor, @NotNull InlayHintsSink sink) {
        if (isHintable(variable)) {
            addInlayOnEditor(variable.getNameIdentifier(), sink);
            if (CEUtil.isNotPreviewEditor(editor)) {
                GlobalSearchScope scope = GlobalSearchScope.fileScope(variable.getContainingFile());
                PsiReference[] refs = ReferencesSearch.search(variable, scope, false).toArray(PsiReference.EMPTY_ARRAY);
                for (PsiReference ref : refs) {
                    addInlayOnEditor(ref.getElement(), sink);
                }
            } else {
                //TODO: reimplement using GlobalSearchScope
                processReferencesInPreviewEditor(file, variable, sink);
            }
        }
    }

    private void processReferencesInPreviewEditor(@NotNull PsiJavaFile file, @NotNull PsiVariable variable, @NotNull InlayHintsSink inlayHintsSink) {
        file.accept(new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                PsiElement qualifier = CEUtil.identifyFirstQualifier(expression);
                if (qualifier != null && Objects.equals(qualifier.getText(), variable.getName())) {
                    addInlayOnEditor(qualifier, inlayHintsSink);
                }
                super.visitReferenceExpression(expression);
            }
        });
    }
}