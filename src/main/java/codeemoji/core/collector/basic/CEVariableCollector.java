package codeemoji.core.collector.basic;

import codeemoji.core.collector.CESingleCollector;
import codeemoji.core.util.CESymbol;
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
@SuppressWarnings("UnstableApiUsage")
public abstract class CEVariableCollector extends CESingleCollector<PsiVariable, PsiElement> {

    private final boolean enabledForField;
    private final boolean enabledForParam;
    private final boolean enabledForLocalVariable;

    protected CEVariableCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable CESymbol symbol) {
        super(editor, keyId, symbol);
        enabledForField = true;
        enabledForParam = true;
        enabledForLocalVariable = true;

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
        if (needsHint(variable)) {
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

    private void processReferencesInPreviewEditor(@NotNull PsiVariable variable, @NotNull InlayHintsSink inlayHintsSink) {
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

    @Override
    public int calcOffset(@Nullable PsiElement element) {
        if (element != null) {
            var length = element.getTextLength();
            var attr = "this.";
            if (element.getText().contains(attr)) {
                length -= attr.length();
            }
            return element.getTextOffset() + length;
        }
        return 0;
    }
}