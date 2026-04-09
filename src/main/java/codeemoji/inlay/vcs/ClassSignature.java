package codeemoji.inlay.vcs;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiTypeParameter;
import gr.uom.java.xmi.UMLClass;
import gr.uom.java.xmi.UMLTypeParameter;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
public final class ClassSignature {
    private final String qualifiedName;
    private final List<String> typeParameters;

    private static final Cache<PsiClass, ClassSignature> PSI_CACHE = CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .weakKeys()
            .build();

    private ClassSignature(String qualifiedName, List<String> typeParameters) {
        this.qualifiedName = qualifiedName;
        this.typeParameters = Collections.unmodifiableList(typeParameters);
    }

    public static ClassSignature from(PsiClass psiClass) {
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName == null) qualifiedName = "";

        PsiTypeParameter[] psiTypeParameters = psiClass.getTypeParameters();
        List<String> typeParams = new ArrayList<>(psiTypeParameters.length);
        for (PsiTypeParameter param : psiTypeParameters) {
            typeParams.add(param.getName());
        }

        return new ClassSignature(qualifiedName, typeParams);
    }

    public static ClassSignature from(UMLClass umlClass) {
        String qualifiedName = umlClass.getName();
        if (qualifiedName == null) qualifiedName = "";

        List<String> typeParams = new ArrayList<>();
        for (UMLTypeParameter param : umlClass.getTypeParameters()) {
            typeParams.add(param.getName());
        }

        return new ClassSignature(qualifiedName, typeParams);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassSignature that)) return false;
        return Objects.equals(qualifiedName, that.qualifiedName)
                && Objects.equals(typeParameters, that.typeParameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, typeParameters);
    }

    @Override
    public String toString() {
        if (typeParameters.isEmpty()) {
            return qualifiedName;
        }
        return qualifiedName + "<" + String.join(",", typeParameters) + ">";
    }

    public static void clearCache() {
        PSI_CACHE.invalidateAll();
    }
}
