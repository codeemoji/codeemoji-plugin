package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitInterface;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CEJPAEntityCollector extends CEJPACollector {

    public final @NotNull List<String> baseNames;

    public CEJPAEntityCollector(@NotNull Editor editor, @NotNull String keyId, int codePoint) {
        super(editor, keyId, codePoint);
        this.baseNames = CEJPAUtils.buildBaseNames("Entity");
    }

    @Override
    public void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink) {
        if (member instanceof PsiClass clazz) {
            var implicits = new ArrayList<CEImplicitInterface>();
            implicits.add(new CEJPAImplicitEntity());
            implicits.add(new CEJPAImplicitTable());
            processImplicits(clazz, implicits, sink);
        }
        if (member instanceof PsiField field) {
            var implicits = new ArrayList<CEImplicitInterface>();
            implicits.add(new CEJPAImplicitColumn());
            implicits.add(new CEJPAImplicitBasic());
            processImplicits(field, implicits, sink);
        }
    }
}
