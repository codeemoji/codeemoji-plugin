package codeemoji.core.util;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

@Getter
public class CEBundle {

    final ResourceBundle bundle;

    private CEBundle() {
        this.bundle = ResourceBundle.getBundle("CEBundle");
    }

    @SuppressWarnings("SameReturnValue")
    private static CEBundle getInstance() {
        return CEBundleHolder.instance;
    }

    public static @NotNull String getString(String key) {
        return getInstance().getBundle().getString(key);
    }

    private static final class CEBundleHolder {
        private static final CEBundle instance = new CEBundle();
    }
}