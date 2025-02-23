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
import static codeemoji.core.config.CERuleElement.CLASS;
import static codeemoji.core.config.CERuleFeature.*;

@Getter
public final class CEProjectClassCollector extends CEProjectCollector<PsiClass, PsiElement>
        implements CEProjectReferenceList<PsiReferenceList, PsiElement> {

    private final @NotNull String extendsKey;
    private final @NotNull String implementsKey;
    private final @NotNull CESymbol extendsSymbol;
    private final @NotNull CESymbol implementsSymbol;

    public CEProjectClassCollector(@NotNull Editor editor, String key) {
        super(editor, key,key + ".class");
        extendsKey = getMainKeyId() + "." + EXTENDS.getValue() + ".tooltip";
        implementsKey = getMainKeyId() + "." + IMPLEMENTS.getValue() + ".tooltip";
        extendsSymbol = CESymbol.empty();
        implementsSymbol = CESymbol.empty();
    }

    @Override
    public PsiElementVisitor createElementVisitor(@NotNull Editor editor, @NotNull InlayTreeSink InlayTreeSink) {
        return new JavaRecursiveElementVisitor() {
            @Override
            public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
                if (CEUtils.isNotPreviewEditor(editor)) {
                    var reference = expression.getReference();
                    if (null != reference) {
                        var resolveElement = reference.resolve();
                        if (resolveElement instanceof PsiClass clazz) {
                            processHint(expression, clazz, InlayTreeSink);
                        }
                    }
                }
                super.visitReferenceExpression(expression);
            }

            @Override
            public void visitVariable(@NotNull PsiVariable variable) {
                var typeElement = variable.getTypeElement();
                if (null != typeElement &&
                        !typeElement.isInferredType() &&
                        typeElement.getType() instanceof PsiClassType classType) {
                    var clazz = classType.resolve();
                    if (null != clazz) {
                        processHint(variable, clazz, InlayTreeSink);
                    }

                }
                super.visitVariable(variable);
            }

            @Override
            public void visitClass(@NotNull PsiClass aClass) {
                if (CEUtils.isNotPreviewEditor(editor)) {
                    visitClassForRefs(aClass.getExtendsList());
                    visitClassForRefs(aClass.getImplementsList());
                }
                super.visitClass(aClass);
            }

            private void visitClassForRefs(@Nullable PsiReferenceList list) {
                if (null != list) {
                    for (var ref : list.getReferenceElements()) {
                        var resolveElement = ref.resolve();
                        if (resolveElement instanceof PsiClass clazz) {
                            processHint(ref, clazz, InlayTreeSink);
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void processHint(@NotNull PsiElement addHintElement, @NotNull PsiClass evaluationElement, @NotNull InlayTreeSink sink) {
        processAnnotationsFR(CLASS, evaluationElement, addHintElement, sink);
        processReferenceListFR(EXTENDS, evaluationElement.getExtendsList(), addHintElement, sink, getExtendsSymbol(), extendsKey);
        processReferenceListFR(IMPLEMENTS, evaluationElement.getImplementsList(), addHintElement, sink, getImplementsSymbol(), implementsKey);
    }

    @Override
    public void addInlayReferenceListFR(@NotNull PsiElement addHintElement, @NotNull List<String> hintValues,
                                        @NotNull InlayTreeSink sink, @NotNull CESymbol symbol, @NotNull String keyTooltip) {
        if (!hintValues.isEmpty()) {
            InlayVisuals inlay = InlayVisuals.translated(symbol, keyTooltip, String.valueOf(hintValues));
            addInlayInline(addHintElement, sink, inlay);
        }
    }

    @Override
    public int calcOffset(@Nullable PsiElement element) {
        if (element instanceof PsiVariable variable) {
            var varName = variable.getNameIdentifier();
            if (null != varName) {
                return varName.getTextOffset() - 1;
            }
        }
        return super.calcOffset(element);
    }

    @Override
    @NotNull
    public CESymbol getAnnotationsSymbol() {
        return readRuleEmoji(CLASS, ANNOTATIONS, ANNOTATIONS_SYMBOL);
    }

    @NotNull
    private CESymbol getExtendsSymbol() {
        return readRuleEmoji(CLASS, EXTENDS, EXTENDS_SYMBOL);
    }

    @NotNull
    private CESymbol getImplementsSymbol() {
        return readRuleEmoji(CLASS, IMPLEMENTS, IMPLEMENTS_SYMBOL);
    }

}