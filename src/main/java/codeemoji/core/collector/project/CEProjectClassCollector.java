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

import static codeemoji.core.collector.project.config.CEElementRule.CLASS;
import static codeemoji.core.collector.project.config.CEFeatureRule.EXTENDS;
import static codeemoji.core.collector.project.config.CEFeatureRule.IMPLEMENTS;

@Getter
public abstract class CEProjectClassCollector extends CEProjectCollector<PsiClass, PsiElement> {

    private final String tooltipKeyExtends;
    private final String tooltipKeyImplements;
    private final CESymbol symbolExtends;
    private final CESymbol symbolImplements;

    protected CEProjectClassCollector(@NotNull Editor editor) {
        super(editor);
        tooltipKeyExtends = "";
        tooltipKeyImplements = "";
        symbolExtends = new CESymbol();
        symbolImplements = new CESymbol();
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        PsiReference reference = expression.getReference();
                        if (reference != null) {
                            PsiElement resolveElement = reference.resolve();
                            if (resolveElement instanceof PsiClass clazz) {
                                processHint(expression, clazz, inlayHintsSink);
                            }
                        }
                    }
                    super.visitReferenceExpression(expression);
                }

                @Override
                public void visitVariable(@NotNull PsiVariable variable) {
                    PsiTypeElement typeElement = variable.getTypeElement();
                    if (typeElement != null &&
                            !typeElement.isInferredType() &&
                            typeElement.getType() instanceof PsiClassType classType) {
                        PsiClass clazz = classType.resolve();
                        if (clazz != null) {
                            processHint(variable, clazz, inlayHintsSink);
                        }

                    }
                    super.visitVariable(variable);
                }
            });
        }
        return false;
    }

    @Override
    public void processHint(@NotNull PsiElement addHintElement, @NotNull PsiClass evaluationElement, @NotNull InlayHintsSink sink) {
        processAnnotationsFR(CLASS, evaluationElement, addHintElement, sink);
        addInlayClassFR(addHintElement, needsHintClassFR(EXTENDS, evaluationElement.getExtendsList()), sink,
                getSymbolExtends(), getTooltipKeyExtends());
        addInlayClassFR(addHintElement, needsHintClassFR(IMPLEMENTS, evaluationElement.getImplementsList()), sink,
                getSymbolImplements(), getTooltipKeyImplements());
    }

    private @NotNull List<String> needsHintClassFR(@NotNull CEFeatureRule featureRule, PsiReferenceList refList) {
        Map<CEFeatureRule, List<String>> rules = getRules(CLASS);
        List<String> featureValues = rules.get(featureRule);
        List<String> hintValues = new ArrayList<>();
        if (featureValues != null && (refList != null)) {
            PsiClassType[] refs = refList.getReferencedTypes();
            for (PsiClassType psiType : refs) {
                for (String value : featureValues) {
                    String qualifiedName = CEUtils.resolveQualifiedName(psiType);
                    if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                        hintValues.add(value);
                    }
                }
            }

        }
        return hintValues;
    }

    private void addInlayClassFR(@NotNull PsiElement addHintElement, @NotNull List<String> hintValues,
                                 @NotNull InlayHintsSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        if (!hintValues.isEmpty()) {
            InlayPresentation inlay = buildInlay(symbol, keyTooltip, String.valueOf(hintValues));
            addInlay(addHintElement, sink, inlay);
        }
    }

    @Override
    public int calcOffset(@Nullable PsiElement element) {
        if (element instanceof PsiVariable variable) {
            var varName = variable.getNameIdentifier();
            if (varName != null) {
                return varName.getTextOffset() - 1;
            }
        }
        return super.calcOffset(element);
    }

}