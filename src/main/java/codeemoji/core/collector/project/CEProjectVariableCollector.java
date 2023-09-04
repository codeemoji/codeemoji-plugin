package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CERuleElement;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static codeemoji.core.collector.project.ProjectRuleSymbol.ANNOTATIONS_SYMBOL;
import static codeemoji.core.collector.project.ProjectRuleSymbol.TYPES_SYMBOL;
import static codeemoji.core.collector.project.config.CERuleFeature.ANNOTATIONS;
import static codeemoji.core.collector.project.config.CERuleFeature.TYPES;

@Getter
@SuppressWarnings("UnstableApiUsage")
public non-sealed class CEProjectVariableCollector extends CEProjectCollector<PsiVariable, PsiReferenceExpression>
        implements CEIProjectTypes<PsiReferenceExpression> {

    private final CERuleElement elementRule;
    private final String typesKey;
    private final CESymbol typesSymbol;

    public CEProjectVariableCollector(@NotNull Editor editor, @NotNull CERuleElement elementRule, @NotNull String mainKeyId) {
        super(editor, mainKeyId + "." + elementRule.getValue());
        this.elementRule = elementRule;
        this.typesKey = getMainKeyId() + "." + TYPES.getValue() + ".tooltip";
        this.typesSymbol = new CESymbol();
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        var reference = expression.getReference();
                        if (reference != null) {
                            var resolveElement = reference.resolve();
                            var elementRuleType = getClassByElementRule();
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
            var inlay = buildInlay(symbol, keyTooltip, String.valueOf(hintValues));
            addInlay(addHintElement, sink, inlay);
        }
    }

    @Override
    public int calcOffset(@Nullable PsiReferenceExpression reference) {
        if (reference != null) {
            var lastChild = reference.getLastChild();
            var length = lastChild.getTextLength();
            return lastChild.getTextOffset() + length;
        }
        return 0;
    }

    @Override
    public @NotNull CESymbol getAnnotationsSymbol() {
        return readRuleEmoji(getElementRule(), ANNOTATIONS, ANNOTATIONS_SYMBOL);
    }

    public @NotNull CESymbol getTypesSymbol() {
        return readRuleEmoji(getElementRule(), TYPES, TYPES_SYMBOL);
    }
}