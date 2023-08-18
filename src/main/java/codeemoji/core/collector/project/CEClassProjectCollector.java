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
import static codeemoji.core.collector.project.config.CEFeatureRule.*;

@Getter
public abstract class CEClassProjectCollector extends CEProjectCollector<PsiClass, PsiElement> {

    private final String tooltipKeyExtends;
    private final String tooltipKeyImplements;
    private final CESymbol symbolExtends;
    private final CESymbol symbolImplements;

    protected CEClassProjectCollector(@NotNull Editor editor) {
        super(editor);
        tooltipKeyExtends = "";
        tooltipKeyImplements = "";
        symbolExtends = new CESymbol();
        symbolImplements = new CESymbol();
    }

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
                                checkHint(expression, clazz, inlayHintsSink);
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
                            checkHint(variable, clazz, inlayHintsSink);
                        }

                    }
                    super.visitVariable(variable);
                }
            });
        }
        return false;
    }

    public void checkHint(@NotNull PsiElement hintElement, @NotNull PsiClass evaluationElement, @NotNull InlayHintsSink sink) {
        Map<CEFeatureRule, List<String>> rules = getRules(CLASS);
        List<String> hintValues = checkHintAnnotations(evaluationElement, rules.get(ANNOTATIONS));
        if (!hintValues.isEmpty()) {
            InlayPresentation inlay = buildInlay(getSymbolAnnotations(), getTooltipKeyAnnotations(), String.valueOf(hintValues));
            addInlay(hintElement, sink, inlay);
        }
        hintValues = checkHintClassRefTypes(rules.get(EXTENDS), evaluationElement.getExtendsList());
        if (!hintValues.isEmpty()) {
            InlayPresentation inlay = buildInlay(getSymbolExtends(), getTooltipKeyExtends(), String.valueOf(hintValues));
            addInlay(hintElement, sink, inlay);
        }
        hintValues = checkHintClassRefTypes(rules.get(IMPLEMENTS), evaluationElement.getImplementsList());
        if (!hintValues.isEmpty()) {
            InlayPresentation inlay = buildInlay(getSymbolImplements(), getTooltipKeyImplements(), String.valueOf(hintValues));
            addInlay(hintElement, sink, inlay);
        }
    }

    private @NotNull List<String> checkHintClassRefTypes(@NotNull List<String> featureValues, PsiReferenceList refList) {
        List<String> result = new ArrayList<>();
        if (refList != null) {
            PsiClassType[] refs = refList.getReferencedTypes();
            for (PsiClassType psiType : refs) {
                for (String value : featureValues) {
                    String qualifiedName = CEUtils.resolveQualifiedName(psiType);
                    if (qualifiedName != null && qualifiedName.equalsIgnoreCase(value)) {
                        result.add(value);
                    }
                }
            }
        }
        return result;
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