package codeemoji.core.provider;

import codeemoji.core.util.CEBundle;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsProvider;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.codeInsight.hints.SettingsKey;
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
public abstract class CEProvider<S> implements InlayHintsProvider<S> {

    private static final Logger LOG = Logger.getInstance(CEProvider.class);

    private final SettingsKey<S> key;
    private S settings;

    protected CEProvider() {
        settings = createSettings();
        key = new SettingsKey<>(getClass().getSimpleName().toLowerCase());
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
        return new MyImmediateConfigurable();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final @NotNull S createSettings() {
        if (null == settings) {
            try {
                var type = (Class<S>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                var genericType = type.getDeclaredConstructor().newInstance();
                if (genericType instanceof NoSettings) {
                    settings = (S) new NoSettings();
                } else if (genericType instanceof PersistentStateComponent<?> typeT) {
                    settings = (S) ApplicationManager.getApplication().getService(typeT.getClass());
                } else {
                    throw new CEProviderException();
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException ex) {
                LOG.error(ex);
            }
        }
        return settings;
    }

    public final @NotNull String getKeyId() {
        return getKey().getId();
    }

    //TODO: deprecate and replace with the second one
    protected InlayHintsCollector buildCollector(Editor editor){
        throw new IllegalStateException("at least one build collection must be implemented");
    };

    protected InlayHintsCollector buildCollector(PsiFile psiFile, Editor editor){
        return buildCollector(editor);
    }

    @Override
    public final InlayHintsCollector getCollectorFor(@NotNull PsiFile psiFile, @NotNull Editor editor, @NotNull S settings, @NotNull InlayHintsSink inlayHintsSink) {
        return buildCollector(psiFile, editor);
    }

    @Override
    public final boolean isLanguageSupported(@NotNull Language language) {
        return "JAVA".equals(language.getID());
    }

    private static class MyImmediateConfigurable implements ImmediateConfigurable {
        @Override
        public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
            return FormBuilder.createFormBuilder().getPanel();
        }
    }
}
