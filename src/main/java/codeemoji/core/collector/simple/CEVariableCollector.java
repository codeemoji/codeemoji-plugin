package codeemoji.core.collector.simple;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
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
public abstract non-sealed class CEVariableCollector extends CESimpleCollector<PsiVariable, PsiElement> {

    private boolean enabledForField;
    private boolean enabledForParam;
    private boolean enabledForLocalVariable;

    protected CEVariableCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key, @Nullable CESymbol symbol) {
        super(editor, key, symbol);
        enabledForField = true;
        enabledForParam = true;
        enabledForLocalVariable = true;
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        return new JavaRecursiveElementVisitor() {
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
                var inlay = createInlayFor(variable);
                if (inlay != null) {
                    addInlayInline(variable.getNameIdentifier(), sink, inlay);
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        var scope = GlobalSearchScope.fileScope(variable.getContainingFile());
                        var refs = ReferencesSearch.search(variable, scope, false).toArray(PsiReference.EMPTY_ARRAY);
                        for (var ref : refs) {
                            addInlayInline(ref.getElement(), sink, inlay);
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
                            var inlay = createInlayFor(null); //TODO: wrong! this is a hack
                            if (inlay != null) {
                                addInlayInline(expression, inlayHintsSink, inlay);
                            }
                        }
                        super.visitReferenceExpression(expression);
                    }
                });
            }
        };
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