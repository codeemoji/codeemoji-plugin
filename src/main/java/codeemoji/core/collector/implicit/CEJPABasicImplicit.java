package codeemoji.core.collector.implicit;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.PsiMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record CEJPABasicImplicit(String name) implements CEIJPAImplicit {

    @Override
    public @Nullable String processAttributes(@NotNull PsiMember member, @NotNull List<JvmAnnotationAttribute> attributes) {
        return null;
    }

    @Override
    public @NotNull String buildAnnotationFor(@NotNull PsiMember member) {
        return "@Basic";
    }
}
