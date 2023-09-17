package codeemoji.core.collector.implicit.spring;

import codeemoji.core.collector.implicit.CEImplicitAttribute;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiMember;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuppressWarnings("UnstableApiUsage")
public class CESpringImplicitRequestMapping extends CESpringImplicitMapping {

    private final @NotNull String baseName;

    public CESpringImplicitRequestMapping() {
        baseName = "org.springframework.web.bind.annotation.RequestMapping";
    }

    @Override
    public @NotNull List<CEImplicitAttribute> createChildrenAttributesFor(@NotNull PsiMember member,
                                                                          @NotNull PsiAnnotation memberAnnotation) {
        List<CEImplicitAttribute> attrList = new ArrayList<>();
        if (null == memberAnnotation.findAttribute("method")) {
            prepareAttribute(member, memberAnnotation, "method", attrList);
        }
        return attrList;
    }
}