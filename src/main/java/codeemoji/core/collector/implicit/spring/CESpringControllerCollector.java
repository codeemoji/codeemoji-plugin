package codeemoji.core.collector.implicit.spring;

import codeemoji.core.collector.implicit.CEImplicit;
import codeemoji.core.collector.implicit.CEImplicitCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Getter
@Setter
@SuppressWarnings("UnstableApiUsage")
public class CESpringControllerCollector extends CEImplicitCollector {

    public @NotNull String baseName;

    public CESpringControllerCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key, int codePoint) {
        super(editor, key, codePoint);
        baseName = "org.springframework.stereotype.Controller";
    }

    @Override
    protected void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayHintsSink sink) {
        if (member instanceof PsiClass clazz) {
            var implicits = new ArrayList<CEImplicit>();
            implicits.add(new CESpringImplicitRequestMapping());
            processImplicitsList(clazz, implicits, sink);
        } else if (member instanceof PsiMethod method) {
            var implicits = new ArrayList<CEImplicit>();
            implicits.add(new CESpringImplicitRequestMapping());
            implicits.add(new CESpringImplicitGetMapping());
            implicits.add(new CESpringImplicitPostMapping());
            implicits.add(new CESpringImplicitPutMapping());
            implicits.add(new CESpringImplicitPatchMapping());
            implicits.add(new CESpringImplicitDeleteMapping());
            processImplicitsList(method, implicits, sink);
        }
    }
}
