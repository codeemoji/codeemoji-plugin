package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicitCollector;
import codeemoji.core.collector.implicit.CEImplicitInterface;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CEJPAEntityCollector extends CEImplicitCollector {

    public final @NotNull String baseName;
    private final @NotNull String nameSpace;

    public CEJPAEntityCollector(@NotNull Editor editor, @NotNull String keyId, int codePoint, @NotNull String nameSpace) {
        super(editor, keyId, codePoint);
        this.nameSpace = nameSpace;
        baseName = nameSpace + ".Entity";
    }

    @Override
    protected void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink) {
        if (member instanceof PsiClass clazz) {
            var implicits = new ArrayList<CEImplicitInterface>();
            implicits.add(new CEJPAImplicitEntity(nameSpace));
            implicits.add(new CEJPAImplicitTable(nameSpace));
            processImplicitsList(clazz, implicits, sink);
        }
        if (member instanceof PsiField field) {
            var implicits = new ArrayList<CEImplicitInterface>();
            implicits.add(new CEJPAImplicitColumn(nameSpace));
            implicits.add(new CEJPAImplicitBasic(nameSpace));
            processImplicitsList(field, implicits, sink);
        }
    }
}
