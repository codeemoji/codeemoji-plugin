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

    private MethodSignature(String className, String methodName, List<String> parameterTypes) {
        this.className = className != null ? className : "";
        this.methodName = methodName;
        this.parameterTypes = Collections.unmodifiableList(parameterTypes);
    }

    // UML version - takes type names as-is
    public static MethodSignature from(UMLOperation operation) {
        String className = operation.getClassName() != null ? operation.getClassName() : "";
        String methodName = operation.getName();

        List<UMLParameter> parameters = new ArrayList<>(operation.getParameters());
        UMLParameter returnParameter = operation.getReturnParameter();
        if (returnParameter != null) {
            parameters.remove(returnParameter);
        }

        List<String> paramTypes = new ArrayList<>(parameters.size());
        for (UMLParameter p : parameters) {
            paramTypes.add(p.getType().toString()); // Just take the string as-is
        }

        return new MethodSignature(className, methodName, paramTypes);
    }

    // PSI version - takes type names as-is
    public static MethodSignature from(PsiMethod method) {
        String className = method.getContainingClass() != null ?
                method.getContainingClass().getQualifiedName() : "";  // Force qualified

        String methodName = method.getName();

        List<String> paramTypes = new ArrayList<>();
        for (PsiParameter param : method.getParameterList().getParameters()) {
            // Get simple type name (e.g., "String" instead of "java.lang.String")
            paramTypes.add(param.getType().getPresentableText());
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


}
