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
public abstract class CEMethodProjectCollector extends CEProjectCollector<PsiMethod, PsiMethodCallExpression> {

    private final String tooltipKeyReturns;
    private final CESymbol symbolReturns;

    protected CEMethodProjectCollector(@NotNull Editor editor) {
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
                            checkHint(mexp, method, inlayHintsSink);
                        }
                    }
                    super.visitCallExpression(callExpression);
                }

            });
        }
        return false;
    }

    @Override
    public void checkHint(@NotNull PsiMethodCallExpression hintElement, @NotNull PsiMethod evaluationElement, @NotNull InlayHintsSink sink) {
        Map<CEFeatureRule, List<String>> rules = getRules(METHOD);

        processHintAnnotations(METHOD, hintElement, evaluationElement, sink);

        if (!evaluationElement.isConstructor()) {
            List<String> hintValues = checkHintMethodReturns(rules.get(RETURNS), evaluationElement.getReturnType());
            if (!hintValues.isEmpty()) {
                InlayPresentation inlay = buildInlay(getSymbolReturns(), getTooltipKeyReturns(), String.valueOf(hintValues));
                addInlay(hintElement, sink, inlay);
            }
        }
    }

    private @NotNull List<String> checkHintMethodReturns(@NotNull List<String> featureValues, PsiType returnType) {
        List<String> result = new ArrayList<>();
        for (String value : featureValues) {
            String qualifiedName = "";
            if (returnType instanceof PsiClassType classType) {
                qualifiedName = CEUtils.resolveQualifiedName(classType);
            } else {
                qualifiedName = returnType.getPresentableText();
            }
            if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                result.add(value);
            }
        }
        return result;
    }

    @Override
    public int calcOffset(@Nullable PsiMethodCallExpression element) {
        if (element != null) {
            return element.getTextOffset() + element.getMethodExpression().getTextLength();
        }
        return 0;
    }

}