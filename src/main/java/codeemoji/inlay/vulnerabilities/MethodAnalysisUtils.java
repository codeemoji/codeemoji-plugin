package codeemoji.inlay.vulnerabilities;

import codeemoji.core.util.CEUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiMethodCallExpression;
import com.intellij.psi.util.PsiTreeUtil;

public class MethodAnalysisUtils {
    public static boolean checkMethodExternality(PsiMethod method, Project project) {
        return method.getContainingFile() instanceof PsiJavaFile javaFile &&
                method.getContainingClass() != null &&
                javaFile.getPackageStatement() != null &&
                !javaFile.getPackageName().startsWith("java") &&
                !CEUtils.getSourceRootsInProject(project).contains(
                        ProjectFileIndex.getInstance(method.getProject()).getSourceRootForFile(
                                method.getNavigationElement().getContainingFile().getVirtualFile()
                        )
                );
    }

    public static PsiMethod[] collectExternalFunctionalityInvokingMethods(PsiMethod method){
        return PsiTreeUtil.collectElementsOfType(method.getNavigationElement(), PsiMethodCallExpression.class)
                .stream()
                .distinct()
                .<PsiMethod>mapMulti((methodCallExpression, consumer) -> {
                    PsiMethod resolvedMethodCallExpression = methodCallExpression.resolveMethod();
                    if (resolvedMethodCallExpression != null && !method.isEquivalentTo(resolvedMethodCallExpression)) {
                        consumer.accept(resolvedMethodCallExpression);
                    }
                })
                .toArray(PsiMethod[]::new);
    }
}