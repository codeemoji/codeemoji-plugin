package codeemoji.core.collector.project;

import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
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

    public CEProjectMethodCollector(@NotNull final Editor editor, @NotNull final String mainKeyId) {
        super(editor, mainKeyId + ".method");
        this.returnsKey = this.getMainKeyId() + "." + RETURNS.getValue() + ".tooltip";
        this.returnsSymbol = new CESymbol();
    }

    @Override
    public boolean processCollect(@NotNull final PsiElement psiElement, @NotNull final Editor editor, @NotNull final InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitCallExpression(@NotNull final PsiCallExpression callExpression) {
                    if (CEUtils.isNotPreviewEditor(editor) &&
                            (callExpression instanceof final PsiMethodCallExpression mexp)) {
                        final var method = mexp.resolveMethod();
                        if (null != method) {
                            CEProjectMethodCollector.this.processHint(mexp, method, inlayHintsSink);
                        }
                    }
                    super.visitCallExpression(callExpression);
                }

            });
        }
        return false;
    }

    @Override
    public void processHint(@NotNull final PsiMethodCallExpression addHintElement, @NotNull final PsiMethod evaluationElement, @NotNull final InlayHintsSink sink) {
        this.processAnnotationsFR(METHOD, evaluationElement, addHintElement, sink);
        final var type = evaluationElement.getReturnType();
        if (!evaluationElement.isConstructor() && null != type) {
            this.processTypesFR(METHOD, RETURNS, type, addHintElement, sink, this.getReturnsSymbol(), returnsKey);
        }
    }

    @Override
    public void addInlayTypesFR(@NotNull final PsiMethodCallExpression addHintElement, @NotNull final List<String> hintValues,
                                @NotNull final InlayHintsSink sink, @NotNull final CESymbol symbol, @NotNull final String keyTooltip) {
        if (!hintValues.isEmpty()) {
            final var inlay = this.buildInlayWithEmoji(symbol, keyTooltip, String.valueOf(hintValues));
            this.addInlayInline(addHintElement, sink, inlay);
        }
    }

    @Override
    public int calcOffset(@Nullable final PsiMethodCallExpression element) {
        if (null != element) {
            return element.getTextOffset() + element.getMethodExpression().getTextLength();
        }
        return 0;
    }

    @Override
    public @NotNull CESymbol getAnnotationsSymbol() {
        return this.readRuleEmoji(METHOD, ANNOTATIONS, ANNOTATIONS_SYMBOL);
    }

    @NotNull
    private CESymbol getReturnsSymbol() {
        return this.readRuleEmoji(METHOD, RETURNS, RETURNS_SYMBOL);
    }

}