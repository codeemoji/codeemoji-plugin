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

import static codeemoji.core.collector.config.CERuleElement.CLASS;
import static codeemoji.core.collector.config.CERuleFeature.*;
import static codeemoji.core.collector.project.ProjectRuleSymbol.*;

@Getter
@SuppressWarnings("UnstableApiUsage")
public non-sealed class CEProjectClassCollector extends CEProjectCollector<PsiClass, PsiElement>
        implements CEProjectReferenceListInterface<PsiReferenceList, PsiElement> {

    private final @NotNull String extendsKey;
    private final @NotNull String implementsKey;
    private final @NotNull CESymbol extendsSymbol;
    private final @NotNull CESymbol implementsSymbol;

    public CEProjectClassCollector(@NotNull final Editor editor, @NotNull final String mainKeyId) {
        super(editor, mainKeyId + ".class");
        this.extendsKey = this.getMainKeyId() + "." + EXTENDS.getValue() + ".tooltip";
        this.implementsKey = this.getMainKeyId() + "." + IMPLEMENTS.getValue() + ".tooltip";
        this.extendsSymbol = new CESymbol();
        this.implementsSymbol = new CESymbol();
    }

    @Override
    public boolean processCollect(@NotNull final PsiElement psiElement, @NotNull final Editor editor, @NotNull final InlayHintsSink inlayHintsSink) {
        if (psiElement instanceof PsiJavaFile) {
            psiElement.accept(new JavaRecursiveElementVisitor() {
                @Override
                public void visitReferenceExpression(@NotNull final PsiReferenceExpression expression) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        final var reference = expression.getReference();
                        if (null != reference) {
                            final var resolveElement = reference.resolve();
                            if (resolveElement instanceof final PsiClass clazz) {
                                CEProjectClassCollector.this.processHint(expression, clazz, inlayHintsSink);
                            }
                        }
                    }
                    super.visitReferenceExpression(expression);
                }

                @Override
                public void visitVariable(@NotNull final PsiVariable variable) {
                    final var typeElement = variable.getTypeElement();
                    if (null != typeElement &&
                            !typeElement.isInferredType() &&
                            typeElement.getType() instanceof final PsiClassType classType) {
                        final var clazz = classType.resolve();
                        if (null != clazz) {
                            CEProjectClassCollector.this.processHint(variable, clazz, inlayHintsSink);
                        }

                    }
                    super.visitVariable(variable);
                }

                @Override
                public void visitClass(@NotNull final PsiClass psiClass) {
                    if (CEUtils.isNotPreviewEditor(editor)) {
                        this.visitClassForRefs(psiClass.getExtendsList());
                        this.visitClassForRefs(psiClass.getImplementsList());
                    }
                    super.visitClass(psiClass);
                }

                private void visitClassForRefs(@Nullable final PsiReferenceList list) {
                    if (null != list) {
                        for (final var ref : list.getReferenceElements()) {
                            final var resolveElement = ref.resolve();
                            if (resolveElement instanceof final PsiClass clazz) {
                                CEProjectClassCollector.this.processHint(ref, clazz, inlayHintsSink);
                            }
                        }
                    }
                }
            });
        }
        return false;
    }

    @Override
    public void processHint(@NotNull final PsiElement addHintElement, @NotNull final PsiClass evaluationElement, @NotNull final InlayHintsSink sink) {
        this.processAnnotationsFR(CLASS, evaluationElement, addHintElement, sink);
        this.processReferenceListFR(EXTENDS, evaluationElement.getExtendsList(), addHintElement, sink, this.getExtendsSymbol(), extendsKey);
        this.processReferenceListFR(IMPLEMENTS, evaluationElement.getImplementsList(), addHintElement, sink, this.getImplementsSymbol(), implementsKey);
    }

    @Override
    public void addInlayReferenceListFR(@NotNull final PsiElement addHintElement, @NotNull final List<String> hintValues,
                                        @NotNull final InlayHintsSink sink, @NotNull final CESymbol symbol, @NotNull final String keyTooltip) {
        if (!hintValues.isEmpty()) {
            final var inlay = this.buildInlayWithEmoji(symbol, keyTooltip, String.valueOf(hintValues));
            this.addInlayInline(addHintElement, sink, inlay);
        }
    }

    @Override
    public int calcOffset(@Nullable final PsiElement element) {
        if (element instanceof final PsiVariable variable) {
            final var varName = variable.getNameIdentifier();
            if (null != varName) {
                return varName.getTextOffset() - 1;
            }
        }
        return super.calcOffset(element);
    }

    @Override
    public @NotNull CESymbol getAnnotationsSymbol() {
        return this.readRuleEmoji(CLASS, ANNOTATIONS, ANNOTATIONS_SYMBOL);
    }

    public @NotNull CESymbol getExtendsSymbol() {
        return this.readRuleEmoji(CLASS, EXTENDS, EXTENDS_SYMBOL);
    }

    public @NotNull CESymbol getImplementsSymbol() {
        return this.readRuleEmoji(CLASS, IMPLEMENTS, IMPLEMENTS_SYMBOL);
    }

}