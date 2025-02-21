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

import java.util.Collection;
import java.util.List;

public class StateIndependentMethod extends CEProviderMulti<StateIndependentMethodSettings> {

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        return List.of(
                new CESimpleMethodCollector(editor, getKey(), mainSymbol()) {
                    @Override
                    protected boolean needsInlay(@NotNull PsiMethod element){
                        return isStateIndependentMethod(element);
                    }
                },

                new CESimpleReferenceMethodCollector(editor, getKey(), mainSymbol()) {
                    @Override
                    protected boolean needsInlay(@NotNull PsiMethod element){
                        return isStateIndependentMethod(element);
                    }
                }
        );
    }

    @Override
    public @NotNull CEConfigurableWindow<StateIndependentMethodSettings> createConfigurable() {
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