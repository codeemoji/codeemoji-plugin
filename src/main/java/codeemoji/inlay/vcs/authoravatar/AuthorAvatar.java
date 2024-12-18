package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.collector.CEDynamicInlayBuilder;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.CEVcsUtils;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
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

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class AuthorAvatar extends CEProvider<AuthorAvatarSettings> {

    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull PsiFile file, @NotNull Editor editor) {
        return new RecentlyModifiedCollector(file, editor);
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull AuthorAvatarSettings settings) {
        return new AuthorAvatarConfigurable(settings);
    }

    //screw anonymous classes. they are ugly
    private class RecentlyModifiedCollector extends CEDynamicMethodCollector {
        @Nullable
        private final FileAnnotation vcsBlame;
        private final CEDynamicInlayBuilder presentationBuilder; //presentation builder. helper object basically
        private final Editor editor;

        protected RecentlyModifiedCollector(@NotNull PsiFile file, @NotNull Editor editor) {
            super(editor);
            this.vcsBlame = CEVcsUtils.getAnnotation(file, editor);
            this.presentationBuilder = new CEDynamicInlayBuilder(editor);
            this.editor = editor;
        }

        @Override
        public InlayPresentation needsHint(@NotNull PsiMethod element, @NotNull Map<?, ?> externalInfo) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            String author = getMostCommonAuthor(element.getProject(), textRange, editor, vcsBlame);

            if (author == null) return null;

            return makePresentation(author);
        }

        @Nullable
        private InlayPresentation makePresentation(String author) {
            CESymbol authorAvatar = getSettings().getSymbolForAuthor(author
                    .substring(0, author.indexOf(" ")).toLowerCase(Locale.ROOT));
            if (authorAvatar == null) return null;
            var factory = new PresentationFactory(this.editor);

            var pres = authorAvatar.createPresentation(factory);
            pres = factory.roundWithBackground(pres);
            pres = factory.withTooltip(author, pres);
            return pres;
        }

        @Nullable
        private static String getMostCommonAuthor(
                Project project, TextRange range, Editor editor, FileAnnotation blame) {

            LineAnnotationAspect aspect = Arrays.stream(blame.getAspects())
                    .filter(a -> Objects.equals(a.getId(), LineAnnotationAspect.AUTHOR))
                    .findFirst().orElse(null);

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








