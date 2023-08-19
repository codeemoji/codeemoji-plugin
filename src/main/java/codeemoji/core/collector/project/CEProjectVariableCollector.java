package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEElementRule;
import codeemoji.core.collector.project.config.CEFeatureRule;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static codeemoji.core.collector.project.config.CEElementRule.METHOD;
import static codeemoji.core.collector.project.config.CEFeatureRule.TYPES;

@Getter
public abstract class CEProjectVariableCollector extends CEProjectCollector<PsiVariable, PsiReferenceExpression> {

    private final CEElementRule elementRule;
    private final String tooltipKeyTypes;
    private final CESymbol symbolTypes;

    protected CEProjectVariableCollector(@NotNull Editor editor, @NotNull CEElementRule elementRule) {
        super(editor);
        this.elementRule = elementRule;
        this.tooltipKeyTypes = "";
        this.symbolTypes = new CESymbol();
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
            });
        }
        return false;
    }

    @Override
    public void processHint(@NotNull PsiReferenceExpression addHintElement, @NotNull PsiVariable evaluationElement, @NotNull InlayHintsSink sink) {
        processAnnotationsFR(METHOD, evaluationElement, addHintElement, sink);
        addInlayVariableTypesFR(addHintElement, needsHintVariableFR(evaluationElement.getType()), sink);
    }

    public @NotNull List<String> needsHintVariableFR(@NotNull PsiType type) {
        Map<CEFeatureRule, List<String>> rules = getRules(getElementRule());
        List<String> featureValues = rules.get(TYPES);
        List<String> hintValues = new ArrayList<>();
        if (featureValues != null) {
            for (String value : featureValues) {
                String qualifiedName = "";
                if (type instanceof PsiClassType classType) {
                    qualifiedName = CEUtils.resolveQualifiedName(classType);
                } else {
                    qualifiedName = type.getPresentableText();
                }
                if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                    hintValues.add(value);
                }
            }
        }
        return hintValues;
    }

    private void addInlayVariableTypesFR(@NotNull PsiReferenceExpression addHintElement, @NotNull List<String> hintValues,
                                         @NotNull InlayHintsSink sink) {
        if (!hintValues.isEmpty()) {
            InlayPresentation inlay = buildInlay(getSymbolTypes(), getTooltipKeyTypes(), String.valueOf(hintValues));
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