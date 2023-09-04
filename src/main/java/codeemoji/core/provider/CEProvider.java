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
public abstract non-sealed class CEProvider<S> implements CEIProvider<S> {

    private static final Logger LOG = Logger.getInstance(CEProvider.class);

    private S settings;

    protected CEProvider() {
        this.settings = createSettings();
    }

    @Override
    public @NotNull SettingsKey<S> getKey() {
        return new SettingsKey<>(getClass().getSimpleName().toLowerCase());
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @Override
    public @NotNull String getName() {
        try {
            return Objects.requireNonNull(getProperty("inlay." + getKeyId() + ".name"));
        } catch (RuntimeException ex) {
            return "<NAME_NOT_DEFINED>";
        }
    }

    @Nls
    @Override
    public String getProperty(@NotNull String key) {
        return CEBundle.getString(key);
    }

    @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull S settings) {
        return new ImmediateConfigurable() {
            @Override
            public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
                return FormBuilder.createFormBuilder().getPanel();
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull S createSettings() {
        if (settings == null) {
            try {
                var type = (Class<S>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                var genericType = type.getDeclaredConstructor().newInstance();
                if (genericType instanceof NoSettings) {
                    this.settings = (S) new NoSettings();
                } else if (genericType instanceof PersistentStateComponent<?> typeT) {
                    this.settings = (S) ApplicationManager.getApplication().getService(typeT.getClass());
                } else {
                    throw new CEProviderException("Settings must be 'NoSettings' or 'PersistentStateComponent' type.");
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException ex) {
                LOG.error(ex);
            }
        }
        return settings;
    }

    @Override
    public final @NotNull String getKeyId() {
        return getKey().getId();
    }

    @Override
    public final InlayHintsCollector getCollectorFor(@NotNull PsiFile psiFile, @NotNull Editor editor, @NotNull S settings, @NotNull InlayHintsSink inlayHintsSink) {
        return buildCollector(editor);
    }

    @Override
    public final boolean isLanguageSupported(@NotNull Language language) {
        return "JAVA".equals(language.getID());
    }

}
