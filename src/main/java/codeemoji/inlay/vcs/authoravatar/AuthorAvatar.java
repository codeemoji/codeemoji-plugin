package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AuthorAvatar extends CEProvider<AuthorAvatarSettings> {

    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @Nullable InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        return new RecentlyModifiedCollector(psiFile, editor, getKey());
    }

    @Override
    public @NotNull CEConfigurableWindow<AuthorAvatarSettings> createConfigurable() {
        return new AuthorAvatarConfigurable();
    }

    //screw anonymous classes. they are ugly
    private class RecentlyModifiedCollector extends VCSMethodCollector {
        protected RecentlyModifiedCollector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            String author = getMostCommonAuthor(element.getProject(), textRange, getEditor(), vcsBlame);

            if (author == null) return null;

            return makePresentation(author);
        }

        @Nullable
        private InlayVisuals makePresentation(String author) {
            CESymbol authorAvatar = getSettings().getSymbolForAuthor(author
                    .substring(0, author.indexOf(" ")).toLowerCase(Locale.ROOT));
            if (authorAvatar == null) return null;

            return InlayVisuals.of(authorAvatar, author);
        }

        @Nullable
        private String getMostCommonAuthor(
                Project project, TextRange range, Editor editor, FileAnnotation blame) {

            LineAnnotationAspect aspect = getAspect(LineAnnotationAspect.AUTHOR);

            if (aspect == null) return null;

            Document document = editor.getDocument();
            int startLine = document.getLineNumber(range.getStartOffset());
            int endLine = document.getLineNumber(range.getEndOffset());
            UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

            return IntStream.rangeClosed(startLine, endLine)
                    .mapToObj(provider::getLineNumber)
                    .map(aspect::getValue) // gets the author name for line
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())) // group by author and count occurrences
                    .entrySet()
                    .stream()
                    .max(Map.Entry.comparingByValue()) // find the entry with the highest count
                    .map(Map.Entry::getKey) // get the most common author's name
                    .orElse(null); // return null if no author found
        }


    }


}








