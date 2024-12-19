package codeemoji.core.collector;

import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.InlayHintsUtils;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.presentation.DynamicInsetPresentation;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.MenuOnClickPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

@Getter
@ToString
@EqualsAndHashCode
@SuppressWarnings("UnstableApiUsage")
public abstract sealed class CEInlayBuilder permits CECollector, CEDynamicInlayBuilder {

    private final Editor editor;
    private final @NotNull PresentationFactory factory;
    private final SettingsKey<?> settingsKey;

    protected CEInlayBuilder(Editor editor, SettingsKey<?> settingsKey) {
        this.editor = editor;
        this.factory = new PresentationFactory(this.editor);
        this.settingsKey = settingsKey;
    }

    private static String getTooltip(@NotNull String key) {
        try {
            return CEBundle.getString(key);
        } catch (RuntimeException ex) {
            return key;
        }
    }

    public InlayPresentation buildInlayWithEmoji(@NotNull CESymbol symbol, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return formatInlay(symbol.createPresentation(factory), keyTooltip, suffixTooltip);
    }


    protected @NotNull InlayPresentation buildInlayWithText(@NotNull String fullText, @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        return formatInlay(getFactory().smallText(fullText), keyTooltip, suffixTooltip);
    }


    // is tooltip suffix needed?
    private @NotNull InlayPresentation formatInlay(@NotNull InlayPresentation inlay,
                                                   @NotNull String keyTooltip, @Nullable String suffixTooltip) {
        inlay = buildInsetValuesForInlay(inlay);
        inlay = factory.withCursorOnHover(inlay, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        inlay = addContextMenu(inlay, editor.getProject());
        var tooltip = getTooltip(keyTooltip);
        if (null != suffixTooltip) {
            tooltip += " " + suffixTooltip;
        }
        inlay = factory.withTooltip(tooltip, inlay);

        return inlay;
    }

    private InlayPresentation addContextMenu(InlayPresentation presentation, Project project) {
        return new MenuOnClickPresentation(presentation, project,
                () -> InlayHintsUtils.INSTANCE.getDefaultInlayHintsProviderPopupActions(
                        settingsKey,
                        CEBundle.getLazyString("inlay." + settingsKey.getId() + ".name")
                )
        );
    }

    //TODO: refactor internal api usage
    private @NotNull InlayPresentation buildInsetValuesForInlay(@NotNull InlayPresentation inlay) {
        /*var inset = new InsetValueProvider() {
            @Override
            public int getTop() {
                return (new InlayTextMetricsStorage(getEditor())).getFontMetrics(true).offsetFromTop();
            }
        };
        return new DynamicInsetPresentation(inlay, inset);*/
        return factory.roundWithBackground(inlay);
    }
}
