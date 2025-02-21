package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSClassCollector;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.VcsCodeAuthorInfo;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.codeInsight.hints.settings.language.NewInlayProviderSettingsModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AuthorAvatar extends CEProviderMulti<AuthorAvatarSettings> {

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        return List.of(new MethodCollector(psiFile, editor, getKey()),
                new ClassCollector(psiFile, editor, getKey()));
    }

    @Override
    public @NotNull CEConfigurableWindow<AuthorAvatarSettings> createConfigurable() {
        return new AuthorAvatarConfigurable();
    }

    private class ClassCollector extends VCSClassCollector {

        protected ClassCollector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiClass element) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            String author = getMostCommonAuthor(element.getProject(), textRange, getEditor(), vcsBlame);

            if (author == null) return null;

            return makePresentation(author);
        }
    }

    //screw anonymous classes. they are ugly
    private class MethodCollector extends VCSMethodCollector {
        protected MethodCollector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
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
    }

    @Nullable
    private InlayVisuals makePresentation(String author) {
        String formattedAuthor = author;
        if (author.contains(" ")) {
            formattedAuthor = author.substring(0, author.indexOf(" "));
        }
        CESymbol authorAvatar = getSettings().getSymbolForAuthor(formattedAuthor
                .toLowerCase(Locale.ROOT));
        if (authorAvatar == null) return null;

        return InlayVisuals.of(authorAvatar, author);
    }

    @Nullable
    private String getMostCommonAuthor(
            Project project, TextRange range, Editor editor, FileAnnotation blame) {

        LineAnnotationAspect aspect = CEVcsUtils.getAspect(blame, LineAnnotationAspect.AUTHOR);

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








