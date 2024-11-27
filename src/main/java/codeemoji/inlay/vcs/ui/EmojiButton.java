package codeemoji.inlay.vcs.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class EmojiButton extends JButton {

    private final boolean isFilled;
    private boolean selected = false;

    public EmojiButton(Emoji emoji, Font font,
                       int size, boolean fill,
                       Consumer<Emoji> callback,
                       Consumer<Emoji> onHover) {

        isFilled = fill;
        setText(emoji.symbol());
        setFont(font);
        setContentAreaFilled(fill);
        setBorderPainted(false);
        setPreferredSize(new Dimension(size, size)); // Make the button square
        // Add action listener to handle emoji selection
        addActionListener(e -> {
            boolean newSelected = !selected;
            if (isFilled) {
                //if lost focus remove border
                setBorderPainted(selected);
            }
            callback.accept(newSelected ? emoji : null);
            selected = newSelected;
            if(selected){
                requestFocus();
                if(isFilled)setBorderPainted(true);
            }
        });

        // Add mouse listener for hover effect
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (fill) setBorderPainted(true);
                else setContentAreaFilled(true);
                repaint(); // Force the button to repaint immediately
                if (onHover != null)
                    onHover.accept(emoji); // Update the info label with the emoji description
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (fill) {
                    if (!hasFocus()){
                        setBorderPainted(false);
                    }
                } else setContentAreaFilled(false);
                repaint(); // Force the button to repaint immediately
                if (onHover != null)
                    onHover.accept(null); // Reset the info label
            }
        });
    }

    public void unselect() {
        selected = false;
        transferFocus();
        repaint();
    }

    @Override
    public boolean hasFocus() {
        return isFilled && selected;
    }
}
