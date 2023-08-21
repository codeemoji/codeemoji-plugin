package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEElementRule;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static codeemoji.core.collector.project.config.CEFeatureRule.TYPES;

@Getter
public abstract class CEProjectVariableCollector extends CEProjectCollector<PsiVariable, PsiReferenceExpression>
        implements ICEProjectTypes<PsiReferenceExpression> {

    private final CEElementRule elementRule;
    private final String typesKey;
    private final CESymbol typesSymbol;

    protected CEProjectVariableCollector(@NotNull Editor editor, @NotNull CEElementRule elementRule, @NotNull String mainKeyId) {
        super(editor, mainKeyId + "." + elementRule.getValue());
        this.elementRule = elementRule;
        this.typesKey = getMainKeyId() + "." + TYPES.getValue() + ".tooltip";
        this.typesSymbol = new CESymbol();
    }

    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        PsiReference reference = expression.getReference();
                        if (reference != null) {
                            PsiElement resolveElement = reference.resolve();
                            Class<? extends PsiVariable> elementRuleType = getClassByElementRule();
                            if (elementRuleType != null && (elementRuleType.isInstance(resolveElement))) {
                                processHint(expression, (PsiVariable) resolveElement, inlayHintsSink);
                            }
                        }
                    }
                    super.visitReferenceExpression(expression);
                }
            });
        }
        return false;
    }

    @Contract(pure = true)
    private @Nullable Class<? extends PsiVariable> getClassByElementRule() {
        switch (elementRule) {
            case FIELD -> {
                return PsiField.class;
            }
            case LOCALVARIABLE -> {
                return PsiLocalVariable.class;
            }
            case PARAMETER -> {
                return PsiParameter.class;
            }
            default -> {
                return null;
            }
        }
    }

    @Override
    public void processHint(@NotNull PsiReferenceExpression addHintElement, @NotNull PsiVariable evaluationElement, @NotNull InlayHintsSink sink) {
        processAnnotationsFR(getElementRule(), evaluationElement, addHintElement, sink);
        processTypesFR(getElementRule(), TYPES, evaluationElement.getType(), addHintElement, sink, getTypesSymbol(), getTypesKey());
    }

    @Override
    public void addInlayTypesFR(@NotNull PsiReferenceExpression addHintElement, @NotNull List<String> hintValues,
                                @NotNull InlayHintsSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        if (!hintValues.isEmpty()) {
            InlayPresentation inlay = buildInlay(symbol, keyTooltip, String.valueOf(hintValues));
            addInlay(addHintElement, sink, inlay);
        }
    }

    @Override
    public int calcOffset(@Nullable PsiReferenceExpression reference) {
        if (reference != null) {
            PsiElement lastChild = reference.getLastChild();
            int length = lastChild.getTextLength();
            return lastChild.getTextOffset() + length;
        }
        return 0;
    }
}