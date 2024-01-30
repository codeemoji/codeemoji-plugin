package codeemoji.inlay.structuralinspection.method;

import codeemoji.core.collector.simple.CEMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import static codeemoji.inlay.structuralinspection.StructuralInspectionSymbols.SIDE_EFFECTED_METHOD;

public class SideEffectedMethod extends CEProviderMulti<NoSettings> {


    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    protected List<InlayHintsCollector> buildCollectors(Editor editor) {
        return List.of(

                new CEMethodCollector(editor, getKeyId(), SIDE_EFFECTED_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return sideEffectPredicate().test(false, element);
                    }
                },

                new CEReferenceMethodCollector(editor, getKeyId(), SIDE_EFFECTED_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
                        return sideEffectPredicate().test(true, element);
                    }
                }

        );
    }

    private static BiPredicate<Boolean, PsiMethod> sideEffectPredicate(){

        return (fromLocallyScopedVariable, method) -> {

            boolean sideEffected = false;
            boolean locallyScoped = false;

            for (PsiReferenceExpression referenceExpression : PsiTreeUtil.collectElementsOfType(method, PsiReferenceExpression.class)){

                // Check: is the reference a method?
                if(referenceExpression.resolve() instanceof PsiMethod methodReference) {

                    // TRUE => Check: is it qualified?
                    if (referenceExpression.isQualified() && referenceExpression.getQualifierExpression() != null){

                        // TRUE => Resolve the qualifier directly or indirectly (ie. through an array access, return value of method invocation)
                        if(referenceExpression.getQualifierExpression() instanceof PsiArrayAccessExpression arrayAccessExpression) { referenceExpression = (PsiReferenceExpression) arrayAccessExpression.getArrayExpression(); }
                        else if(referenceExpression.getQualifierExpression() instanceof PsiReferenceExpression) {
                            referenceExpression = (PsiReferenceExpression) referenceExpression.getQualifierExpression();
                        }

                        locallyScoped =  referenceExpression.resolve() instanceof PsiLocalVariable;
                    }

                    // TRUE => Check: that neither of these assertions hold:
                    // (a) the reference locally scoped (ie. not a local variable nor a static field
                    // (b) the recursive invocation of the predicate on the method yields that the referenced implementations are side effected, implying the original method being analyzed is side effected as well
                    if(
                            !locallyScoped || sideEffectPredicate().test(locallyScoped, methodReference)
                    ){

                        // TRUE => The qualifier references a scope that is external to the local method scope, implying a side effect
                        sideEffected = true;
                        break;
                    }
                }
                else {

                    // FALSE => Check: the element is one of these disjointed assertions:
                    // (a) a non-local variable (referenced directly or indirectly - through array access)
                    // (b) a static field
                    // (c) the reference doesn't originate from a locally scoped element that is unqualified
                    if(
                            (
                                    !(referenceExpression.resolve() instanceof PsiLocalVariable) ||
                                            (
                                                    (referenceExpression.getQualifierExpression() instanceof PsiArrayAccessExpression arrayAccessExpression) &&
                                                            (((PsiReferenceExpression)arrayAccessExpression.getArrayExpression()).resolve() instanceof PsiLocalVariable)
                                            )
                            ) &&
                                    (
                                            (
                                                    !fromLocallyScopedVariable &&
                                                            !referenceExpression.isQualified() &&
                                                            referenceExpression.getQualifierExpression() == null
                                            ) ||
                                                    (referenceExpression.resolve() instanceof PsiField field && field.hasModifier(JvmModifier.STATIC))
                                    )
                    ){
                        sideEffected = true;
                        break;
                    }
                }

            }

            return sideEffected;

        };
    };
}
