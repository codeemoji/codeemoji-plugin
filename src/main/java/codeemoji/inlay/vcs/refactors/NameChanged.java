package codeemoji.inlay.vcs.refactors;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.actions.VcsFacade;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.history.*;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.vcsUtil.VcsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class NameChanged extends CEProvider<NameChangedSettings> {

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new NameChangedCollector(psiFile, editor, getKey());
    }

    @Override
    public @NotNull CEConfigurableWindow<NameChangedSettings> createConfigurable() {
        return new CEConfigurableWindow<>();
    }

    private class NameChangedCollector extends VCSMethodCollector {

        protected NameChangedCollector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (vcsBlame == null) return null;
            String currentMethodName = element.getName();
            if(true)return null;
            if (currentMethodName.equals("testMethod")) {
                int aa0 = 1;
            }
            //text range of this element without comments
            VcsRevisionNumber lastRevision = getLastRevision(element.getProject(), element, getEditor(), vcsBlame);

            //get current revision (latest version in which this file was modified)
            VcsRevisionNumber currentRevision = vcsBlame.getCurrentRevision();
            if (lastRevision != null) {
                String previousMethodName = getMethodNameFromPreviousRevision(element, lastRevision);
                //get current method name

                if (previousMethodName != null && !previousMethodName.equals(currentMethodName)) {
                    return InlayVisuals.translated(getSettings().getMainSymbol(),
                            "inlay.namechanged.tooltip", previousMethodName);
                }
            }
            return null;
        }

        @Nullable
        private String getMethodNameFromPreviousRevision(@NotNull PsiMethod method, @NotNull VcsRevisionNumber lastRevision) {
            VirtualFile file = method.getContainingFile().getVirtualFile();
            if (file == null) return null;

            AbstractVcs vcs = ProjectLevelVcsManager.getInstance(method.getProject()).getVcsFor(file);
            if (vcs == null) return null;

            VcsHistoryProvider historyProvider = vcs.getVcsHistoryProvider();
            if (historyProvider == null) return null;

            // Convert VirtualFile to FilePath for the VCS API
            FilePath filePath = VcsUtil.getFilePath(file);
            List<VcsFileRevision> history = new ArrayList<>();

            try {
                historyProvider.reportAppendableHistory(filePath, new VcsAppendableHistorySessionPartner() {
                    @Override
                    public void reportCreatedEmptySession(VcsAbstractHistorySession session) {
                    }

                    @Override
                    public void acceptRevision(VcsFileRevision revision) {
                        // Collect revisions older than the given lastRevision
                        if (revision.getRevisionNumber().compareTo(lastRevision) <= 0 && history.isEmpty()) {
                            history.add(revision);
                        }
                    }

                    @Override
                    public void reportException(VcsException e) {
                        // Log exception if needed
                    }
                });
            } catch (VcsException e) {
                // Handle exception from reportAppendableHistory
                return null;
            }

            if (history.isEmpty()) return null;

            // Get the most recent revision before lastRevision (assuming history is in reverse chronological order)
            VcsFileRevision previousRevision = history.get(0);

            try {
                //TODO: add revision cache
                byte[] content = previousRevision.loadContent();
                if (content == null) return null; // Handle nullable content
                String previousContent = new String(content, StandardCharsets.UTF_8);
                return extractMethodName(previousContent, method);
            } catch (IOException | VcsException e) { // Handle both exceptions
                // Log exception properly
                e.printStackTrace();
                return null;
            }
        }

        private String extractMethodName(String fileContent, PsiMethod method) {
            Pattern pattern = Pattern.compile("\\b(\\w+)\\s+" + method.getName() + "\\s*\\(");
            Matcher matcher = pattern.matcher(fileContent);
            return matcher.find() ? matcher.group(1) : null;
        }


        //null if it's not from last revision
        @Nullable
        private VcsRevisionNumber getLastRevision(
                Project project, PsiMethod method, Editor editor, FileAnnotation blame) {

            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(method);

            VcsRevisionNumber lastRevision = blame.getCurrentRevision();
            if (lastRevision == null) return null;
            Document document = editor.getDocument();
            int startLine = document.getLineNumber(textRange.getStartOffset());
            int endLine =startLine+1;
            UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

            PrimitiveIterator.OfInt iterator = IntStream.rangeClosed(startLine, endLine)
                    .map(provider::getLineNumber).iterator();
            while (iterator.hasNext()) {
                int line = iterator.nextInt();
                VcsRevisionNumber revision = blame.getLineRevisionNumber(line);
                if (lastRevision.equals(revision)) {
                    return revision;
                }
            }
            return null;
        }
    }

/*
    private String getMethodNameInLastRevision(
            Project project,
            PsiMethod currentMethod,
            VcsRevisionNumber lastRevision
    ) {
        // Get the current file and its virtual file
        PsiFile currentFile = currentMethod.getContainingFile();
        VirtualFile vFile = currentFile.getVirtualFile();
        if (vFile == null) return null;

        // Fetch content from the last revision
        ContentRevision revision = VcsFacade.getInstance().getContentRevision(vFile, lastRevision, project);
        if (revision == null) return null;

        String oldContent = revision.getContent();
        if (oldContent == null) return null;

        // Proceed to parse the old content into a PSI file...

        // Create a temporary PSI file from the old content
        PsiFile oldFile = PsiFileFactory.getInstance(project)
                .createFileFromText(vFile.getName(), currentFile.getFileType(), oldContent);

        // Find the equivalent method in the old PSI file...

        // Get the line number of the method's start in the last revision
        Document currentDoc = PsiDocumentManager.getInstance(project).getDocument(currentFile);
        if (currentDoc == null) return null;

        TextRange range = currentMethod.getTextRange();
        int currentStartLine = currentDoc.getLineNumber(range.getStartOffset());

        // Use the line number provider to map to the last revision's line
        UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(currentDoc, project);
        int lastRevisionLine = provider.getLineNumber(currentStartLine);
        if (lastRevisionLine == -1) return null; // Method didn't exist in the last revision

        // Find the method at this line in the old PSI file
        Document oldDoc = PsiDocumentManager.getInstance(project).getDocument(oldFile);
        if (oldDoc == null) return null;

        int oldStartOffset = oldDoc.getLineStartOffset(lastRevisionLine);
        PsiElement elementAtOffset = oldFile.findElementAt(oldStartOffset);
        if (elementAtOffset == null) return null;

        // Navigate up to the enclosing method
        PsiMethod oldMethod = PsiTreeUtil.getParentOfType(elementAtOffset, PsiMethod.class);
        if (oldMethod == null) return null;

        // Extract the method name
        return oldMethod.getName();
    }*/
}








