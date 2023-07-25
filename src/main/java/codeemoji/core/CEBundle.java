package codeemoji.core;

import lombok.Getter;

import java.util.ResourceBundle;

@Getter
public class CEBundle {

    private static volatile CEBundle INSTANCE;
    final ResourceBundle messages;

    private CEBundle() {
        this.messages = ResourceBundle.getBundle("CodeemojiBundle");
    }

    public static CEBundle getInstance() {
        if (INSTANCE == null) {
            synchronized (CEBundle.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CEBundle();
                }
            }
        }
        return INSTANCE;
    }
}