package codeemoji.core.collector.project;

import codeemoji.core.collector.config.CERuleElement;
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

import static codeemoji.core.collector.config.CERuleFeature.ANNOTATIONS;
import static codeemoji.core.collector.config.CERuleFeature.TYPES;
import static codeemoji.core.collector.project.ProjectRuleSymbol.ANNOTATIONS_SYMBOL;
import static codeemoji.core.collector.project.ProjectRuleSymbol.TYPES_SYMBOL;

@Getter
@SuppressWarnings("UnstableApiUsage")
public non-sealed class CEProjectVariableCollector extends CEProjectCollector<PsiVariable, PsiReferenceExpression>
        implements CEProjectTypesInterface<PsiReferenceExpression> {

    private final @NotNull CERuleElement elementRule;
    private final @NotNull String typesKey;
    private final @NotNull CESymbol typesSymbol;

    public CEProjectVariableCollector(@NotNull final Editor editor, @NotNull final CERuleElement elementRule, @NotNull final String mainKeyId) {
        super(editor, mainKeyId + "." + elementRule.getValue());
        this.elementRule = elementRule;
        typesKey = this.getMainKeyId() + "." + TYPES.getValue() + ".tooltip";
        typesSymbol = new CESymbol();
    }

    @Override
    public boolean processCollect(@NotNull final PsiElement psiElement, @NotNull final Editor editor, @NotNull final InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull final PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        final var reference = expression.getReference();
                        if (null != reference) {
                            final var resolveElement = reference.resolve();
                            final var elementRuleType = CEProjectVariableCollector.this.getClassByElementRule();
                            if (null != elementRuleType && (elementRuleType.isInstance(resolveElement))) {
                                CEProjectVariableCollector.this.processHint(expression, (PsiVariable) resolveElement, inlayHintsSink);
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
        switch (this.elementRule) {
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
    public void processHint(@NotNull final PsiReferenceExpression addHintElement, @NotNull final PsiVariable evaluationElement, @NotNull final InlayHintsSink sink) {
        this.processAnnotationsFR(elementRule, evaluationElement, addHintElement, sink);
        this.processTypesFR(elementRule, TYPES, evaluationElement.getType(), addHintElement, sink, this.getTypesSymbol(), typesKey);
    }

    @Override
    public void addInlayTypesFR(@NotNull final PsiReferenceExpression addHintElement, @NotNull final List<String> hintValues,
                                @NotNull final InlayHintsSink sink, @NotNull final CESymbol symbol, @NotNull final String keyTooltip) {
        if (!hintValues.isEmpty()) {
            final var inlay = this.buildInlayWithEmoji(symbol, keyTooltip, String.valueOf(hintValues));
            this.addInlayInline(addHintElement, sink, inlay);
        }
    }

    @Override
    public int calcOffset(@Nullable final PsiReferenceExpression reference) {
        if (null != reference) {
            final var lastChild = reference.getLastChild();
            final var length = lastChild.getTextLength();
            return lastChild.getTextOffset() + length;
        }
        return 0;
    }

    @Override
    public @NotNull CESymbol getAnnotationsSymbol() {
        return this.readRuleEmoji(elementRule, ANNOTATIONS, ANNOTATIONS_SYMBOL);
    }

    public @NotNull CESymbol getTypesSymbol() {
        return this.readRuleEmoji(elementRule, TYPES, TYPES_SYMBOL);
    }
}