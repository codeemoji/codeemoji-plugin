package codeemoji.core.collector.project;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static codeemoji.core.collector.project.config.CEElementRule.CLASS;
import static codeemoji.core.collector.project.config.CEFeatureRule.EXTENDS;
import static codeemoji.core.collector.project.config.CEFeatureRule.IMPLEMENTS;

@Getter
public abstract class CEProjectClassCollector extends CEProjectCollector<PsiClass, PsiElement>
        implements ICEProjectReferenceList<PsiReferenceList, PsiElement> {

    private final String extendsKey;
    private final String implementsKey;
    private final CESymbol extendsSymbol;
    private final CESymbol implementsSymbol;

    protected CEProjectClassCollector(@NotNull Editor editor, @NotNull String mainKeyId) {
        super(editor, mainKeyId + ".class");
        extendsKey = getMainKeyId() + "." + EXTENDS.getValue() + ".tooltip";
        implementsKey = getMainKeyId() + "." + IMPLEMENTS.getValue() + ".tooltip";
        extendsSymbol = new CESymbol();
        implementsSymbol = new CESymbol();
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

                @Override
                public void visitClass(@NotNull PsiClass psiClass) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        visitClassForRefs(psiClass.getExtendsList());
                        visitClassForRefs(psiClass.getImplementsList());
                    }
                    super.visitClass(psiClass);
                }

                private void visitClassForRefs(@Nullable PsiReferenceList list) {
                    if (list != null) {
                        for (PsiJavaCodeReferenceElement ref : list.getReferenceElements()) {
                            PsiElement resolveElement = ref.resolve();
                            if (resolveElement instanceof PsiClass clazz) {
                                processHint(ref, clazz, inlayHintsSink);
                            }
                        }
                    }
                }
            });
        }
        return false;
    }

    @Override
    public void processHint(@NotNull PsiElement addHintElement, @NotNull PsiClass evaluationElement, @NotNull InlayHintsSink sink) {
        processAnnotationsFR(CLASS, evaluationElement, addHintElement, sink);
        processReferenceListFR(EXTENDS, evaluationElement.getExtendsList(), addHintElement, sink, getExtendsSymbol(), getExtendsKey());
        processReferenceListFR(IMPLEMENTS, evaluationElement.getImplementsList(), addHintElement, sink, getImplementsSymbol(), getImplementsKey());
    }

    @Override
    public void addInlayReferenceListFR(@NotNull PsiElement addHintElement, @NotNull List<String> hintValues,
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