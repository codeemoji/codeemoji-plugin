package codeemoji.core.collector.implicit;

import com.intellij.lang.jvm.annotation.JvmAnnotationAttribute;
import com.intellij.psi.PsiMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CEIJPAImplicit {

    String name();

    @Nullable String processAttributes(@NotNull PsiMember member, @NotNull List<JvmAnnotationAttribute> attributes);

    @Nullable String buildAnnotationFor(@NotNull PsiMember member);
}
