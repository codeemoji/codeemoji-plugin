package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.STATE_CHANGING_METHOD;

@SuppressWarnings("UnstableApiUsage")
public class StateChangingMethod extends CEProviderMulti<StateChangingMethodSettings> {
    @Nullable
    @Override
    public String getPreviewText() {
        return """
                public class StateChangingMethodExample {
                    private int attribute;
                                
                    public void stateChangingMethod() {
                        this.attribute = this.attribute * 2;
                    }
                }
                """;
    }

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        return List.of(
                new CESimpleMethodCollector(editor, getKey(), STATE_CHANGING_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element){
                        return isStateChangingMethod(element);
                    }


                },
                new CEReferenceMethodCollector(editor, getKey(), STATE_CHANGING_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element){
                        return isStateChangingMethod(element);
                    }
                }
        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull StateChangingMethodSettings settings) {
        return new StateChangingMethodConfigurable(settings);
    }

    private PsiElement[] collectStateChangingElements(PsiMethod method){
        return PsiTreeUtil.collectElements(
                method.getNavigationElement(),
                element ->
                        element instanceof PsiAssignmentExpression assignmentExpression &&
                        assignmentExpression.getLExpression() instanceof PsiReferenceExpression referenceExpression && referenceExpression.resolve() instanceof PsiField
        );
    }

    private PsiMethod[] collectStateChangingMethods(PsiMethod method){
        return PsiTreeUtil.collectElementsOfType(method.getNavigationElement(), PsiMethodCallExpression.class)
                .stream()
                .distinct()
                .<PsiMethod>mapMulti((methodCallExpression, consumer) -> {
                    PsiMethod resolvedMethodCallExpression = methodCallExpression.resolveMethod();
                    if (resolvedMethodCallExpression != null && !method.isEquivalentTo(resolvedMethodCallExpression)) {
                        consumer.accept(resolvedMethodCallExpression);
                    }
                })
                .toArray(PsiMethod[]::new);
    }

    private boolean isStateChangingMethod(PsiMethod method){

        if (
                !method.isConstructor() &&
                method.getBody() != null &&
                collectStateChangingElements(method).length > 0
        ) {
            return true;
        }

        else {

            if(getSettings().isCheckMethodCallsForStateChangeApplied()){
                return Arrays.stream(collectStateChangingMethods(method)).anyMatch(this::isStateChangingMethod);
            }
            else{
                return false;
            }
        }
    }
}
