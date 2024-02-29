package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
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
                        // Perform operations on the class attribute here
                        this.attribute = this.attribute * 2;
                    }
                }
                """;
    }

    @Override
    protected List<InlayHintsCollector> buildCollectors(Editor editor) {

        return List.of(
                new CEMethodCollector(editor, getKeyId(), STATE_CHANGING_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        // TODO 2: read externalInfo map: if there's a key informing about whether the PureSetterMethod inlay hint is active return false (eg. editor.getInlayModel().hasInlineElementAt(element.getStartOffsetInParent()))
                        return isStateChangingMethod(element);
                    }


                },
                new CEReferenceMethodCollector(editor, getKeyId(), STATE_CHANGING_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return isStateChangingMethod(element);
                    }
                }
        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull StateChangingMethodSettings settings) {
        return new StateChangingMethodConfigurable(settings);
    }

    private boolean isStateChangingMethod(PsiMethod method){
        // Collect assignment expressions to fields
        PsiElement[] stateChangingElements = PsiTreeUtil.collectElements(method.getNavigationElement(), element -> element instanceof PsiAssignmentExpression assignmentExpression && assignmentExpression.getLExpression() instanceof PsiReferenceExpression referenceExpression && referenceExpression.resolve() instanceof PsiField);

        // The method contains 1 or more assignment expressions to fields
        if (stateChangingElements.length > 0) {
            return true;
        }

        // The method does not contain any assignment expressions to fields, check if it invokes other state changing methods
        else {

            // Every method call contained in the currently analyzed method body is recursively checked for assessing whether it changes state
            if(getSettings().isCheckMethodCallsForStateChangeApplied()){

                // Collect non-recursive method calls
                stateChangingElements = PsiTreeUtil.collectElements(method.getNavigationElement(), element -> element instanceof PsiMethodCallExpression methodCallExpression && methodCallExpression.resolveMethod() != null && !method.isEquivalentTo(methodCallExpression.resolveMethod()));

                // The method changes state if any of the method calls contains 1 or more assignment expressions to fields
                return stateChangingElements.length > 0 && Arrays.stream(stateChangingElements).anyMatch(stateChangingElement -> isStateChangingMethod(((PsiMethodCallExpression) stateChangingElement).resolveMethod()));

            }

            // No method call contained in the currently analyzed method body is recursively checked for assessing whether it changes state
            else{
                return false;
            }
        }
    }
}
