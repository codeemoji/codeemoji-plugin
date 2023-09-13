package codeemoji.core.collector.implicit.jpa;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CEJPAUtils {
    ;

    private static final String NS1 = "javax.persistence.";
    private static final String NS2 = "jakarta.persistence.";

    public static @NotNull List<String> buildBaseNames(@NotNull final String name) {
        return Arrays.asList(CEJPAUtils.NS1 + name, CEJPAUtils.NS2 + name);
    }

    public static @NotNull List<String> buildBaseListFor(final String @NotNull ... names) {
        final var resultList = new ArrayList<String>();
        for (final var name : names) {
            resultList.add(CEJPAUtils.NS1 + name);
            resultList.add(CEJPAUtils.NS2 + name);
        }
        return resultList;
    }

    public static @Nullable PsiAnnotation searchAnnotation(@NotNull final PsiMember member, final String searched) {
        final var ann = member.getAnnotation(CEJPAUtils.NS1 + searched);
        return (null == ann) ? member.getAnnotation(CEJPAUtils.NS2 + searched) : ann;
    }
}
