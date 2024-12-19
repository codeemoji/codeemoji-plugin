package codeemoji.core.util;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;
import java.util.function.Supplier;

@Getter
public final class CEBundle {

    final ResourceBundle bundle;

    private CEBundle() {
        bundle = ResourceBundle.getBundle("CEBundle");
    }

    @SuppressWarnings("SameReturnValue")
    private static @NotNull CEBundle getInstance() {
        return CEBundleHolder.INSTANCE;
    }

    public static @NotNull String getString(@NotNull String key, @NotNull Object... args) {
        return String.format(getInstance().getBundle().getString(key), args);
    }
    public static Supplier<String> getLazyString(@NotNull String key, @NotNull Object... args) {
        return () -> getString(key, args);
    }

    private static final class CEBundleHolder {
        private static final CEBundle INSTANCE = new CEBundle();
    }
}