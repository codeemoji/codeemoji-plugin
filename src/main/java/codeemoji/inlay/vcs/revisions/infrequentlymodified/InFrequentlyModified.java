package codeemoji.inlay.vcs.revisions.infrequentlymodified;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CEUtils;
import codeemoji.inlay.vcs.CEVcsUtils;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class InFrequentlyModified extends CEProvider<InFrequentlyModifiedSettings> {

    @Override
    public @NotNull CEBaseConfigurableWindow<InFrequentlyModifiedSettings> createConfigurable() {
        return new InFrequentlyModifiedConfigurable();
    }

    @Override
    protected void createCollectors(CEProvider<InFrequentlyModifiedSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        builder.addMethodCollector(e -> createInlayFor(e, editor));
        builder.addClassCollector(e -> createInlayFor(e, editor));
    }

    protected @Nullable InlayVisuals createInlayFor(@NotNull PsiElement element, @NotNull Editor editor) {
        FileAnnotation vcsBlame = CEVcsUtils.getAnnotation(element.getContainingFile(), editor);
        if (vcsBlame == null) return null;

        Document document = CEUtils.getContainingDocument(element);
        if (document == null) return null;

        //text range of this element without comments
        TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

        Date date = CEVcsUtils.getLatestModificationDate(element.getProject(), textRange, document, vcsBlame);

        return makeInlay(date);
    }

    private @Nullable InlayVisuals makeInlay(Date date) {
        if (date == null) return null;

        long diff = System.currentTimeMillis() - date.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays >= getSettings().getDays()) {
            var settings = getSettings();
            String tooltip = settings.isShowDate() ? date.toString() : CEVcsUtils.getDaysAgoTooltipString(date);
            CESymbol mainSymbol = settings.getMainSymbol();
            return InlayVisuals.direct(mainSymbol, tooltip);
        }
        return null;
    }
}








