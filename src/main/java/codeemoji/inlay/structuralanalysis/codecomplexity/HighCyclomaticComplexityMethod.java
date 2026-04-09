package codeemoji.inlay.structuralanalysis.codecomplexity;

import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CEUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.java.*;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class HighCyclomaticComplexityMethod extends CEProvider<HighCyclomaticComplexityMethodSettings> {

    private static final Collection<String> booleanOperators = List.of("&&", "||");
    private static final Collection<Class<? extends PsiStatement>> decisionStatements = List.of(
            PsiIfStatementImpl.class,
            PsiWhileStatementImpl.class,
            PsiForStatementImpl.class,
            PsiTryStatementImpl.class,
            PsiSwitchLabelStatementImpl.class,
            PsiSwitchLabeledRuleStatementImpl.class
    );

    @Override
    protected void createCollectors(CEProvider<HighCyclomaticComplexityMethodSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addSimpleMethodCollector(this::isHighCyclomaticComplexityMethod);
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<HighCyclomaticComplexityMethodSettings> createConfigurable() {
        return new HighCyclomaticComplexityMethodConfigurable();
    }

    private boolean isHighCyclomaticComplexityMethod(PsiMethod method) {
        int numberOfLinesInMethod = CEUtils.calculateMethodBodyLineCount(method) - CEUtils.calculateCommentPaddingLinesInMethod(method);
        int cyclomaticComplexityOfMethod = calculateCyclomaticComplexity(method);
        return method.getBody() != null &&
                cyclomaticComplexityOfMethod > getSettings().getCyclomaticComplexityThreshold() &&
                numberOfLinesInMethod > getSettings().getLineCountStartThreshold() &&
                (((double) cyclomaticComplexityOfMethod / numberOfLinesInMethod) >= getSettings().getCyclomaticComplexityPerLine());
    }

    private int calculateCyclomaticComplexity(PsiMethod method) {
        return Arrays.stream(PsiTreeUtil.collectElements(method.getBody(), HighCyclomaticComplexityMethod::filterCyclomaticallyComplexElements))
                .mapToInt(HighCyclomaticComplexityMethod::mapCyclomaticallyComplexElementToSummableValue)
                .reduce(1, Integer::sum);
    }

    private static boolean filterCyclomaticallyComplexElements(PsiElement element) {
        return decisionStatements.contains(element.getClass()) || (element instanceof PsiJavaToken && booleanOperators.contains(element.getText()));
    }

    private static int mapCyclomaticallyComplexElementToSummableValue(PsiElement element) {
        return ((element instanceof PsiSwitchLabelStatement || element instanceof PsiSwitchLabeledRuleStatement) && !element.getText().startsWith("case")) ? 0 : 1;
    }
}
