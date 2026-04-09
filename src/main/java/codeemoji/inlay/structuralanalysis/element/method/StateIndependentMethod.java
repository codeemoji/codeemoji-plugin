package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.base.simple.CESimpleMethodCollector;
import codeemoji.core.collector.base.simple.CESimpleReferenceMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class StateIndependentMethod extends CEProvider<StateIndependentMethodSettings> {

    @Override
    protected void createCollectors(CEProvider<StateIndependentMethodSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addSimpleMethodCollector(this::isStateIndependentMethod);
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<StateIndependentMethodSettings> createConfigurable() {
        return new StateIndependentMethodConfigurable();
    }

    private boolean isStateIndependentMethod(PsiMethod method){

        Collection<PsiReferenceExpression> referenceExpressions = PsiTreeUtil.collectElementsOfType(method.getNavigationElement(), PsiReferenceExpression.class);

        if(
                !method.isConstructor() &&
                method.getBody() != null &&
                referenceExpressions.stream()
                        .<PsiField>mapMulti((referenceExpression, consumer) -> {
                            PsiElement resolvedStateIndependentElement = referenceExpression.resolve();
                            if (resolvedStateIndependentElement instanceof PsiField resolvedField) {
                                consumer.accept(resolvedField);
                            }
                        })
                        .distinct()
                        .findAny()
                        .isEmpty()
        ){
            if(getSettings().isCheckMethodCallsForStateIndependenceApplied()) {

                return referenceExpressions.stream()
                        .<PsiMethod>mapMulti((referenceExpression, consumer) -> {
                            if (referenceExpression.resolve() instanceof PsiMethod referenceMethod && !method.isEquivalentTo(referenceMethod) && !isStateIndependentMethod(referenceMethod)) {
                                consumer.accept(referenceMethod);
                            }
                        })
                        .distinct()
                        .findAny()
                        .isEmpty();
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }
}