package codeemoji.inlay.vcs;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.TypeConversionUtil;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.UMLParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class MethodSignature {
    private final String className;
    private final String methodName;
    private final List<String> parameterTypes;

    private static final Cache<PsiMethod, MethodSignature> PSI_CACHE = CacheBuilder.newBuilder()
            .maximumSize(10_000)
            .weakKeys()
            .build();

    private MethodSignature(String className, String methodName, List<String> parameterTypes) {
        this.className = className;
        this.methodName = methodName;
        this.parameterTypes = Collections.unmodifiableList(parameterTypes);
    }

    public static MethodSignature from(UMLOperation operation) {
        String className = operation.getClassName();
        if (className == null) className = "";
        String methodName = operation.getName();
        List<UMLParameter> parameters = operation.getParameters();
        var returnParameter = operation.getReturnParameter();
        if (returnParameter != null) {
            parameters.remove(returnParameter);
        }
        List<String> paramTypes = new ArrayList<>(parameters.size());
        for (UMLParameter p : parameters) {
            paramTypes.add(p.getType().toQualifiedString());
        }

        return new MethodSignature(className, methodName, paramTypes);
    }

    public static MethodSignature from(PsiMethod method) {
        String className = method.getContainingClass() != null
                ? method.getContainingClass().getQualifiedName()
                : "";
        String methodName = method.getName();
        PsiParameter[] parameters = method.getParameterList().getParameters();

        List<String> paramTypes = new ArrayList<>(parameters.length);
        for (PsiParameter param : parameters) {
            PsiType type = param.getType();
            // Use erased type to match UMLOperation's representation
            PsiType erasedType = TypeConversionUtil.erasure(type);
            paramTypes.add(erasedType.getCanonicalText());
        }

        return new MethodSignature(className, methodName, paramTypes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodSignature that)) return false;
        return Objects.equals(className, that.className)
                && Objects.equals(methodName, that.methodName)
                && Objects.equals(parameterTypes, that.parameterTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, methodName, parameterTypes);
    }

    @Override
    public String toString() {
        return className + "#" + methodName + "(" + String.join(",", parameterTypes) + ")";
    }

    public static void clearCache() {
        PSI_CACHE.invalidateAll();
    }
}
