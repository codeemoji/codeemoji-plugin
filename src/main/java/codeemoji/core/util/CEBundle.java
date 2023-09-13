package codeemoji.core.util;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

@Getter
public final class CEBundle {

    final ResourceBundle bundle;

    private CEBundle() {
        this.bundle = ResourceBundle.getBundle("CEBundle");
    }

    @SuppressWarnings("SameReturnValue")
    private static @NotNull CEBundle getInstance() {
        return CEBundleHolder.INSTANCE;
    }

    public static @NotNull String getString(@NotNull String key) {
        return getInstance().getBundle().getString(key);
    }

    private static final class CEBundleHolder {
        private static final CEBundle INSTANCE = new CEBundle();
    }
}