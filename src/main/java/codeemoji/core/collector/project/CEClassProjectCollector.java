package codeemoji.core.collector.project;

import codeemoji.core.collector.project.config.CEFeatureRule;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static codeemoji.core.collector.project.config.CEElementRule.CLASS;
import static codeemoji.core.collector.project.config.CEFeatureRule.*;
import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.*;

@Getter
public class CEClassProjectCollector extends CEProjectCollector<PsiClass, PsiElement> {

    public CEClassProjectCollector(@NotNull Editor editor) {
        super(editor);
    }

    public boolean processCollect(@NotNull PsiElement psiElement, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        Map<CEFeatureRule, List<String>> rules = getRules(CLASS);

        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitClass(@NotNull PsiClass clazz) {
                    List<String> hintableValues = checkHintableAnnotations(clazz, rules.get(ANNOTATIONS));
                    if (!hintableValues.isEmpty()) {
                        String keyTooltip = "inlay.showingspecifics.class.annotations.tooltip";
                        InlayPresentation inlay = buildInlay(ANNOTATIONS_SYMBOL, keyTooltip, String.valueOf(hintableValues));
                        addInlayOnEditor(clazz.getNameIdentifier(), inlayHintsSink, inlay);
                    }
                    hintableValues = checkHintableClassRefTypes(rules.get(EXTENDS), clazz.getExtendsList());
                    if (!hintableValues.isEmpty()) {
                        String keyTooltip = "inlay.showingspecifics.class.extends.tooltip";
                        InlayPresentation inlay = buildInlay(EXTENDS_SYMBOL, keyTooltip, String.valueOf(hintableValues));
                        addInlayOnEditor(clazz.getNameIdentifier(), inlayHintsSink, inlay);
                    }
                    hintableValues = checkHintableClassRefTypes(rules.get(IMPLEMENTS), clazz.getImplementsList());
                    if (!hintableValues.isEmpty()) {
                        String keyTooltip = "inlay.showingspecifics.class.implements.tooltip";
                        InlayPresentation inlay = buildInlay(IMPLEMENTS_SYMBOL, keyTooltip, String.valueOf(hintableValues));
                        addInlayOnEditor(clazz.getNameIdentifier(), inlayHintsSink, inlay);
                    }
                }
            });
        }
        return false;
    }

    private @NotNull List<String> checkHintableClassRefTypes(@NotNull List<String> featureValues, PsiReferenceList refList) {
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

}