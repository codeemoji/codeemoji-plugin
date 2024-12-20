package codeemoji.inlay.structuralanalysis.element.method;

import codeemoji.core.collector.simple.CESimpleMethodCollector;
import codeemoji.core.collector.simple.CEReferenceMethodCollector;
import codeemoji.core.provider.CEProviderMulti;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static codeemoji.inlay.structuralanalysis.StructuralAnalysisSymbols.PURE_GETTER_METHOD;

@SuppressWarnings("UnstableApiUsage")
public class PureGetterMethod extends CEProviderMulti<PureGetterMethodSettings> {

    @Nullable
    @Override
    public String getPreviewText() {
        return """
                public class PureGetterMethodExample {
                                
                    private int attribute;
                    
                    public PureGetterMethodExample(int attribute){
                        this.attribute = attribute;
                    }
                    
                    private int getAttribute(){
                        return attribute;
                    }
                }
                """;
    }

    @Override
    protected List<InlayHintsCollector> buildCollectors(Editor editor) {
        return List.of(
                new CESimpleMethodCollector(editor, getKey(), PURE_GETTER_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element){
                        return isPureGetterMethod(element);
                    }


                },
                new CEReferenceMethodCollector(editor, getKey(), PURE_GETTER_METHOD) {
                    @Override
                    protected boolean needsHint(@NotNull PsiMethod element){
                        return isPureGetterMethod(element);
                    }
                }
        );
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull PureGetterMethodSettings settings) {
        return new PureGetterMethodConfigurable(settings);
    }

    private boolean isPureGetterMethod(PsiMethod method) {
        final PsiCodeBlock methodBody = method.getBody();
        final PsiStatement[] methodBodyStatements = methodBody != null ? methodBody.getStatements() : null;
        return !method.hasParameters() &&
                methodBody != null && methodBodyStatements.length == 1 &&
                methodBodyStatements[0] instanceof PsiReturnStatement returnStatement &&
                returnStatement.getReturnValue() instanceof PsiReferenceExpression referenceExpression &&
                referenceExpression.resolve() instanceof PsiField field &&
                Objects.equals(field.getContainingClass(), method.getContainingClass()) &&
                (!getSettings().isJavaBeansNamingConventionApplied() || followsJavaBeansGetterNamingConvention(field, method));
    }

    private boolean followsJavaBeansGetterNamingConvention(PsiField field, PsiMethod method) {
        return Objects.equals(
                ((field.getType().getCanonicalText().equals("boolean")) ? "is" : "get") + StringUtils.capitalise(field.getName()),
                method.getName()
        );
    }
}
