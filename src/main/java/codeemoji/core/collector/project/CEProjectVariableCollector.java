package codeemoji.core.collector.project;

import codeemoji.core.config.CERuleElement;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static codeemoji.core.collector.project.ProjectRuleSymbol.ANNOTATIONS_SYMBOL;
import static codeemoji.core.collector.project.ProjectRuleSymbol.TYPES_SYMBOL;
import static codeemoji.core.config.CERuleFeature.ANNOTATIONS;
import static codeemoji.core.config.CERuleFeature.TYPES;

@Getter
public final class CEProjectVariableCollector extends CEProjectCollector<PsiVariable, PsiReferenceExpression>
        implements CEProjectTypes<PsiReferenceExpression> {

    private final @NotNull CERuleElement elementRule;
    private final @NotNull String typesKey;
    private final @NotNull CESymbol typesSymbol;

    public CEProjectVariableCollector(@NotNull Editor editor, String key, @NotNull CERuleElement elementRule) {
        super(editor, key, key + "." + elementRule.getValue());
        this.elementRule = elementRule;
        typesKey = getMainKeyId() + "." + TYPES.getValue() + ".tooltip";
        typesSymbol = CESymbol.empty();
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                if (CEUtils.isNotPreviewEditor(editor)) {
                    var reference = expression.getReference();
                    if (null != reference) {
                        var resolveElement = reference.resolve();
                        var elementRuleType = getClassByElementRule();
                        if (null != elementRuleType && (elementRuleType.isInstance(resolveElement))) {
                            processHint(expression, (PsiVariable) resolveElement, InlayTreeSink);
                        }
                    }
                }
                super.visitReferenceExpression(expression);
            }

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
        };
    }

    @Override
    protected void processHint(@NotNull PsiReferenceExpression addHintElement, @NotNull PsiVariable evaluationElement, @NotNull InlayTreeSink sink) {
        processAnnotationsFR(elementRule, evaluationElement, addHintElement, sink);
        processTypesFR(elementRule, TYPES, evaluationElement.getType(), addHintElement, sink, getTypesSymbol(), typesKey);
    }

    @Override
    public void addInlayTypesFR(@NotNull PsiReferenceExpression addHintElement, @NotNull List<String> hintValues,
                                @NotNull InlayTreeSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        if (!hintValues.isEmpty()) {
            var inlay = buildInlayWithEmoji(symbol, keyTooltip, String.valueOf(hintValues));
            addInlayInline(addHintElement, sink, inlay);
        }
    }

    @Override
    public int calcOffset(@Nullable PsiReferenceExpression reference) {
        if (null != reference) {
            var lastChild = reference.getLastChild();
            var length = lastChild.getTextLength();
            return lastChild.getTextOffset() + length;
        }
        return 0;
    }

    @Override
    @NotNull
    public CESymbol getAnnotationsSymbol() {
        return readRuleEmoji(elementRule, ANNOTATIONS, ANNOTATIONS_SYMBOL);
    }

    @NotNull
    private CESymbol getTypesSymbol() {
        return readRuleEmoji(elementRule, TYPES, TYPES_SYMBOL);
    }
}