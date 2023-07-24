package codeemoji.core;

import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public record CodeemojiConfig(String header) implements ImmediateConfigurable {

    @NotNull
    @Override
    public JComponent createComponent(@NotNull ChangeListener changeListener) {
        return FormBuilder.createFormBuilder()
                .addComponent(new JLabel(header()))
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }
}
