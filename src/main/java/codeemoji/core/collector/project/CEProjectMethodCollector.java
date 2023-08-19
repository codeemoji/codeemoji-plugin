package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEFeatureRule;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static codeemoji.core.collector.project.config.CEElementRule.METHOD;
import static codeemoji.core.collector.project.config.CEFeatureRule.RETURNS;

@Getter
public abstract class CEProjectMethodCollector extends CEProjectCollector<PsiMethod, PsiMethodCallExpression> {

    private final String tooltipKeyReturns;
    private final CESymbol symbolReturns;

    protected CEProjectMethodCollector(@NotNull Editor editor) {
        super(editor);
        tooltipKeyReturns = "";
        symbolReturns = new CESymbol();
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitCallExpression(@NotNull PsiCallExpression callExpression) {
                    if (CEUtils.isNotPreviewEditor(editor) &&
                            (callExpression instanceof PsiMethodCallExpression mexp)) {
                        PsiMethod method = mexp.resolveMethod();
                        if (method != null) {
                            processHint(mexp, method, inlayHintsSink);
                        }
                    }
                    super.visitCallExpression(callExpression);
                }

            });
        }
        return false;
    }

    @Override
    public void processHint(@NotNull PsiMethodCallExpression addHintElement, @NotNull PsiMethod evaluationElement, @NotNull InlayHintsSink sink) {
        processAnnotationsFR(METHOD, evaluationElement, addHintElement, sink);
        if (!evaluationElement.isConstructor()) {
            addInlayMethodReturnsFR(addHintElement, needsHintMethodReturnsFR(evaluationElement.getReturnType()), sink);
        }
    }

    private @NotNull List<String> needsHintMethodReturnsFR(PsiType type) {
        Map<CEFeatureRule, List<String>> rules = getRules(METHOD);
        List<String> featureValues = rules.get(RETURNS);
        List<String> result = new ArrayList<>();
        if (featureValues != null) {
            for (String value : featureValues) {
                String qualifiedName = "";
                if (type instanceof PsiClassType classType) {
                    qualifiedName = CEUtils.resolveQualifiedName(classType);
                } else {
                    qualifiedName = type.getPresentableText();
                }
                if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                    result.add(value);
                }
            }
        }
        return result;
    }

    private void addInlayMethodReturnsFR(@NotNull PsiMethodCallExpression addHintElement, @NotNull List<String> hintValues,
                                         @NotNull InlayHintsSink sink) {
        if (!hintValues.isEmpty()) {
            InlayPresentation inlay = buildInlay(getSymbolReturns(), getTooltipKeyReturns(), String.valueOf(hintValues));
            addInlay(addHintElement, sink, inlay);
        }
    }

    @Override
    public int calcOffset(@Nullable PsiMethodCallExpression element) {
        if (element != null) {
            return element.getTextOffset() + element.getMethodExpression().getTextLength();
        }
        return 0;
    }

}