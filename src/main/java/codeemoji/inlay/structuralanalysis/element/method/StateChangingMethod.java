package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.collector.simple.CESimpleReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class StateChangingMethod extends CEProviderMulti<StateChangingMethodSettings> {

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        return List.of(
                new CESimpleMethodCollector(editor, getKey(), mainSymbol()) {
                    @Override
                    protected boolean needsInlay(@NotNull PsiMethod element){
                        return isStateChangingMethod(element);
                    }


                },
                new CESimpleReferenceMethodCollector(editor, getKey(), mainSymbol()) {
                    @Override
                    protected boolean needsInlay(@NotNull PsiMethod element){
                        return isStateChangingMethod(element);
                    }
                }
        );
    }

    @Override
    public @NotNull CEConfigurableWindow<StateChangingMethodSettings> createConfigurable() {
        return new StateChangingMethodConfigurable();
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
