package codeemoji.core.settings;

import codeemoji.core.config.CEPSIType;
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
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

// a configurable class that holds a symbol list
public class CEBaseConfigurableWindow<S extends CEBaseSettings<S>> {

    protected final List<CESymbolHolder> localSymbols = new ArrayList<>();

    public @NotNull JComponent createComponent(S settings, @Nullable String preview, Project project,
                                               Language language, ChangeListener changeListener) {
        localSymbols.clear();
        for (var s : settings.gatherAllSymbols()) {
            localSymbols.add(s.makeCopy());
        }

        FormBuilder builder = FormBuilder.createFormBuilder();
        buildForm(builder, settings, preview, project, language, changeListener);
        return builder.getPanel();
    }

    /**
     * Subclasses override this to add their specific fields.
     */
    protected void buildForm(FormBuilder builder, S settings, @Nullable String preview, Project project,
                             Language language, ChangeListener changeListener) {

        // Add symbol rows
        for (var holder : localSymbols) {
            builder.addComponent(createSymbolRow(holder, settings, changeListener));
        }

        // Add PSI Type picker if necessary
        if (settings.getAllowedPsiType() == CEPSIType.METHODS_AND_CLASSES) {
            builder.addLabeledComponent(
                    CEBundle.getString("codeemoji.configurable.target_type"),
                    createPickTarget(settings, changeListener));
        }

        if (settings.isCanHaveReferences()) {
            // Add "Include References" checkbox
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(settings.isIncludeReferences());
            checkBox.addActionListener(e -> {
                settings.setIncludeReferences(checkBox.isSelected());
                changeListener.settingsChanged();
            });
            builder.addLabeledComponent(
                    CEBundle.getString("codeemoji.configurable.include_references"), checkBox);
        }

        JCheckBox onlyInProjectCheckBox = new JCheckBox();
        onlyInProjectCheckBox.setSelected(!settings.isOnlyInProject());
        onlyInProjectCheckBox.addActionListener(e -> {
            settings.setOnlyInProject(!onlyInProjectCheckBox.isSelected());
            changeListener.settingsChanged();
        });
        builder.addLabeledComponent(
                CEBundle.getString("codeemoji.configurable.include_non_project_files"), onlyInProjectCheckBox);
    }

    private @NotNull JComponent createPickTarget(S settings, ChangeListener changeListener) {
        var filteredValues = Arrays.stream(CEPSIType.values())
                .filter(v -> v != CEPSIType.UNSPECIFIED)
                .toArray(CEPSIType[]::new);

        ComboBox<CEPSIType> comboBox = new ComboBox<>(filteredValues);
        comboBox.setSelectedItem(settings.getTargetType());

        comboBox.setRenderer(new ListCellRenderer<>() {
            private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

            @Override
            public Component getListCellRendererComponent(JList<? extends CEPSIType> list, CEPSIType value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                renderer.setText(CEBundle.getString("codeemoji.configurable.target_type."
                        + value.name().toLowerCase()));
                return renderer;
            }
        });

        comboBox.addActionListener(e -> {
            settings.setTargetType((CEPSIType) comboBox.getSelectedItem());
            changeListener.settingsChanged();
        });
        return comboBox;
    }

    private @NotNull JComponent createSymbolRow(CESymbolHolder holder, S settings, ChangeListener listener) {
        JLabel label = holder.getSymbol().createLabel(holder.getTranslatedName());

        JButton pickEmojiButton = new JButton(CEBundle.getString("codeemoji.configurable.edit"));
        pickEmojiButton.addActionListener(e -> createPickEmojiMenu(label, holder, true, settings, listener));

        JPanel labelButtonPanel = new JPanel(new BorderLayout(10, 0));
        labelButtonPanel.add(label, BorderLayout.CENTER);
        labelButtonPanel.add(pickEmojiButton, BorderLayout.EAST);
        labelButtonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));

        return labelButtonPanel;
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
            editorTextField.addSettingsProvider(CEBaseConfigurableWindow::addSettings);
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
