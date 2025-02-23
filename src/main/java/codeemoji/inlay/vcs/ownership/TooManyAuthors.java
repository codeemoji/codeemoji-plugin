package codeemoji.inlay.vcs.ownership;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProviderMulti;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSClassCollector;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TooManyAuthors extends CEProviderMulti<TooManyAuthorsSettings> {

    @Override
    protected List<SharedBypassCollector> createCollectors(@NotNull PsiFile psiFile, Editor editor) {
        return List.of(new Collector(psiFile, editor, getKey()));
    }

    @Override
    public @NotNull CEConfigurableWindow<TooManyAuthorsSettings> createConfigurable() {
        return new TooManyAuthorsConfigurable();
    }

    //screw anonymous classes. they are ugly
    private class Collector extends VCSClassCollector {
        protected Collector(@NotNull PsiFile file, @NotNull Editor editor, String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiClass element) {
            if (vcsBlame == null) return null;

            //text range of this element without comments
            TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

            List<String> author = getAuthors(element.getProject(), textRange, getEditor(), vcsBlame);

            if (author.size() < getSettings().getMinimumAuthors()) return null;

            return makePresentation(author);
        }

        private @NotNull InlayVisuals makePresentation(List<String> author) {
            StringBuilder authors = new StringBuilder();
            int max = 4;
            for (int i = 0; i < author.size() && i < max; i++) {
                authors.append(author.get(i));
                if (i < author.size() - 1) {
                    authors.append(", ");
                }
            }
            return InlayVisuals.of(getSettings().getMainSymbol(),
                    CEBundle.getString("inlay.toomanyauthors.tooltip", authors.toString()));
        }


        private List<String> getAuthors(Project project, TextRange range, Editor editor, FileAnnotation blame) {

            LineAnnotationAspect aspect = CEVcsUtils.getAspect(blame, LineAnnotationAspect.AUTHOR);

            if (aspect == null) return List.of();

            Document document = editor.getDocument();
            int startLine = document.getLineNumber(range.getStartOffset());
            int endLine = document.getLineNumber(range.getEndOffset());
            UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

            return IntStream.rangeClosed(startLine, endLine)
                    .mapToObj(provider::getLineNumber)
                    .map(aspect::getValue) // gets the author name for line
                    .filter(a -> a != null && !a.isEmpty())
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting())) // group by author and count occurrences
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .toList();
        }


    }


}








