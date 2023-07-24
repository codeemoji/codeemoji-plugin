package codeemoji.core;

import com.intellij.codeInsight.hints.*;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import lombok.Data;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ResourceBundle;

@Data
public abstract class CEProvider<T> implements InlayHintsProvider<T> {

    final ResourceBundle bundle = ResourceBundle.getBundle("CodeemojiBundle");
    T settings;

    public CEProvider() {
        this.settings = createSettings();
    }

    @NotNull
    @Override
    public SettingsKey<T> getKey() {
        return new SettingsKey<>(getName().replaceAll(" ", "").trim().toLowerCase());
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return bundle.getString(getBaseKey() + ".name");
    }

    public String getHeader() {
        return bundle.getString(getBaseKey() + ".header");
    }

    @NotNull
    @Override
    public ImmediateConfigurable createConfigurable(@NotNull T settings) {
        return new CEConfigurable(getHeader());
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull T createSettings() {
        if (settings == null) {
            try {
                Class<T> type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                T genericType = type.getDeclaredConstructor().newInstance();
                if (genericType instanceof NoSettings) {
                    this.settings = (T) new NoSettings();
                } else if (genericType instanceof PersistentStateComponent<?> typeT) {
                    this.settings = (T) ApplicationManager.getApplication().getService(typeT.getClass());
                } else {
                    throw new RuntimeException("Settings must be 'NoSettings' or 'PersistentStateComponent' type.");
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return settings;
    }

    @Nullable
    @Override
    public InlayHintsCollector getCollectorFor(@NotNull PsiFile psiFile, @NotNull Editor editor, @NotNull T settings, @NotNull InlayHintsSink inlayHintsSink) {
        return getCollector(editor);
    }

    @Override
    public boolean isLanguageSupported(@NotNull Language language) {
        return "JAVA".equals(language.getID());
    }


    public @NotNull String getBaseKey() {
        return getClass().getSimpleName().toLowerCase();
    }

    public abstract InlayHintsCollector getCollector(@NotNull Editor editor);

}