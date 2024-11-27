package codeemoji.inlay.vcs;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.annotate.AnnotationProvider;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.vcs.CacheableAnnotationProvider;
import org.jetbrains.annotations.Nullable;

// static class. clean up later.
public final class CEVcsUtils {

    // cache key. Same that VcsCodeAuthorInlay uses
    private static final Key<FileAnnotation> VCS_CODE_AUTHOR_ANNOTATION = new Key<>("Vcs.CodeAuthor.Annotation");


    // helper
    public static FileAnnotation getAnnotation( PsiFile file, Editor editor){
        VirtualFile virtualFile = file.getVirtualFile();
        return getAnnotation(file.getProject(), virtualFile, editor);
    }

    //copied from VcsCodeAuthorInlayHintsCollector
    //gets the git annotation of the current file
    //basically gets the git blame for each line
    @Nullable
    public static FileAnnotation getAnnotation(Project project, VirtualFile file, Editor editor) {
        // uhm get cached one maybe
        FileAnnotation annotation = editor.getUserData(VCS_CODE_AUTHOR_ANNOTATION);
        if (annotation != null) {
            return annotation;
        }

        // gets version control for this project file. Similar to GitInstance thing i guess?
        AbstractVcs vcs = ProjectLevelVcsManager.getInstance(project).getVcsFor(file);
        if (vcs == null) {
            return null;
        }

        // could it be this is the GitAnnotationProvider from before?
        AnnotationProvider provider = vcs.getAnnotationProvider();
        if (provider instanceof CacheableAnnotationProvider cacheable) {
            // this probably calls .annotate internally
            // .annotate is where the magic happens
            annotation = cacheable.getFromCache(file);

            //whatever this does...

            /*
            Disposable annotationDisposable = new Disposable() {
                @Override
                public void dispose() {
                    unregisterAnnotation(annotation);
                    annotation.dispose();
                }
            };

            annotation.setCloser(() -> {
                editor.putUserData(VCS_CODE_AUTHOR_ANNOTATION, null);
                Disposer.dispose(annotationDisposable);

                project.getService(AnnotationsPreloader.class).schedulePreloading(file);
            });

            annotation.setReloader(annotation::close);

            editor.putUserData(VCS_CODE_AUTHOR_ANNOTATION, annotation);
            registerAnnotation(annotation);
            disposeWithEditor(editor, annotationDisposable);
            */

            return annotation;
        }

        return null;
    }

    public static TextRange getTextRangeWithoutLeadingCommentsAndWhitespaces(PsiElement element) {
        PsiElement startElement = SyntaxTraverser.psiApi().children(element)
                .find(child -> !(child instanceof PsiComment || child instanceof PsiWhiteSpace));
        if (startElement == null) startElement = element;
        return TextRange.create(startElement.getTextRange().getStartOffset(), element.getTextRange().getEndOffset());
    }



    //there's also a Vcsutil calss
}
