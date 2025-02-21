package codeemoji.core.settings;

import codeemoji.core.ui.EmojiPickerPanel;
import codeemoji.core.ui.EmojiRepository;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CESymbolHolder;
import com.intellij.codeInsight.hints.settings.language.SingleLanguageInlayHintsSettingsPanelKt;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

// a configurable class that holds a symbol list
public class CEConfigurableWindow<S extends CEBaseSettings<S>> {

    protected final List<CESymbolHolder> localSymbols = new ArrayList<>();

    public @NotNull JComponent createComponent(S settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {

        localSymbols.clear();
        for (var s : settings.gatherAllSymbols()) {
            localSymbols.add(s.makeCopy());
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));


        //addPreviewText(panel, project, language, preview);
        for (var holder : localSymbols) {
            addSymbolRow(panel, holder, settings, changeListener);
        }

        return panel;
    }

    private void addSymbolRow(JPanel panel, CESymbolHolder holder, S settings, ChangeListener listener) {
        // Create label
        JLabel label = holder.getSymbol().createLabel(holder.getTranslatedName());

        // Create button
        JButton pickEmojiButton = new JButton(CEBundle.getString("codeemoji.configurable.edit"));
        pickEmojiButton.addActionListener(e -> createPickEmojiMenu(label, holder, true, settings, listener));

        // Create a sub-panel for label and button with horizontal layout
        JPanel labelButtonPanel = new JPanel(new BorderLayout(10, 0)); // Add some horizontal spacing
        labelButtonPanel.add(label, BorderLayout.CENTER); // Label on the left
        labelButtonPanel.add(pickEmojiButton, BorderLayout.EAST); // Button on the right
        labelButtonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));

        // Add the sub-panel to the main panel
        panel.add(labelButtonPanel);
    }

    protected void createPickEmojiMenu(JLabel label, CESymbolHolder holder,
                                       boolean hasName, S settings, ChangeListener listener) {
        AtomicReference<JDialog> thisDialog = new AtomicReference<>();
        EmojiPickerPanel emojiPickerPanel = new EmojiPickerPanel(
                EmojiRepository.getLocalEmojis(true),
                new JLabel().getFont(), 50,
                em -> {
                    if (em != null) {
                        CESymbol emoji = CESymbol.of(em.symbol());
                        holder.setSymbol(emoji);
                        emoji.applyToLabel(hasName ? holder.getTranslatedName() : "", label);

                        syncSettings(settings, listener);
                    }

                    // Close the dialog by disposing of it directly
                    SwingUtilities.invokeLater(() -> {
                        // Dispose the dialog that contains the emoji picker
                        JDialog dialog = thisDialog.get();
                        if (dialog != null) {
                            dialog.dispose();  // Dispose of the specific dialog
                        }
                    });
                });

        emojiPickerPanel.setPreferredSize(new Dimension(600, 400));

        // Create a custom dialog where the panel is placed
        JOptionPane optionPane = new JOptionPane(
                emojiPickerPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{},  // no buttons
                null
        );

        // Display the dialog and block until closed
        JDialog dialog = optionPane.createDialog(CEBundle.getString("codeemoji.configurable.select_emoji"));
        thisDialog.set(dialog);

        dialog.setVisible(true);
    }

    protected void syncSettings(S settings, ChangeListener listener) {
        var copy = new ArrayList<CESymbolHolder>();
        for (CESymbolHolder pair : localSymbols) {
            copy.add(pair.makeCopy());
        }
        settings.setAllSymbols(copy);
        listener.settingsChanged();
    }

    // use inlayProviders resources files instead
    @Deprecated(forRemoval = true)
    protected void addPreviewText(JPanel panel, Project project, Language language, String previewText) {
        if (previewText != null) {
            EditorTextField editorTextField = SingleLanguageInlayHintsSettingsPanelKt.createEditor(language, project, ((editor) -> {
                //InlaySettingsPanel.this.currentEditor = editor;
                //   InlaySettingsPanel.PREVIEW_KEY.set((UserDataHolder) editor, treeNode);
                //  InlaySettingsPanelKt.getCASE_KEY().set((UserDataHolder) editor, var3);
                //  InlaySettingsPanel.this.updateHints(editor, model, var3);

                //   LanguageFileType fileType = language.getAssociatedFileType();
                // ReadAction.nonBlocking(() -> updateHints(project, null, null, fileType, editor, null))
                //       .finishOnUiThread(ModalityState.stateForComponent(panel), Runnable::run)
                //   .expireWhen(editor::isDisposed)
                //     .inSmartMode(project)
                // .submit(AppExecutorUtil.getAppExecutorService());

                return null;
            }));
            editorTextField.setText(previewText);
            editorTextField.addSettingsProvider(CEConfigurableWindow::addSettings);
            panel.add(ScrollPaneFactory.createScrollPane(editorTextField), "growx");
        }
    }


    private static void addSettings(EditorEx it) {
        it.setBorder(JBUI.Borders.empty(10));
        it.setBackgroundColor(EditorColorsManager.getInstance().getGlobalScheme().getDefaultBackground());
        EditorSettings editorSettings = it.getSettings();
        editorSettings.setLineNumbersShown(false);
        editorSettings.setCaretRowShown(false);
        editorSettings.setRightMarginShown(false);
    }


    // no impl. just leaving here as it might be needed in the future
    public interface ChangeListener {
        void settingsChanged();

    }

}
