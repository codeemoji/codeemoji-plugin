package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEBundle;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CESymbolHolder;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AuthorAvatarConfigurable extends CEConfigurableWindow<AuthorAvatarSettings> {

    @Override
    public @NotNull JComponent createComponent(AuthorAvatarSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        localSymbols.clear();
        //make deep copy. we update later
        for (CESymbolHolder pair : settings.getSymbols()) {
            localSymbols.add(pair.makeCopy());
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add existing rows to the panel
        for (CESymbolHolder pair : localSymbols) {
            addRow(panel, pair, settings, changeListener);
        }

        // Add the "Add" button at the end
        JButton addButton = new JButton(CEBundle.getString("inlay.authoravatar.settings.add_avatar"));
        addButton.addActionListener(e -> {
            CESymbolHolder newPair = new CESymbolHolder(
                    CESymbol.of("\uD83D\uDC68\uFE0F"),
                    "inlay.authoravatar.settings.author", localSymbols.size() + 1);
            localSymbols.add(newPair); // Add to the local list
            addRow(panel, newPair, settings, changeListener); // Add the new row to the panel

            syncSettings(settings, changeListener);
        });

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(addButton);

        panel.add(addPanel); // Add the "Add" button panel

        return panel;
    }

    private void addRow(JPanel panel, CESymbolHolder holder, AuthorAvatarSettings settings, ChangeListener listener) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Create a text field for the author (editable)
        JTextField authorTextField = new JTextField(holder.getTranslatedName());
        authorTextField.setEditable(true);
        int height = 30;
        authorTextField.setPreferredSize(new Dimension(160, height));

        // Update the Pair when the author name is changed
        authorTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                holder.setId(authorTextField.getText()); // Update the Pair's author value

                syncSettings(settings, listener);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                holder.setId(authorTextField.getText()); // Update the Pair's author value

                syncSettings(settings, listener);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });


        // Create a non-editable text field to display the emoji
        JLabel emojiLabel = holder.getSymbol().createLabel("");
        emojiLabel.setFocusable(false);
        emojiLabel.setPreferredSize(new Dimension(height, height));
        emojiLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                createPickEmojiMenu(emojiLabel, holder, false, settings, listener);
            }
        });

        // Create the button to pick a new emoji
        JButton pickEmojiButton = new JButton(CEBundle.getString("codeemoji.configurable.edit"));
        pickEmojiButton.addActionListener(e -> createPickEmojiMenu(
                emojiLabel, holder, false, settings, listener));

        // Create the delete button to remove the current row
        JButton deleteButton = new JButton(CEBundle.getString("codeemoji.configurable.delete"));
        deleteButton.addActionListener(e -> {
            panel.remove(rowPanel); // Remove the row from the panel
            panel.revalidate(); // Revalidate the panel to update the layout
            panel.repaint(); // Repaint the panel to apply changes

            localSymbols.remove(holder); // Remove from the settings list

            syncSettings(settings, listener);
        });

        // Add the components to the row panel
        rowPanel.add(authorTextField);
        rowPanel.add(emojiLabel);
        rowPanel.add(pickEmojiButton);
        rowPanel.add(deleteButton);

        // Add the row panel to the main panel before the "Add" button panel
        panel.add(rowPanel, panel.getComponentCount() - 1);
        panel.revalidate(); // Revalidate the panel to include the new row
        panel.repaint(); // Repaint the panel
    }

}