package codeemoji.core.util;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

@Getter
public class CEBundle {

    private static volatile CEBundle INSTANCE;
    final ResourceBundle bundle;

    private CEBundle() {
        this.bundle = ResourceBundle.getBundle("CEBundle");
    }

    private static CEBundle getInstance() {
        if (INSTANCE == null) {
            synchronized (CEBundle.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CEBundle();
                }
            }
        }
        return INSTANCE;
    }

    public static @NotNull String getString(String key) {
        return getInstance().getBundle().getString(key);
    }
}