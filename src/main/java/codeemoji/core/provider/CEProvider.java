package codeemoji.core.provider;

import codeemoji.core.collector.CECollectorMulti;
import codeemoji.core.collector.InlayVisuals;
import codeemoji.core.collector.base.CEClassCollector;
import codeemoji.core.collector.base.CEMethodCollector;
import codeemoji.core.collector.base.CEReferenceClassCollector;
import codeemoji.core.collector.base.CEReferenceMethodCollector;
import codeemoji.core.collector.base.simple.CESimpleClassCollector;
import codeemoji.core.collector.base.simple.CESimpleMethodCollector;
import codeemoji.core.collector.base.simple.CESimpleReferenceClassCollector;
import codeemoji.core.collector.base.simple.CESimpleReferenceMethodCollector;
import codeemoji.core.settings.CEBaseConfigurableWindow;
import codeemoji.core.settings.CEBaseSettings;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector;
import com.intellij.codeInsight.hints.declarative.InlayHintsCustomSettingsProvider;
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider;
import com.intellij.codeInsight.hints.declarative.SharedBypassCollector;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

// Class that providers both the hints collectors and the configurable
@Getter
public abstract class CEProvider<S extends CEBaseSettings<S>> implements InlayHintsProvider, InlayHintsCustomSettingsProvider<S> {

    private S settings;
    private final CEBaseConfigurableWindow<S> window;
    private final String key;

    protected CEProvider() {
        settings = createSettings();
        window = createConfigurable();
        key = getClass().getSimpleName().toLowerCase(Locale.ROOT);
    }

    protected abstract void createCollectors(Builder builder, @NotNull PsiFile psiFile, Editor editor);

    @Override
    public final InlayHintsCollector createCollector(@NotNull PsiFile psiFile, @NotNull Editor editor) {
        Builder builder = new Builder(editor);
        createCollectors(builder, psiFile, editor);
        return builder.build();
    }

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
        //TODO: figure these out. also what about the setting own save method?
        //   settings.save(project);
    }

    public @NotNull CEBaseConfigurableWindow<S> createConfigurable() {
        return new CEBaseConfigurableWindow<>();
    }

    // encapsulate and delegates the UI behavior to a dedicated object that is composed instead of implemented directly into this class createComponent
    @Override
    public final @NotNull JComponent createComponent(@NotNull Project project, @NotNull Language language) {
        return window.createComponent(settings, "something", project, language, () -> {
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


    public class Builder {
        private final Editor editor;
        private final List<SharedBypassCollector> collectors = new ArrayList<>();

        public Builder(Editor editor) {
            this.editor = editor;
        }

        public Builder add(SharedBypassCollector collector) {
            collectors.add(collector);
            return this;
        }

        public Builder addIf(boolean condition, SharedBypassCollector collector) {
            if (condition) {
                collectors.add(collector);
            }
            return this;
        }

        public Builder addSimpleMethodCollector(Function<PsiMethod, Boolean> needsInlayFunc) {
            if (!settings.appliesToMethods()) return this;
            addIf(getSettings().isIncludeReferences(),
                    new CESimpleReferenceMethodCollector(editor, CEProvider.this) {
                        @Override
                        protected boolean needsInlay(@NotNull PsiMethod element) {
                            if (getSettings().isOnlyInProject() && !CEUtils.isFromCurrentProject(element)) {
                                return false;
                            }
                            return needsInlayFunc.apply(element);
                        }
                    });
            add(new CESimpleMethodCollector(editor, CEProvider.this) {
                @Override
                protected boolean needsInlay(@NotNull PsiMethod element) {
                    if (getSettings().isOnlyInProject() && !CEUtils.isFromCurrentProject(element)) {
                        return false;
                    }
                    return needsInlayFunc.apply(element);
                }
            });
            return this;
        }

        public Builder addSimpleClassCollector(Function<PsiClass, Boolean> needsInlayFunc) {
            if (!settings.appliesToClasses()) return this;
            addIf(getSettings().isIncludeReferences(),
                    new CESimpleReferenceClassCollector(editor, CEProvider.this) {
                        @Override
                        protected boolean needsInlay(@NotNull PsiClass element) {
                            if (getSettings().isOnlyInProject() && !CEUtils.isFromCurrentProject(element)) {
                                return false;
                            }
                            return needsInlayFunc.apply(element);
                        }
                    });
            add(new CESimpleClassCollector(editor, CEProvider.this) {
                @Override
                protected boolean needsInlay(@NotNull PsiClass element) {
                    if (getSettings().isOnlyInProject() && !CEUtils.isFromCurrentProject(element)) {
                        return false;
                    }
                    return needsInlayFunc.apply(element);
                }
            });
            return this;
        }

        public Builder addMethodCollector(Function<PsiMethod, InlayVisuals> inlayFunc) {
            if (!settings.appliesToMethods()) return this;
            addIf(getSettings().isIncludeReferences(),
                    new CEReferenceMethodCollector(editor, CEProvider.this.getKey()) {
                        @Override
                        protected InlayVisuals createInlayFor(@NotNull PsiMethod element) {
                            if (getSettings().isOnlyInProject() && !CEUtils.isFromCurrentProject(element)) {
                                return null;
                            }
                            return inlayFunc.apply(element);
                        }
                    });
            add(new CEMethodCollector(editor, CEProvider.this.getKey()) {
                @Override
                protected InlayVisuals createInlayFor(@NotNull PsiMethod element) {
                    if (getSettings().isOnlyInProject() && !CEUtils.isFromCurrentProject(element)) {
                        return null;
                    }
                    return inlayFunc.apply(element);
                }
            });
            return this;
        }

        public Builder addClassCollector(Function<PsiClass, InlayVisuals> inlayFunc) {
            if (!settings.appliesToClasses()) return this;
            addIf(getSettings().isIncludeReferences(),
                    new CEReferenceClassCollector(editor, CEProvider.this.getKey()) {
                        @Override
                        protected InlayVisuals createInlayFor(@NotNull PsiClass element) {
                            if (getSettings().isOnlyInProject() && !CEUtils.isFromCurrentProject(element)) {
                                return null;
                            }
                            return inlayFunc.apply(element);
                        }
                    });
            add(new CEClassCollector(editor, CEProvider.this.getKey()) {
                @Override
                protected InlayVisuals createInlayFor(@NotNull PsiClass element) {
                    if (getSettings().isOnlyInProject() && !CEUtils.isFromCurrentProject(element)) {
                        return null;
                    }
                    return inlayFunc.apply(element);
                }
            });
            return this;
        }

        SharedBypassCollector build() {
            if (collectors.isEmpty()) {
                // throw new IllegalStateException("No collectors were added to the builder.");
            }
            if (collectors.size() == 1) {
                return collectors.get(0);
            } else return new CECollectorMulti(collectors);
        }
    }

}
