package codeemoji.inlay.vcs;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.annotate.AnnotationProvider;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspectAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.vcs.CacheableAnnotationProvider;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

import java.util.Objects;

//copied from VcsCodeVisionProviderKt
public class CEVcsFileAnnotationProvider {

    private static final Key<FileAnnotation> VCS_CODE_AUTHOR_ANNOTATION = new Key<>("Vcs.CodeAuthor.Annotation");
    private static final Key<?> PREVIEW_INFO_KEY = Key.create("preview.author.info");


    private static boolean hasPreviewInfo(Editor editor) {
        return PREVIEW_INFO_KEY.get(editor) != null;
    }

    public static AnnotationResult getAspect(PsiFile file, Editor editor) {
        if (hasPreviewInfo(editor)) {
            return (new AnnotationResult.Success(LineAnnotationAspectAdapter.NULL_ASPECT));
        } else {
            VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile == null) {
                return AnnotationResult.NoAnnotation.INSTANCE;
            } else {
                Project project = file.getProject();
                AnnotationResult annotationResult = getAnnotation(project, virtualFile, editor);
                if (annotationResult == AnnotationResult.NoAnnotation.INSTANCE) {
                    return AnnotationResult.NoAnnotation.INSTANCE;
                } else if ( annotationResult == AnnotationResult.NotReady.INSTANCE) {
                    return AnnotationResult.NotReady.INSTANCE;
                } else {
                    if (!(annotationResult instanceof AnnotationResult.Success success)) {
                        throw new NoWhenBranchMatchedException();
                    }

                    if (success.getRes() instanceof FileAnnotation fileAnnotation) {
                        LineAnnotationAspect[] aspects = fileAnnotation.getAspects();
                        int j = 0;

                        for (int maxLen = aspects.length; j < maxLen; ++j) {
                            LineAnnotationAspect aspect = aspects[j];
                            if (Objects.equals(aspect.getId(), LineAnnotationAspect.AUTHOR)) {
                                return (new AnnotationResult.Success(aspect));
                            }
                        }
                    }
                    return (new AnnotationResult.Success(null));
                }
            }
        }
    }


    public static AnnotationResult getAnnotation(Project project, VirtualFile file, Editor editor) {
        FileAnnotation var3 = editor.getUserData(VCS_CODE_AUTHOR_ANNOTATION);
        if (var3 != null) {
            return (new AnnotationResult.Success(var3));
        } else {
            AbstractVcs abstractVcs = ProjectLevelVcsManager.getInstance(project).getVcsFor(file);
            if (abstractVcs == null) {
                return AnnotationResult.NoAnnotation.INSTANCE;
            } else {
                AnnotationProvider annotationProvider = abstractVcs.getAnnotationProvider();
                if (!(annotationProvider instanceof CacheableAnnotationProvider cacheableProvider)) {
                    return AnnotationResult.NoAnnotation.INSTANCE;
                } else {
                    FileStatus fileStatus = FileStatusManager.getInstance(project).getStatus(file);
                    if (!Intrinsics.areEqual(fileStatus, FileStatus.UNKNOWN) && !Intrinsics.areEqual(fileStatus, FileStatus.IGNORED)) {
                        if (Intrinsics.areEqual(fileStatus, FileStatus.ADDED)) {
                            return (new AnnotationResult.Success(null));
                        } else {
                            FileAnnotation fileAnnotation = cacheableProvider.getFromCache(file);
                            if (fileAnnotation == null) {
                                return AnnotationResult.NotReady.INSTANCE;
                            } else {
                               /*
                                Disposable annotationDisposable = CEVcsFileAnnotationProvider::getAnnotation$lambda$9;
                                fileAnnotation.setCloser(() -> {
                                    CEVcsFileAnnotationProvider.getAnnotation$lambda$10(editor, annotationDisposable, project, file);
                                });
                                fileAnnotation.setReloader(annotation -> {
                                    CEVcsFileAnnotationProvider.getAnnotation$lambda$11();
                                });
                                editor.putUserData(VCS_CODE_AUTHOR_ANNOTATION, fileAnnotation);
                                registerAnnotation(fileAnnotation);
                                ApplicationManager.getApplication().invokeLater(() -> {
                                    CEVcsFileAnnotationProvider.getAnnotation$lambda$12(editor, annotationDisposable);
                                });*/
                                return (new AnnotationResult.Success(fileAnnotation));


                               // return AnnotationResult.NotReady.INSTANCE;
                            }
                        }
                    } else {
                        return AnnotationResult.NoAnnotation.INSTANCE;
                    }
                }
            }
        }
    }


    private static void getAnnotation$lambda$9(FileAnnotation annotation) {
        unregisterAnnotation(annotation);
        annotation.dispose();
    }

    /*
    private static void getAnnotation$lambda$10(Editor editor, Disposable disposable, Project project, VirtualFile $file) {
        editor.putUserData(VCS_CODE_AUTHOR_ANNOTATION, null);
        Disposer.dispose(disposable);
        AnnotationsPreloader var7 = project.getService(AnnotationsPreloader.class);
        if (var7 == null) {
            throw new IllegalStateException(("Cannot find service " + AnnotationsPreloader.class.getName() + " in " + project + " (classloader=" + serviceClass$iv.getClassLoader()).toString());
        } else {
            var7.schedulePreloading($file);
        }
    }
*/

    private static void getAnnotation$lambda$11(Function1 $tmp0, Object p0) {
        $tmp0.invoke(p0);
    }

    private static void getAnnotation$lambda$12(Editor editor, Disposable disposable) {
        EditorUtil.disposeWithEditor(editor, disposable);
    }

    private static void unregisterAnnotation(FileAnnotation annotation) {
        ProjectLevelVcsManager.getInstance(annotation.getProject()).getAnnotationLocalChangesListener().unregisterAnnotation(annotation);
    }

    private static void registerAnnotation(FileAnnotation annotation) {
        ProjectLevelVcsManager.getInstance(annotation.getProject()).getAnnotationLocalChangesListener().registerAnnotation(annotation);
    }







    //--------



}
