package codeemoji.inlay.external;

import codeemoji.core.external.CEExternalService;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Service
@Getter
public final class MyExternalService implements CEExternalService<VirtualFile, Object> {

    Map<VirtualFile, Object> persistedData = new HashMap<>();
    // all the libraries used in the project
    List<String> libraries = new ArrayList<>();

    public void preProcess(@NotNull Project project) {
        // Preprocess and persist information
        persistedData.put(project.getWorkspaceFile(), null);
        getLibraries(project);

    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void buildInfo(@NotNull Map infoResult, @Nullable PsiElement element) {
        try {
            if (element != null) {
                // Retrieves preprocessed persistent values
                var data = retrieveData(element.getProject().getWorkspaceFile());
                // Put informations about element
                infoResult.put("externalParam", null);
            }
        } catch (RuntimeException ignored) {
        }
    }

    public void getLibrariesFromFiles(List<PsiFile> psiFiles) {
        for (PsiFile psiFile : psiFiles) {
            PsiImportList importList = ((PsiJavaFile) psiFile).getImportList();
            if (importList != null) {
                PsiImportStatement[] importStatements = importList.getImportStatements();
                for (PsiImportStatement importStatement : importStatements) {
                    String libraryName = importStatement.getQualifiedName();
                    if (libraryName != null) {
                        libraries.add(libraryName);
                    }
                }
            }
        }
    }

    public void getLibraries(Project project) {
        List<PsiFile> javaFiles = new ArrayList<>();

        PsiManager psiManager = PsiManager.getInstance(project);
        VirtualFile baseDir = project.getBaseDir();

        if (baseDir != null) {
            VirtualFile srcDir = baseDir.findChild("src");
            if (srcDir != null) {
                findJavaFilesInDirectory(srcDir, javaFiles, psiManager);
            }
        }
        getLibrariesFromFiles(javaFiles);
    }

    private void findJavaFilesInDirectory(VirtualFile directory, List<PsiFile> resultList, PsiManager psiManager) {
        for (VirtualFile child : directory.getChildren()) {
            if (child.isDirectory()) {
                findJavaFilesInDirectory(child, resultList, psiManager);
            } else if (child.getName().endsWith(".java")) {
                PsiFile psiFile = psiManager.findFile(child);
                if (psiFile != null) {
                    resultList.add(psiFile);
                }
            }
        }
    }

}