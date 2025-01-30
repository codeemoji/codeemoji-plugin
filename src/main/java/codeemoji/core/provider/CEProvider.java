package codeemoji.core.provider;

import codeemoji.core.util.CEBundle;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.codeInsight.hints.SettingsKey;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsCustomSettingsProvider;
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

// Class that providers both the hints collectors and the configurables
@Getter
public abstract class CEProvider<S> implements InlayHintsProvider, InlayHintsCustomSettingsProvider<S> {

    private static final Logger LOG = Logger.getInstance(CEProvider.class);

    private final SettingsKey<S> key;
    private S settings;

    protected CEProvider() {
         settings = createSettings();
        key = new SettingsKey<>(getClass().getSimpleName().toLowerCase());
    }


    @Nullable
    @Override
    public abstract InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor);


    //new config stuff

    private S settingsCopy;

    @Override
    public @NotNull JComponent createComponent(@NotNull Project project, @NotNull Language language) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Enable custom inlay hints:");
        JCheckBox checkBox = new JCheckBox();
        // checkBox.setSelected(settingsCopy.isEnabled());
        //   checkBox.addActionListener(e -> settingsCopy.setEnabled(checkBox.isSelected()));
        panel.add(label);
        panel.add(checkBox);
        return panel;
    }

    @Override
    public boolean isDifferentFrom(@NotNull Project project, S settings) {
        return !settingsCopy.equals(settings);
    }

    @Override
    public @NotNull S getSettingsCopy() {
        return null;
    }

    @Override
    public void putSettings(@NotNull Project project, S settings, @NotNull Language language) {
        settingsCopy = settings;
    }

    @Override
    public void persistSettings(@NotNull Project project, S settings, @NotNull Language language) {
        //   settings.save(project);
    }


    @Nullable
    public abstract String getPreviewText();

    // Config stuff
    public final @NotNull String getKeyId() {
        return getKey().getId();
    }

    // @Nls
    //@Override
    public String getProperty(@NotNull String key) {
        return CEBundle.getString(key);
    }

    public String getName() {
        return "NAME";
    }

    // @Override
    public @NotNull ImmediateConfigurable createConfigurable(@NotNull S settings) {
        return new MyImmediateConfigurable();
    }

    private static class MyImmediateConfigurable implements ImmediateConfigurable {
        @Override
        public @NotNull JComponent createComponent(@NotNull ChangeListener changeListener) {
            return FormBuilder.createFormBuilder().getPanel();
        }
    }

    @SuppressWarnings("unchecked")
    public final @NotNull S createSettings() {
        if (null == settings) {
            try {
                var type = (Class<S>) ((ParameterizedType) getClass()
                        .getGenericSuperclass()).getActualTypeArguments()[0];
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
    /*





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





     */
}
