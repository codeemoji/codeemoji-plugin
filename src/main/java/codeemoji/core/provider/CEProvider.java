package codeemoji.core.provider;

import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CESymbol;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsCustomSettingsProvider;
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.function.Supplier;

// Class that providers both the hints collectors and the configurables
@Getter
public abstract class CEProvider<S extends CEBaseSettings<S>> implements InlayHintsProvider, InlayHintsCustomSettingsProvider<S> {

    private static final Logger LOG = Logger.getInstance(CEProvider.class);

    private S settings;
    private final CEConfigurableWindow<S> window;
    private final String key;

    protected CEProvider() {
        settings = createSettings();
        window = createConfigurable();
        key = ""; //TODO: make this the ID used in the xml
    }

    @Nullable
    @Override
    public abstract InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor);

    //TODO: remove
    public abstract String getPreviewText();

    @Override
    public boolean isDifferentFrom(@NotNull Project project, S newSettings) {
        return !settings.equals(newSettings);
    }

    @Override
    public @NotNull S getSettingsCopy() {
        return settings;
    }

    @Override
    public void putSettings(@NotNull Project project, S newSettings, @NotNull Language language) {
        settings = newSettings;
    }

    @Override
    public void persistSettings(@NotNull Project project, S settings, @NotNull Language language) {
        int aa = 1;
        //TODO: figure these out. also what about the setting own save method?
        //   settings.save(project);
    }

    public @NotNull CEConfigurableWindow<S> createConfigurable() {
        return new CEConfigurableWindow<>();
    }

    // encapsulate and delegates the UI behavior to a dedicated object that is composed instead of implemented directly into this class createComponent
    @Override
    public final @NotNull JComponent createComponent(@NotNull Project project, @NotNull Language language) {
        return window.createComponent(settings, project, language, () -> {
        });
    }

    // Config stuff

    // Reflection magic to instantiate an object of our generic
    @SuppressWarnings("unchecked")
    private @NotNull S createSettings() {
        try {
            var type = (Class<S>) ((ParameterizedType) getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0];
            var genericType = type.getDeclaredConstructor().newInstance();
            return (S) ApplicationManager.getApplication().getService(genericType.getClass());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }


    //helper

    public Supplier<CESymbol> mainSymbol() {
        return () -> this.getSettings().getMainSymbol();
    }

    /*


    @Nls(capitalization = Nls.Capitalization.Sentence)
    @Override
    public @NotNull String getName() {
        try {
            return Objects.requireNonNull(getProperty("text." + getKeyId() + ".name"));
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
