package codeemoji.core;

import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class CELocalVariableCollector extends CECollector<PsiLocalVariable, PsiElement> {

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
    public boolean collect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitLocalVariable(@NotNull PsiLocalVariable variable) {
                    if (putHintHere(variable)) {
                        addInlayOnEditor(variable.getNameIdentifier(), inlayHintsSink);
                        psiElement.accept(new JavaRecursiveElementVisitor() {
                            @Override
                            public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                                PsiElement qualifier = CEUtil.identifyFirstQualifier(expression);
                                if (qualifier != null && Objects.equals(qualifier.getText(), variable.getName())) {
                                    addInlayOnEditor(qualifier, inlayHintsSink);
                                }
                            }
                        });
                    }
                    super.visitLocalVariable(variable);
                }
            });
        }
        return false;
    }
}