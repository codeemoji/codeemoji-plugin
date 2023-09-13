package codeemoji.core.provider;

import codeemoji.core.util.CEBundle;
import com.intellij.codeInsight.hints.*;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

@Getter
@SuppressWarnings("UnstableApiUsage")
public abstract non-sealed class CEProvider<S> implements CEProviderInterface<S> {

    private static final Logger LOG = Logger.getInstance(CEProvider.class);

    private S settings;

    protected CEProvider() {
        settings = this.createSettings();
    }

    @Override
    public @NotNull SettingsKey<S> getKey() {
        return new SettingsKey<>(this.getClass().getSimpleName().toLowerCase());
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @Override
    public @NotNull String getName() {
        try {
            return Objects.requireNonNull(this.getProperty("inlay." + this.getKeyId() + ".name"));
        } catch (final RuntimeException ex) {
            return "<NAME_NOT_DEFINED>";
        }
    }

    @Nls
    @Override
    public String getProperty(@NotNull final String key) {
        return CEBundle.getString(key);
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull final S settings) {
        return new MyImmediateConfigurable();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull S createSettings() {
        if (null == settings) {
            try {
                final var type = (Class<S>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                final var genericType = type.getDeclaredConstructor().newInstance();
                if (genericType instanceof NoSettings) {
                    settings = (S) new NoSettings();
                } else if (genericType instanceof final PersistentStateComponent<?> typeT) {
                    settings = (S) ApplicationManager.getApplication().getService(typeT.getClass());
                } else {
                    throw new CEProviderException("Settings must be 'NoSettings' or 'PersistentStateComponent' type.");
                }
            } catch (final InstantiationException | IllegalAccessException | InvocationTargetException |
                           NoSuchMethodException ex) {
                CEProvider.LOG.error(ex);
            }
        }
        return this.settings;
    }

    @Override
    public final @NotNull String getKeyId() {
        return this.getKey().getId();
    }

    @Override
    public final InlayHintsCollector getCollectorFor(@NotNull final PsiFile psiFile, @NotNull final Editor editor, @NotNull final S settings, @NotNull final InlayHintsSink inlayHintsSink) {
        return this.buildCollector(editor);
    }

    @Override
    public final boolean isLanguageSupported(@NotNull final Language language) {
        return "JAVA".equals(language.getID());
    }

    private static class MyImmediateConfigurable implements ImmediateConfigurable {
        @Override
        public @NotNull JComponent createComponent(@NotNull final ChangeListener changeListener) {
            return FormBuilder.createFormBuilder().getPanel();
        }
    }
}
