package codeemoji.core.collector.implicit.spring;

import codeemoji.core.collector.implicit.CEImplicitCollector;
import codeemoji.core.collector.implicit.CEImplicitInterface;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CESpringConfigurationCollector extends CEImplicitCollector {

    public final @NotNull String baseName;

    public CESpringConfigurationCollector(@NotNull Editor editor, @NotNull String keyId, int codePoint) {
        super(editor, keyId, codePoint);
        baseName = "org.springframework.context.annotation.Configuration";
    }

    @Override
    protected void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink) {
        if (member instanceof PsiMethod method) {
            var implicits = new ArrayList<CEImplicitInterface>();
            implicits.add(new CESpringImplicitBean());
            processImplicitsList(method, implicits, sink);
        }
    }
}
