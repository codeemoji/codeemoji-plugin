package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.STATE_INDEPENDENT_METHOD;

@SuppressWarnings("UnstableApiUsage")
public class StateIndependentMethod extends CEProviderMulti<StateIndependentMethodSettings> {
    @Nullable
    @Override
    public String getPreviewText() {
        return """
                public class StateIndependentMethodExample {
                                                    
                    public int stateIndependentMethod(int num1, int num2) {
                            int result = num1 + num2;
                            return result;
                    }
                }
                """;
    }

    @Override
    protected List<InlayHintsCollector> buildCollectors(Editor editor) {
        return List.of(
                new CEMethodCollector(editor, getKeyId(), STATE_INDEPENDENT_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isStateIndependentMethod(element);
                    }
                },

                new CEReferenceMethodCollector(editor, getKeyId(), STATE_INDEPENDENT_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isStateIndependentMethod(element);
                    }
                }
        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull StateIndependentMethodSettings settings) {
        return new StateIndependentMethodConfigurable(settings);
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