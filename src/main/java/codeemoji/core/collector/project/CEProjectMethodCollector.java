package codeemoji.core.collector.project;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiCallExpression;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static codeemoji.core.collector.config.CERuleElement.METHOD;
import static codeemoji.core.collector.config.CERuleFeature.ANNOTATIONS;
import static codeemoji.core.collector.config.CERuleFeature.RETURNS;
import static codeemoji.core.collector.project.ProjectRuleSymbol.ANNOTATIONS_SYMBOL;
import static codeemoji.core.collector.project.ProjectRuleSymbol.RETURNS_SYMBOL;

@Getter
@SuppressWarnings("UnstableApiUsage")
public non-sealed class CEProjectMethodCollector extends CEProjectCollector<PsiMethod, PsiMethodCallExpression>
        implements CEProjectTypesInterface<PsiMethodCallExpression> {

    private final @NotNull String returnsKey;
    private final @NotNull CESymbol returnsSymbol;

    public CEProjectMethodCollector(@NotNull Editor editor, @NotNull String mainKeyId) {
        super(editor, mainKeyId + ".method");
        returnsKey = getMainKeyId() + "." + RETURNS.getValue() + ".tooltip";
        returnsSymbol = new CESymbol();
    }

    @Override
    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitCallExpression(@NotNull PsiCallExpression callExpression) {
                    if (CEUtils.isNotPreviewEditor(editor) &&
                            (callExpression instanceof PsiMethodCallExpression mexp)) {
                        var method = mexp.resolveMethod();
                        if (null != method) {
                            processHint(mexp, method, inlayHintsSink);
                        }
                    }
                    super.visitCallExpression(callExpression);
                }

            });
        }
        return false;
    }

    @Override
    protected void processHint(@NotNull PsiMethodCallExpression addHintElement, @NotNull PsiMethod evaluationElement, @NotNull InlayHintsSink sink) {
        processAnnotationsFR(METHOD, evaluationElement, addHintElement, sink);
        var type = evaluationElement.getReturnType();
        if (!evaluationElement.isConstructor() && null != type) {
            processTypesFR(METHOD, RETURNS, type, addHintElement, sink, getReturnsSymbol(), returnsKey);
        }
    }

    @Override
    public void addInlayTypesFR(@NotNull PsiMethodCallExpression addHintElement, @NotNull List<String> hintValues,
                                @NotNull InlayHintsSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        if (!hintValues.isEmpty()) {
            var inlay = buildInlayWithEmoji(symbol, keyTooltip, String.valueOf(hintValues));
            addInlayInline(addHintElement, sink, inlay);
        }
    }

    @Override
    public int calcOffset(@Nullable PsiMethodCallExpression element) {
        if (null != element) {
            return element.getTextOffset() + element.getMethodExpression().getTextLength();
        }
        return 0;
    }

    @Override
    @NotNull
    public CESymbol getAnnotationsSymbol() {
        return readRuleEmoji(METHOD, ANNOTATIONS, ANNOTATIONS_SYMBOL);
    }

    @NotNull
    private CESymbol getReturnsSymbol() {
        return readRuleEmoji(METHOD, RETURNS, RETURNS_SYMBOL);
    }

}