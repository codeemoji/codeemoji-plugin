package codeemoji.inlay.vcs.revisions.frequentlymodified;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.inlay.vcs.CEVcsUtils;
import codeemoji.inlay.vcs.VCSClassCollector;
import codeemoji.inlay.vcs.VCSMethodCollector;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FrequentlyModified extends CEProvider<FrequentlyModifiedSettings> {

    @Override
    protected void createCollectors(CEProvider<FrequentlyModifiedSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        String key = getKey();
        builder.addIf(getSettings().appliesToMethods(), new FrequentlyModifiedMethodCollector(psiFile, editor, key));
        builder.addIf(getSettings().appliesToClasses(), new FrequentlyModifiedClassCollector(psiFile, editor, key));
    }

    @Override
    public @NotNull CEBaseConfigurableWindow<FrequentlyModifiedSettings> createConfigurable() {
        return new FrequentlyModifiedConfigurable();
    }

    private class FrequentlyModifiedMethodCollector extends VCSMethodCollector {

        protected FrequentlyModifiedMethodCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiMethod element) {
            if (vcsBlame == null) return null;
            return maybeCreatePresentation(element, getEditor(), vcsBlame);
        }
    }

    private class FrequentlyModifiedClassCollector extends VCSClassCollector {

        protected FrequentlyModifiedClassCollector(@NotNull PsiFile file, @NotNull Editor editor, @NotNull String key) {
            super(file, editor, key);
        }

        @Override
        protected @Nullable InlayVisuals createInlayFor(@NotNull PsiClass element) {
            if (vcsBlame == null) return null;

            return maybeCreatePresentation(element, getEditor(), vcsBlame);
        }
    }

    private @Nullable InlayVisuals maybeCreatePresentation(@NotNull PsiElement element, Editor editor, FileAnnotation vcsBlame) {
        // text range of this element without comments
        TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

        Set<Date> date = getAllModificationDates(element.getProject(), textRange, editor, vcsBlame);

        int timeFrame = getSettings().getDaysTimeFrame();
        int modifications = getSettings().getModifications();
        Date timeFrameAgo = new Date(System.currentTimeMillis() - timeFrame * 24L * 60 * 60 * 1000);
        int modificationsInTimeFrame = 0;
        // check if it has had more modifications in last x days
        for (Date modificationDate : date) {
            if (modificationDate.after(timeFrameAgo)) {
                modificationsInTimeFrame++;
            }
        }

        if (modificationsInTimeFrame >= modifications) {
            return makePresentation(modificationsInTimeFrame, timeFrame);
        }
        return null;
    }

    private InlayVisuals makePresentation(int modifications, int timeFrame) {
        FrequentlyModifiedSettings settings = getSettings();
        return InlayVisuals.translated(settings.getMainSymbol(),
                "inlay.frequentlymodified.tooltip", modifications, timeFrame);
    }

    private static Set<Date> getAllModificationDates(
            Project project, TextRange range, Editor editor, FileAnnotation blame) {

        Document document = editor.getDocument();
        int startLine = document.getLineNumber(range.getStartOffset());
        int endLine = document.getLineNumber(range.getEndOffset());
        UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

        return IntStream.rangeClosed(startLine, endLine)
                .mapToObj(provider::getLineNumber)
                .map(blame::getLineDate)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}








