package codeemoji.core.collector.implicit.jpa;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CEJPAUtils {

    private static final String NS1 = "javax.persistence.";
    private static final String NS2 = "jakarta.persistence.";

    private CEJPAUtils() {
    }

    public static @NotNull List<String> buildBaseNames(@NotNull String name) {
        return Arrays.asList(NS1 + name, NS2 + name);
    }

    public static @NotNull List<String> buildBaseListFor(String @NotNull ... names) {
        var resultList = new ArrayList<String>();
        for (var name : names) {
            resultList.add(NS1 + name);
            resultList.add(NS2 + name);
        }
        return resultList;
    }

    public static @Nullable PsiAnnotation searchAnnotation(@NotNull PsiMember member, String searched) {
        PsiAnnotation ann = member.getAnnotation(NS1 + searched);
        return (ann == null) ? member.getAnnotation(NS2 + searched) : ann;
    }
}
