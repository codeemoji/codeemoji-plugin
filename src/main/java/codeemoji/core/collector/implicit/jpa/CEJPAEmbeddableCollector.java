package codeemoji.core.collector.implicit.jpa;

import codeemoji.core.collector.implicit.CEImplicit;
import codeemoji.core.collector.implicit.CEImplicitCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CEJPAEmbeddableCollector extends CEImplicitCollector {

    public final @NotNull String baseName;
    private final @NotNull String nameSpace;

    public CEJPAEmbeddableCollector(@NotNull Editor editor, @NotNull String keyId, int codePoint, @NotNull String nameSpace) {
        super(editor, keyId, codePoint);
        this.nameSpace = nameSpace;
        baseName = nameSpace + ".Embeddable";
    }

    @Override
    protected void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink) {
        if (member instanceof PsiField field) {
            var implicits = new ArrayList<CEImplicit>();
            implicits.add(new CEJPAImplicitColumn(nameSpace));
            implicits.add(new CEJPAImplicitBasic(nameSpace));
            processImplicitsList(field, implicits, sink);
        }
    }
}
