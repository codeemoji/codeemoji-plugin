package codeemoji.core.collector.implicit.spring;

import codeemoji.core.collector.implicit.CEImplicit;
import codeemoji.core.collector.implicit.CEImplicitCollector;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.declarative.InlayTreeSink;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiMethod;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CESpringConfigurationCollector extends CEImplicitCollector {

    public final @NotNull String baseName;

    public CESpringConfigurationCollector(@NotNull Editor editor, @NotNull SettingsKey<?> key, int codePoint) {
        super(editor, key, codePoint);
        baseName = "org.springframework.context.annotation.Configuration";
    }

    @Override
    protected void processImplicitsFor(@NotNull PsiMember member, @NotNull InlayTreeSink sink) {
        if (member instanceof PsiClass clazz) {
            var implicits = new ArrayList<CEImplicit>();
            implicits.add(new CESpringImplicitConfiguration());
            processImplicitsList(clazz, implicits, sink);
        } else if (member instanceof PsiMethod method) {
            var implicits = new ArrayList<CEImplicit>();
            implicits.add(new CESpringImplicitBean());
            processImplicitsList(method, implicits, sink);
        }
    }
}
