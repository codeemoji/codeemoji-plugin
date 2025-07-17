package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.collector.base.CEClassCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.vcs.CEVcsUtils;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.concurrency.AppExecutorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AuthorAvatar extends CEProvider<AuthorAvatarSettings> {

    @Override
    protected void createCollectors(Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addMethodCollector(e -> createInlay(e, editor));
        builder.addClassCollector(e -> createInlay(e, editor));
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<AuthorAvatarSettings> createConfigurable() {
        return new AuthorAvatarConfigurable();
    }


    private @Nullable InlayVisuals createInlay(@NotNull PsiElement element, @NotNull Editor editor) {
        FileAnnotation vcsBlame = CEVcsUtils.getAnnotation(element.getContainingFile(), editor);
        if (vcsBlame == null) {
            return null;
        }
        Document document = CEUtils.getContainingDocument(element);
        if (document == null) return null;

        //text range of this element without comments
        TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

        var author = getMostCommonAuthor(element.getProject(), textRange, document, vcsBlame);

        if (author == null) return null;

        return makePresentation(author.first, author.second);
    }


    @Nullable
    private InlayVisuals makePresentation(String author, int otherAuthors) {
        String formattedAuthor = author;
        if (author.contains(" ")) {
            formattedAuthor = author.substring(0, author.indexOf(" "));
        }
        CESymbol authorAvatar = getSettings().getSymbolForAuthor(formattedAuthor
                .toLowerCase(Locale.ROOT));
        if (authorAvatar == null) return null;
        if (otherAuthors > 0) {
            formattedAuthor += " +" + otherAuthors;
        }
        return InlayVisuals.direct(authorAvatar, formattedAuthor);
    }

    @Nullable
    private Pair<String, Integer> getMostCommonAuthor(
            Project project, TextRange range, Document document, FileAnnotation blame) {

        LineAnnotationAspect aspect = CEVcsUtils.getAspect(blame, LineAnnotationAspect.AUTHOR);

        if (aspect == null) return null;

        int startLine = document.getLineNumber(range.getStartOffset());
        int endLine = document.getLineNumber(range.getEndOffset());
        UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

        Map<String, Long> authorCounts = IntStream.rangeClosed(startLine, endLine)
                .mapToObj(provider::getLineNumber)
                .map(aspect::getValue) // gets the author name for line
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())); // group by author and count occurrences

        if (authorCounts.isEmpty()) return null;

        Map.Entry<String, Long> mostCommonEntry = authorCounts.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue()) // find the entry with the highest count
                .orElse(null);

        if (mostCommonEntry == null) return null;

        int otherAuthorsCount = authorCounts.size() - 1; // Count of other distinct authors

        return Pair.create(mostCommonEntry.getKey(), otherAuthorsCount);
    }

}








