package codeemoji.core;

import lombok.Getter;

import java.util.ResourceBundle;

@Getter
public class CEBundle {

    private static volatile CEBundle INSTANCE;
    final ResourceBundle bundle;

    private CEBundle() {
        this.bundle = ResourceBundle.getBundle("CEBundle");
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