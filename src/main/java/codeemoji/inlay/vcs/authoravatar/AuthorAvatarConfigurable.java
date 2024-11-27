package codeemoji.inlay.vcs.authoravatar;

import codeemoji.core.util.CESymbol;
import codeemoji.inlay.vcs.test.CESymbolHolder;
import codeemoji.inlay.vcs.ui.EmojiPickerPanel;
import codeemoji.inlay.vcs.ui.EmojiRepository;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AuthorAvatarConfigurable implements ImmediateConfigurable {
    private final AuthorAvatarSettings settings;
    // local list to hold the author to avatar mappings
    private final List<CESymbolHolder> localAuthorsToAvatars = new ArrayList<>();

    public AuthorAvatarConfigurable(AuthorAvatarSettings settings) {
        this.settings = settings;
    }

    public @NotNull JComponent createComponent(ChangeListener listener) {
        localAuthorsToAvatars.clear();
        //make deep copy. we update later
        for (CESymbolHolder pair : settings.getAvatars()) {
            localAuthorsToAvatars.add(pair.makeCopy());
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add existing rows to the panel
        for (CESymbolHolder pair : localAuthorsToAvatars) {
            addRow(panel, pair, listener);
        }

        // Add the "Add" button at the end
        JButton addButton = new JButton("Add Avatar");
        addButton.addActionListener(e -> {
            CESymbolHolder newPair = new CESymbolHolder(
                    "Author " + (settings.getAvatars().size() + 1),
                    CESymbol.of("\uD83D\uDC68\uFE0F")
            );
            localAuthorsToAvatars.add(newPair); // Add to the local list
            addRow(panel, newPair, listener); // Add the new row to the panel

            syncSettings(listener);
        });

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(addButton);

        panel.add(addPanel); // Add the "Add" button panel

        return panel;
    }

    private void syncSettings(ChangeListener listener) {
        var copy = new ArrayList<CESymbolHolder>();
        for (CESymbolHolder pair : localAuthorsToAvatars) {
            copy.add(pair.makeCopy());
        }
        settings.setAvatars(copy);
        listener.settingsChanged();
    }

    private void addRow(JPanel panel, CESymbolHolder holder, ChangeListener listener) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Create a text field for the author (editable)
        JTextField authorTextField = new JTextField(holder.getName());
        authorTextField.setEditable(true);
        authorTextField.setPreferredSize(new Dimension(100, 30));

        // Update the Pair when the author name is changed
        authorTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                holder.setName(authorTextField.getText()); // Update the Pair's author value

                syncSettings(listener);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                holder.setName(authorTextField.getText()); // Update the Pair's author value

                syncSettings(listener);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });


        // Create a non-editable text field to display the emoji
        JLabel emojiTextField = holder.getSymbol().createLabel("");
        emojiTextField.setFocusable(false);
        emojiTextField.setPreferredSize(new Dimension(50, 30));
        emojiTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                createPickEmojiMenu(holder, rowPanel.getFont(), emojiTextField, listener);
            }
        });

        // Create the button to pick a new emoji
        JButton pickEmojiButton = new JButton("Edit");
        pickEmojiButton.addActionListener(e -> createPickEmojiMenu(
                holder, rowPanel.getFont(), emojiTextField, listener));

        // Create the delete button to remove the current row
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            panel.remove(rowPanel); // Remove the row from the panel
            panel.revalidate(); // Revalidate the panel to update the layout
            panel.repaint(); // Repaint the panel to apply changes

            localAuthorsToAvatars.remove(holder); // Remove from the settings list

            syncSettings(listener);
        });

        // Add the components to the row panel
        rowPanel.add(authorTextField);
        rowPanel.add(emojiTextField);
        rowPanel.add(pickEmojiButton);
        rowPanel.add(deleteButton);

        // Add the row panel to the main panel before the "Add" button panel
        panel.add(rowPanel, panel.getComponentCount() - 1);
        panel.revalidate(); // Revalidate the panel to include the new row
        panel.repaint(); // Repaint the panel
    }

    private void createPickEmojiMenu(CESymbolHolder holder, Font font, JLabel emojiTextField, ChangeListener listener) {
        AtomicReference<JDialog> thisDialog = new AtomicReference<>();
        EmojiPickerPanel emojiPickerPanel = new EmojiPickerPanel(
                EmojiRepository.getLocalEmojis(true),
                font, 50,
                em -> {
                    if (em != null) {
                            // Update the emoji in the map and text field
                            CESymbol.Utf emoji = CESymbol.of(em.symbol());
                            holder.setSymbol(emoji);
                            emoji.applyToLabel("", emojiTextField);  // Update the text field with the selected emoji

                            syncSettings(listener);
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
        JDialog dialog = optionPane.createDialog("Select an Emoji");
        thisDialog.set(dialog);

        dialog.setVisible(true);

    }


}