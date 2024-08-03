package codeemoji.inlay.vulnerabilities;

import codeemoji.core.util.CEUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;

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
}