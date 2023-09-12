package codeemoji.core.collector.implicit;

import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CEJPAEntityCollector extends CEImplicitCollector {

    public final String keyId;
    public final Integer codePoint;
    public final List<String> baseNames;

    public CEJPAEntityCollector(@NotNull Editor editor, @NotNull String keyId, @Nullable Integer codePoint) {
        super(editor);
        this.keyId = keyId;
        this.codePoint = codePoint;
        this.baseNames = CEJPAPersistenceUtils.buildBaseNames("Entity");
    }

    @Override
    public void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink) {
        if (member instanceof PsiClass clazz) {
            var implicits = new ArrayList<CEIJPAImplicit>();
            implicits.add(new CEJPAImplicitEntity());
            implicits.add(new CEJPAImplicitTable());
            processImplicits(clazz, implicits, sink);
        }
        if (member instanceof PsiField field) {
            var implicits = new ArrayList<CEIJPAImplicit>();
            implicits.add(new CEJPAImplicitColumn());
            implicits.add(new CEJPAImplicitBasic());
            processImplicits(field, implicits, sink);
        }
    }

    private void processImplicits(@NotNull PsiMember member, @NotNull List<CEIJPAImplicit> implicits, @NotNull InlayHintsSink sink) {
        for (var implicit : implicits) {
            var hasImplicitAnnotation = false;
            for (var name : implicit.getBaseNames()) {
                var annotation = member.getAnnotation(name);
                if (annotation != null) {
                    var complement = implicit.processAttributes(member, annotation);
                    addImplicitInlayForAttributes(annotation, complement, sink);
                    hasImplicitAnnotation = true;
                    break;
                }
            }
            if (!hasImplicitAnnotation) {
                var complement = implicit.buildAnnotationFor(member);
                if (complement != null) {
                    addImplicitInlay(member, complement, sink);
                }
            }
        }
    }

    private void addImplicitInlayForAttributes(PsiAnnotation annotation, @Nullable String complement, @NotNull InlayHintsSink sink) {
        if (complement != null) {
            var inlay = buildInlayWithText(complement, "inlay." + getKeyId() + ".attributes.tooltip", null);
            addInlayInAnnotation(annotation, sink, inlay);
        }
    }

    private void addImplicitInlay(PsiElement element, @Nullable String fullText, @NotNull InlayHintsSink sink) {
        if (fullText != null) {
            var symbol = new CESymbol(getCodePoint(), fullText);
            var inlay = buildInlayWithEmoji(symbol, "inlay." + getKeyId() + ".annotations.tooltip", null);
            addInlayBlock(element, sink, inlay);
        }
    }


}
