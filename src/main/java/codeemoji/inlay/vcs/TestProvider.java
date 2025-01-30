package codeemoji.inlay.vcs;

import com.intellij.codeInsight.hints.declarative.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestProvider implements InlayHintsProvider {

    @Nullable
    @Override
    public InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new SharedBypassCollector() {
            @Override
            public void collectFromElement(@NotNull PsiElement psiElement, @NotNull InlayTreeSink inlayTreeSink) {
                // Check if the element is a method call
                if (psiElement instanceof PsiMethodCallExpression) {
                    PsiMethodCallExpression methodCall = (PsiMethodCallExpression) psiElement;

                    PsiMethod method = methodCall.resolveMethod();
                    if (method == null) return;

                    PsiExpressionList argumentList = methodCall.getArgumentList();
                    PsiExpression[] arguments = argumentList.getExpressions();
                    PsiParameter[] parameters = method.getParameterList().getParameters();

                    // Add inlay presentations for each argument
                    for (int i = 0; i < Math.min(arguments.length, parameters.length); i++) {
                        PsiExpression argument = arguments[i];
                        PsiParameter parameter = parameters[i];

                        String hintText = parameter.getName();
                        if (hintText != null) {
                            // Use the InlayTreeSink to add presentations
                            inlayTreeSink.addPresentation(
                                    new EndOfLinePosition(0),
                                    null,  // No payloads
                                    "Parameter: " + hintText,  // Tooltip
                                    true,  // Has background
                                    builder -> {
                                        builder.text("hello",
                                                null);
                                        return Unit.INSTANCE;
                                    } // Presentation content
                            );
                        }
                    }
                }
            }
        };
    }
}
