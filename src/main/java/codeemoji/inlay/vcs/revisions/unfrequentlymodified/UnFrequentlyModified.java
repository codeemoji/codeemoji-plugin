package codeemoji.inlay.vcs.revisions.unfrequentlymodified;

import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.provider.CEProvider;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.CEVcsUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class UnFrequentlyModified extends CEProvider<UnFrequentlyModifiedSettings> {

    @Nullable
    private FileAnnotation vcsBlame = null;

    @Override
    public @NotNull CEBaseConfigurableWindow<UnFrequentlyModifiedSettings> createConfigurable() {
        return new UnFrequentlyModifiedConfigurable();
    }

    @Override
    protected void createCollectors(CEProvider<UnFrequentlyModifiedSettings>.Builder builder, @NotNull PsiFile psiFile, Editor editor) {
        vcsBlame = CEVcsUtils.getAnnotation(psiFile, editor);
       // builder.addMethodCollector(e -> createInlayFor(e, editor));
     //   builder.addClassCollector(e -> createInlayFor(e, editor));
    }

    protected @Nullable InlayVisuals createInlayFor(@NotNull PsiElement element, @NotNull Editor editor) {
        if (vcsBlame == null) return null;

        //text range of this element without comments
        TextRange textRange = CEVcsUtils.getTextRangeWithoutLeadingCommentsAndWhitespaces(element);

        Date date = CEVcsUtils.getLatestModificationDate(element.getProject(), textRange, editor, vcsBlame);

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








