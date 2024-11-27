package codeemoji.inlay.vcs.authoravatar;

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
import java.util.concurrent.atomic.AtomicReference;

public record AuthorAvatarConfigurable(AuthorAvatarSettings settings) implements ImmediateConfigurable {
    public @NotNull JComponent createComponent(ChangeListener listener) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Add existing rows to the panel
        var currentPairs = new ArrayList<>(settings.getAvatars()); // Create a copy to avoid concurrent modification
        for (AuthorAvatarSettings.Pair pair : currentPairs) {
            addRow(panel, pair);
        }

        // Add the "Add" button at the end
        JButton addButton = new JButton("Add Avatar");
        addButton.addActionListener(e -> {
            AuthorAvatarSettings.Pair newPair = new AuthorAvatarSettings.Pair();
            newPair.setAuthor("Author " + (settings.getAvatars().size() + 1));
            newPair.setSymbol("\uD83D\uDC68\uFE0F");
            settings.getAvatars().add(newPair); // Add to the settings list
            settings.markDirty();
            addRow(panel, newPair); // Add the new row to the panel
        });

        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(addButton);

        panel.add(addPanel); // Add the "Add" button panel

        return panel;
    }

    private void addRow(JPanel panel, AuthorAvatarSettings.Pair pair) {
        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        int index = settings.getAvatars().indexOf(pair);

        // Create a text field for the author (editable)
        JTextField authorTextField = new JTextField(pair.getAuthor());
        authorTextField.setEditable(true);
        authorTextField.setPreferredSize(new Dimension(100, 30));

        // Update the Pair when the author name is changed
        authorTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                AuthorAvatarSettings.Pair pair = settings.getAvatars().get(index);
                if (pair == null) return;
                pair.setAuthor(authorTextField.getText()); // Update the Pair's author value
                settings.markDirty();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                AuthorAvatarSettings.Pair pair = settings.getAvatars().get(index);
                if (pair == null) return;
                pair.setAuthor(authorTextField.getText()); // Update the Pair's author value
                settings.markDirty();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });


        // Create a non-editable text field to display the emoji
        JTextField emojiTextField = new JTextField(pair.getSymbol());
        emojiTextField.setEditable(false); // Non-editable emoji
        emojiTextField.setFocusable(false);
        emojiTextField.setPreferredSize(new Dimension(50, 30));
        emojiTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                createPickEmojiMenu(index, rowPanel.getFont(), emojiTextField);
            }
        });

        // Create the button to pick a new emoji
        JButton pickEmojiButton = new JButton("Edit");
        pickEmojiButton.addActionListener(e -> createPickEmojiMenu(
                index, rowPanel.getFont(), emojiTextField));

        // Create the delete button to remove the current row
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            settings.getAvatars().remove(index); // Remove from the settings list
            settings.markDirty();
            panel.remove(rowPanel); // Remove the row from the panel
            panel.revalidate(); // Revalidate the panel to update the layout
            panel.repaint(); // Repaint the panel to apply changes
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

    private void createPickEmojiMenu(int pairIndex, Font font, JTextField emojiTextField) {
        AtomicReference<JDialog> thisDialog = new AtomicReference<>();
        EmojiPickerPanel emojiPickerPanel = new EmojiPickerPanel(
                EmojiRepository.getLocalEmojis(true),
                font, 50,
                em -> {
                    if (em != null) {
                        AuthorAvatarSettings.Pair dataPair = settings.getAvatars().get(pairIndex);
                        if (dataPair != null) {
                            // Update the emoji in the map and text field
                            dataPair.setSymbol(em.symbol());
                            emojiTextField.setText(em.symbol());  // Update the text field with the selected emoji
                            settings.markDirty();
                        }
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