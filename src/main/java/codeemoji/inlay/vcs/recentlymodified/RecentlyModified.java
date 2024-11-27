package codeemoji.inlay.vcs.recentlymodified;

import codeemoji.core.collector.CEDynamicInlayBuilder;
import codeemoji.core.collector.simple.CEDynamicMethodCollector;
import codeemoji.core.provider.CEProvider;
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
import com.intellij.openapi.vcs.impl.UpToDateLineNumberProviderImpl;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@SuppressWarnings("UnstableApiUsage")
public class RecentlyModified extends CEProvider<RecentlyModifiedSettings> {
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public @NotNull InlayHintsCollector buildCollector(@NotNull PsiFile file, @NotNull Editor editor) {
        return new RecentlyModifiedCollector(file, editor);
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull RecentlyModifiedSettings settings) {
        return new RecentlyModifiedConfigurable(settings);
    }

    //screw anonymous classes. they are ugly
    private class RecentlyModifiedCollector extends CEDynamicMethodCollector {
        @Nullable
        private final FileAnnotation vcsBlame;
        private final CEDynamicInlayBuilder presentationBuilder;
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

            Date date = getEarliestModificationDate(element.getProject(), textRange, editor, vcsBlame);


            if (date == null) return null;

            //check if date is within a week from now

            long diff = System.currentTimeMillis() - date.getTime();
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays <= getSettings().getDays()) {
                return makePresentation(date);
            }
            return null;
        }

        private InlayPresentation makePresentation(Date d) {
            var factory = new PresentationFactory(this.editor);

            return getSettings().getFirstSymbol().createPresentation(factory, false);

            // return factory.smallText(d.toString());
        }


        @Nullable
        private static Date getEarliestModificationDate(
                Project project, TextRange range, Editor editor, FileAnnotation blame) {

            Document document = editor.getDocument();
            int startLine = document.getLineNumber(range.getStartOffset());
            int endLine = document.getLineNumber(range.getEndOffset());
            UpToDateLineNumberProviderImpl provider = new UpToDateLineNumberProviderImpl(document, project);

            return IntStream.rangeClosed(startLine, endLine)
                    .mapToObj(provider::getLineNumber)
                    .map(blame::getLineDate)  //gets the author name for line
                    .filter(Objects::nonNull)
                    .min(Date::compareTo)
                    .orElse(null);
        }

    }
}








