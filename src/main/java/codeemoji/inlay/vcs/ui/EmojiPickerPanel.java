package codeemoji.inlay.vcs.ui;

import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EmojiPickerPanel extends JPanel {
    private final Map<String, List<Emoji>> emojiMap;
    private final Consumer<Emoji> emojiConsumer;
    private final JPanel emojiGridPanel;
    private final JScrollPane emojiGridScrollPane;
    private final Font font;
    private final JTextField searchField; // Search bar field
    private final int emojiSize;
    private final JLabel emojiInfoLabel; // Label to show hovered emoji info

    public EmojiPickerPanel(List<Emoji> emojis, Font font,
                            int buttonsSize, Consumer<Emoji> emojiConsumer) {
        this.emojiMap = new HashMap<>();
        emojis.forEach(emoji -> emojiMap.computeIfAbsent(emoji.category(), k -> new ArrayList<>()).add(emoji));
        this.emojiConsumer = emojiConsumer;
        this.font = font;
        this.emojiSize = buttonsSize;

        setLayout(new BorderLayout());

        // Create the search field
        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshEmojiGrid(filterEmojisByDescription(searchField.getText().trim()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshEmojiGrid(filterEmojisByDescription(searchField.getText().trim()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        searchField.setPreferredSize(new Dimension(0, 30));  // Optional: Set a height, if you want
        searchField.setFont(font);  // You can adjust the font here

        // Add the search field to the panel, this will make it stretch horizontally
        add(searchField, BorderLayout.NORTH);

        // Create the category selection panel (Vertical list of category icons)
        JPanel categoryPanel = new JPanel();
        categoryPanel.setLayout(new WrapLayout()); // Ensure vertical stacking

        // Add category buttons based on the first emoji in each group
        List<EmojiButton> categoryButtons = new ArrayList<>();
        emojiMap.keySet().forEach(category -> {
            EmojiButton categoryButton = createEmojiButton(emojiMap.get(category).get(0), buttonsSize * 3 / 4,
                    true, r -> {
                        showCategoryEmojis(r);
                        categoryButtons.forEach(EmojiButton::unselect);
                    }, null); // Smaller size for category button
            categoryButton.setToolTipText(category);
            categoryButtons.add(categoryButton);
            categoryPanel.add(categoryButton);
        });

        // Ensure the category panel doesn't stretch unnecessarily in the scroll pane
        JScrollPane categoryScrollPane = new JBScrollPane(categoryPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        categoryScrollPane.setPreferredSize(new Dimension(buttonsSize * 4/3, categoryPanel.getPreferredSize().height)); // Set preferred size to avoid scrollbar length issue
        categoryScrollPane.getVerticalScrollBar().setUnitIncrement(emojiSize / 4);
        add(categoryScrollPane, BorderLayout.WEST);

        // Create the emoji grid panel (which holds emoji buttons)
        emojiGridPanel = new JPanel();
        emojiGridPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 0, 0)); // Horizontal layout for emojis

        // Add the emoji grid panel inside a JScrollPane for vertical scrolling
        emojiGridScrollPane = new JBScrollPane(emojiGridPanel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        emojiGridScrollPane.getVerticalScrollBar().setUnitIncrement(emojiSize / 4);

        // Create a panel to hold the emoji grid and info label
        JPanel emojiGridAndInfoPanel = new JPanel();
        emojiGridAndInfoPanel.setLayout(new BorderLayout());  // Use BorderLayout to position components

        // Add the emoji grid panel
        emojiGridAndInfoPanel.add(emojiGridScrollPane, BorderLayout.CENTER);

        // Create a label to display hovered emoji info at the bottom
        emojiInfoLabel = new JLabel("", SwingConstants.CENTER);
        emojiInfoLabel.setFont(font);
        emojiInfoLabel.setPreferredSize(new Dimension(0, 40)); // Set a fixed height for the bottom info area
        emojiInfoLabel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        setHoveredEmoji(null); // Set initial text

        // Add the info label to the bottom of the panel
        emojiGridAndInfoPanel.add(emojiInfoLabel, BorderLayout.SOUTH);

        // Add the emoji grid and info panel to the center of the main panel
        add(emojiGridAndInfoPanel, BorderLayout.CENTER);

        // Initially display all emojis
        refreshEmojiGrid(filterEmojisByDescription(null));
    }

    // Method to create an emoji button with size control
    private EmojiButton createEmojiButton(Emoji emoji, int size, boolean filled,
                                          Consumer<Emoji> callback, Consumer<Emoji> onHover) {
        Font emojiFont = font.deriveFont((float) size * 0.5f);  // Adjust font size

        return new EmojiButton(emoji, emojiFont, size, filled, callback, onHover);
    }

    // Rewritten refreshEmojiGrid function with merged if-statements
    private List<Emoji> getEmojisByCategory(String category) {
        if (category == null) {
            return emojiMap.values().stream()
                    .flatMap(List::stream)
                    .toList();
        }
        return emojiMap.getOrDefault(category, List.of()); // Return empty list if no emojis found for category
    }

    // Method to filter emojis based on a description
    private List<Emoji> filterEmojisByDescription(String filter) {
        return emojiMap.values().stream()
                .flatMap(List::stream)
                .filter(emoji -> filter == null || emoji.description().contains(filter)) // Apply filter on description
                .toList();
    }

    private void refreshEmojiGrid(List<Emoji> emojis) {
        emojiGridPanel.removeAll(); // Clear existing emoji buttons

        // Display all emojis in the passed list
        emojis.forEach(emoji -> {
            EmojiButton emojiButton = createEmojiButton(emoji, emojiSize, false, emojiConsumer,
                    this::setHoveredEmoji); // Use the extracted callback method
            emojiGridPanel.add(emojiButton);
        });

        // Refresh the layout to trigger wrapping behavior
        emojiGridPanel.revalidate();
        emojiGridPanel.repaint();
        var scrollBar = emojiGridScrollPane.getVerticalScrollBar();
        if (scrollBar != null) scrollBar.setValue(0); // Scroll to the top
    }

    // Method to show emojis of a specific category
    private void showCategoryEmojis(@Nullable Emoji category) {
        if (category == null) {
            refreshEmojiGrid(filterEmojisByDescription(null));
        } else {
            refreshEmojiGrid(getEmojisByCategory(category.category()));
        }
    }

    private void setHoveredEmoji(@Nullable Emoji emoji) {
        if (emoji == null) {
            emojiInfoLabel.setText("Hover over an emoji to see its description");
        } else {
            emojiInfoLabel.setText(emoji.toString());
        }
    }
}
