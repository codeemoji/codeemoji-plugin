package codeemoji.core.base;

import codeemoji.core.ui.EmojiPickerPanel;
import codeemoji.core.ui.EmojiRepository;
import codeemoji.core.util.CESymbol;
import codeemoji.core.util.CESymbolHolder;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

// a configurable class that holds a symbol list
public class CEBaseConfigurable<S extends CEBaseSettings<S>> implements ImmediateConfigurable {

    protected final S settings;
    protected final List<CESymbolHolder> localSymbols = new ArrayList<>();

    public CEBaseConfigurable(S settings) {
        this.settings = settings;
    }

    @Override
    public @NotNull JComponent createComponent(ChangeListener listener) {
        localSymbols.clear();
        for (var s : settings.getSymbols()) {
            localSymbols.add(s.makeCopy());
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (var holder : localSymbols) {
            addSymbolRow(panel, holder, listener);
        }
        return panel;
    }

    private void addSymbolRow(JPanel panel, CESymbolHolder holder, ChangeListener listener) {
        // Create label
        JLabel label = holder.getSymbol().createLabel(holder.getName());

        // Create button
        JButton pickEmojiButton = new JButton("Edit");
        pickEmojiButton.addActionListener(e -> createPickEmojiMenu(label, holder, true, listener));

        // Create a sub-panel for label and button with horizontal layout
        JPanel labelButtonPanel = new JPanel(new BorderLayout(10, 0)); // Add some horizontal spacing
        labelButtonPanel.add(label, BorderLayout.CENTER); // Label on the left
        labelButtonPanel.add(pickEmojiButton, BorderLayout.EAST); // Button on the right
        labelButtonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));

        // Add the sub-panel to the main panel
        panel.add(labelButtonPanel);
    }

    protected void createPickEmojiMenu(JLabel label, CESymbolHolder holder,
                                      boolean hasName, ChangeListener listener) {
        AtomicReference<JDialog> thisDialog = new AtomicReference<>();
        EmojiPickerPanel emojiPickerPanel = new EmojiPickerPanel(
                EmojiRepository.getLocalEmojis(true),
                new JLabel().getFont(), 50,
                em -> {
                    if (em != null) {
                        CESymbol.Utf emoji = CESymbol.of(em.symbol());
                        holder.setSymbol(emoji);
                        emoji.applyToLabel(hasName ? holder.getName() : "", label);

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

    protected void syncSettings(ChangeListener listener) {
        var copy = new ArrayList<CESymbolHolder>();
        for (CESymbolHolder pair : localSymbols) {
            copy.add(pair.makeCopy());
        }
        settings.setSymbols(copy);
        listener.settingsChanged();
    }

}
