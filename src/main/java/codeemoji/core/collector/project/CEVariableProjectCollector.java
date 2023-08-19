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

import static codeemoji.core.collector.project.config.CEFeatureRule.TYPES;

@Getter
public abstract class CEVariableProjectCollector extends CEProjectCollector<PsiVariable, PsiReferenceExpression> {

    private final CEElementRule elementRule;
    private final String tooltipKeyTypes;
    private final CESymbol symbolTypes;

    protected CEVariableProjectCollector(@NotNull Editor editor, @NotNull CEElementRule elementRule) {
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
                            Class<? extends PsiVariable> elementRuleType = getClassByCEElement();
                            if (elementRuleType != null) {
                                if (elementRuleType.isInstance(resolveElement)) {
                                    checkHint(expression, (PsiVariable) resolveElement, inlayHintsSink);
                                }
                            } else {
                                System.out.println("CEVariableProjectCollector is implemented only for FIELD, LOCALVARIALBE, and PARAMETER");
                            }
                        }
                    }
                    super.visitReferenceExpression(expression);
                }

                @Contract(pure = true)
                private @Nullable Class<? extends PsiVariable> getClassByCEElement() {
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
    public void checkHint(@NotNull PsiReferenceExpression hintElement, @NotNull PsiVariable evaluationElement, @NotNull InlayHintsSink sink) {
        processHintAnnotations(getElementRule(), hintElement, evaluationElement, sink);
        processHintTypes(getElementRule(), hintElement, evaluationElement.getType(), sink);
    }

    public void processHintTypes(@NotNull CEElementRule elementRule, @NotNull PsiReferenceExpression hintElement, @NotNull PsiType type,
                                 @NotNull InlayHintsSink sink) {
        Map<CEFeatureRule, List<String>> rules = getRules(elementRule);
        List<String> featureValues = rules.get(TYPES);
        if (featureValues != null) {
            List<String> hintValues = new ArrayList<>();
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
            if (!hintValues.isEmpty()) {
                InlayPresentation inlay = buildInlay(getSymbolTypes(), getTooltipKeyTypes(), String.valueOf(hintValues));
                addInlay(hintElement, sink, inlay);
            }
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