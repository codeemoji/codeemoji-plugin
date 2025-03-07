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

    private static @NotNull CEBundle getInstance() {
        return CEBundleHolder.INSTANCE;
    }

    public static @NotNull String getString(@NotNull String key, @NotNull Object... args) {
        try {
            return String.format(getInstance().getBundle().getString(key), args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Supplier<String> getLazyString(@NotNull String key, @NotNull Object... args) {
        return () -> getString(key, args);
    }

    public static String getOptional(String key, String orElse) {
        try {
            return getString(key);
        } catch (Exception e) {
            return orElse;
        }
    }

    private static final class CEBundleHolder {
        private static final CEBundle INSTANCE = new CEBundle();
    }
}