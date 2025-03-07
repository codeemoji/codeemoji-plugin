package codeemoji.core.collector.project;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static codeemoji.core.collector.project.ProjectRuleSymbol.*;
import static codeemoji.core.config.CERuleElement.METHOD;
import static codeemoji.core.config.CERuleFeature.*;

@Getter
public final class CEProjectMethodCollector extends CEProjectCollector<PsiMethod, PsiMethodCallExpression>
        implements CEProjectTypes<PsiMethodCallExpression>, CEProjectPackages<PsiMethodCallExpression> {

    private final @NotNull String returnsKey;
    private final @NotNull String packagesKey;

    private final @NotNull CESymbol returnsSymbol;
    private final @NotNull CESymbol packagesSymbol;

    public CEProjectMethodCollector(@NotNull Editor editor, @NotNull String key) {
        super(editor, key, key + ".method");
        returnsKey = getMainKeyId() + "." + RETURNS.getValue() + ".tooltip";
        returnsSymbol = CESymbol.empty();

        packagesKey = getMainKeyId() + "." + PACKAGES.getValue() + ".tooltip";
        packagesSymbol = CESymbol.empty();
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitCallExpression(@NotNull PsiCallExpression callExpression) {
                if (CEUtils.isNotPreviewEditor(editor) &&
                        (callExpression instanceof PsiMethodCallExpression mexp)) {
                    var method = mexp.resolveMethod();
                    if (null != method) {
                        processHint(mexp, method, InlayTreeSink);
                    }
                }
                super.visitCallExpression(callExpression);
            }
        };
    }

    @Override
    protected void processHint(@NotNull PsiMethodCallExpression addHintElement, @NotNull PsiMethod evaluationElement, @NotNull InlayTreeSink sink) {
        processAnnotationsFR(METHOD, evaluationElement, addHintElement, sink);
        var type = evaluationElement.getReturnType();
        if (!evaluationElement.isConstructor() && null != type) {
            processTypesFR(METHOD, RETURNS, type, addHintElement, sink, getReturnsSymbol(), returnsKey);
        }
        if (evaluationElement.getContainingFile() instanceof PsiJavaFile javaFile && javaFile.getPackageStatement() != null) {
            processStructuralAnalysisFR(METHOD, PACKAGES, javaFile.getPackageStatement(), addHintElement, sink, getPackagesSymbol(), packagesKey);
        }
    }

    @Override
    public void addInlayTypesFR(@NotNull PsiMethodCallExpression addHintElement, @NotNull List<String> hintValues,
                                @NotNull InlayTreeSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        if (!hintValues.isEmpty()) {
            InlayVisuals inlay = InlayVisuals.translated(symbol, keyTooltip, String.valueOf(hintValues));
            addInlayInline(addHintElement, sink, inlay);
        }
    }

    @Override
    public void addInlayStructuralAnalysisFR(@NotNull PsiMethodCallExpression addHintElement, @NotNull List<String> hintValues, @NotNull InlayTreeSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        if (!hintValues.isEmpty()) {
            var inlay = InlayVisuals.translated(symbol, keyTooltip, String.valueOf(hintValues));
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

    private CESymbol getPackagesSymbol() {
        return readRuleEmoji(METHOD, PACKAGES, PACKAGES_SYMBOL);
    }
}