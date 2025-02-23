package codeemoji.inlay.vcs.wip;

import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.codeInsight.hints.InlayDumpUtil;
import com.intellij.codeInsight.hints.InlayGroup;
import com.intellij.codeInsight.hints.declarative.*;
import com.intellij.codeInsight.hints.declarative.impl.DeclarativeHintsPreviewProvider;
import com.intellij.codeInsight.hints.declarative.impl.DeclarativeInlayHintsPass;
import com.intellij.codeInsight.hints.settings.InlayProviderSettingsModel;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.EmptyProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import kotlin.Pair;
import kotlin.Unit;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TestModel extends InlayProviderSettingsModel {
    private static final Key<PreviewEntries> PREVIEW_ENTRIES = Key.create("declarative.inlays.preview.entries");

    private final InlayHintsProviderExtensionBean providerDescription;
    private final Project project;
    private final DeclarativeInlayHintsSettings settings = DeclarativeInlayHintsSettings.Companion.getInstance();
    private final InlayHintsCustomSettingsProvider<?> customSettingsProvider;
    private final List<MutableOption> options;
    private List<ImmediateConfigurable.Case> _cases;
    private Object savedSettings;

    public TestModel(InlayHintsProviderExtensionBean providerDescription, boolean isEnabled, Language language, Project project) {
        super(isEnabled, providerDescription.requiredProviderId(), language);
        this.providerDescription = providerDescription;
        this.project = project;
        var cs = InlayHintsCustomSettingsProvider.Companion.getCustomSettingsProvider(getId(), language);
        this.customSettingsProvider = (cs != null) ? cs : new DefaultSettingsProvider();
        this.options = loadOptionsFromSettings();
        this.savedSettings = customSettingsProvider.getSettingsCopy();
        this._cases = options.stream()
                .map(option -> new ImmediateConfigurable.Case(
                        option.description.getName(providerDescription),
                        option.description.requireOptionId(),
                        () -> option.isEnabled,
                        newValue -> {option.isEnabled = (newValue);return Unit.INSTANCE;},
                        option.description.getDescription(providerDescription)
                ))
                .collect(Collectors.toList());

    }

    private List<MutableOption> loadOptionsFromSettings() {
        List<MutableOption> loadedOptions = new ArrayList<>();
        for (InlayProviderOption option : providerDescription.getOptions()) {
            boolean enabledByDefault = option.getEnabledByDefault();
            Boolean isEnabled = settings.isOptionEnabled(option.requireOptionId(), providerDescription.requiredProviderId());
            isEnabled = (isEnabled != null) ? isEnabled : enabledByDefault;
            loadedOptions.add(new MutableOption(option, isEnabled));
        }
        return loadedOptions;
    }

    @Override
    public InlayGroup getGroup() {
        return providerDescription.requiredGroup();
    }

    @Override
    public String getName() {
        return providerDescription.getProviderName();
    }

    @Override
    public JComponent getComponent() {
        return customSettingsProvider.createComponent(project, getLanguage());
    }

    @Override
    public String getDescription() {
        return providerDescription.getDescription();
    }

    @Override
    public String getPreviewText() {
        String previewTextWithInlayPlaceholders = DeclarativeHintsPreviewProvider.INSTANCE.getPreview(getLanguage(), getId(), providerDescription.getInstance());
        if (previewTextWithInlayPlaceholders == null) {
            return null;
        }
        return InlayDumpUtil.INSTANCE.removeHints(previewTextWithInlayPlaceholders);
    }

    @Override
    public PsiFile createFile(Project project, FileType fileType, Document document, String caseId) {
        PsiFile file = super.createFile(project, fileType, document);
        String previewTextWithInlayPlaceholders = (caseId == null)
                ? DeclarativeHintsPreviewProvider.INSTANCE.getPreview(getLanguage(), getId(), providerDescription.getInstance())
                : DeclarativeHintsPreviewProvider.INSTANCE.getOptionPreview(getLanguage(), getId(), caseId, providerDescription.getInstance());

        if (previewTextWithInlayPlaceholders != null) {
            List<Pair<Integer, String>> inlayEntries = InlayDumpUtil.INSTANCE.extractEntries(previewTextWithInlayPlaceholders);
            file.putUserData(PREVIEW_ENTRIES, new PreviewEntries(caseId, inlayEntries));
        }
        return file;
    }

    @Override
    public String getCasePreview(ImmediateConfigurable.Case caseObj) {
        if (caseObj == null) {
            return getPreviewText();
        }
        String preview = DeclarativeHintsPreviewProvider.INSTANCE.getOptionPreview(getLanguage(), getId(), caseObj.getId(), providerDescription.getInstance());
        if (preview == null) {
            return null;
        }
        return InlayDumpUtil.INSTANCE.removeHints(preview);
    }

    @Override
    public Language getCasePreviewLanguage(ImmediateConfigurable.Case caseObj) {
        return getLanguage();
    }

    @Override
    public Runnable collectData(Editor editor, PsiFile file) {
        String providerId = providerDescription.requiredProviderId();
        Map<String, Boolean> enabledOptions = providerDescription.getOptions()
                .stream()
                .collect(Collectors.toMap(InlayProviderOption::requireOptionId, option -> true)); // enable all options
        PreviewEntries previewEntries = file.getUserData(PREVIEW_ENTRIES);
        String caseId = (previewEntries != null) ? previewEntries.getCaseId() : null;
        boolean enabled = (caseId != null) ? options.stream().anyMatch(option -> option.description.getOptionId().equals(caseId) && option.isEnabled) : isEnabled();
        DeclarativeInlayHintsPass pass = new DeclarativeInlayHintsPass(file, editor, List.of(new InlayProviderPassInfo(
                new InlayHintsProvider() {
                    @Override
                    public InlayHintsCollector createCollector(PsiFile file, Editor editor) {
                        return new OwnBypassCollector() {
                            @Override
                            public void collectHintsForFile(PsiFile file, InlayTreeSink sink) {
                                sink.addPresentation(new EndOfLinePosition(0),
                                        List.of(), null, true,b->
                                        {b.text("AAA",null);
                                        return Unit.INSTANCE;});
                                if (previewEntries == null) return;
                                for (Pair<Integer, String> entry : previewEntries.offsetToContent) {
                                    sink.addPresentation(new InlineInlayPosition(entry.getFirst(), true, 0),
                                            List.of(),
                                            null,
                                            true,
                                            b -> {b.text(

                                                   "AAAAAA"+ entry.getSecond(), null); return Unit.INSTANCE;});
                                }
                            }
                        };
                    }
                }, providerId, enabledOptions
        )), false, !enabled);

        pass.doCollectInformation(new EmptyProgressIndicator());
        return () -> pass.doApplyInformationToEditor();
    }

    @Override
    public void apply() {
        for (MutableOption option : options) {
            settings.setOptionEnabled(option.description.requireOptionId(), getId(), option.isEnabled);
        }
        settings.setProviderEnabled(getId(), isEnabled());
        savedSettings = persistSettings(customSettingsProvider);
    }

    private <T> T persistSettings(InlayHintsCustomSettingsProvider<T> cs) {
        T newSettingsCopy = cs.getSettingsCopy();
        cs.persistSettings(project, newSettingsCopy, getLanguage());
        return newSettingsCopy;
    }

    @Override
    public String getCaseDescription(ImmediateConfigurable.Case caseInstance) {
        String caseId = caseInstance.getId();
        InlayProviderOption option = providerDescription.getOptions()
                .stream()
                .filter(opt -> opt.requireOptionId().equals(caseId))
                .findFirst()
                .orElse(null);

        if (option == null) {
            return null;
        }
        return option.getDescription(providerDescription);
    }

    @Deprecated(since = "Not used in new UI")
    @Override
    public String getMainCheckBoxLabel() {
        return "";
    }

    @Override
    public boolean isModified() {
        if (isEnabled() != isProviderEnabledInSettings()) {
            return true;
        }

        if (isDifferentFrom(customSettingsProvider)) {
            return true;
        }

        return options.stream().anyMatch(option -> option.isEnabled != isOptionEnabledInSettings(option.description));
    }

    private <T> boolean  isDifferentFrom(InlayHintsCustomSettingsProvider<T> cs) {
        return cs.isDifferentFrom(project, (T) savedSettings);
    }

    @Override
    public void reset() {
        for (MutableOption option : options) {
            option.isEnabled = settings.isOptionEnabled(option.description.requireOptionId(), getId()) != null
                    ? settings.isOptionEnabled(option.description.requireOptionId(), getId())
                    : option.description.getEnabledByDefault();
        }
        settings.setProviderEnabled(providerDescription.requiredProviderId(), isProviderEnabledInSettings());
        resetSettings(customSettingsProvider);
    }

    private <T> void resetSettings(InlayHintsCustomSettingsProvider<T> cs) {
        cs.persistSettings(project, (T) savedSettings, getLanguage());
    }

    @Override
    public List<ImmediateConfigurable.Case> getCases() {
        if (_cases == null) {
            _cases = options.stream().map(option -> new ImmediateConfigurable.Case(option.description.getName(providerDescription),
                            option.description.requireOptionId(), () -> option.isEnabled, en->{option.isEnabled = en;return Unit.INSTANCE;},
                            option.description.getDescription(providerDescription)))
                    .collect(Collectors.toList());
        }
        return _cases;
    }

    private boolean isProviderEnabledInSettings() {
        Boolean providerEnabled = settings.isProviderEnabled(providerDescription.requiredProviderId());
        return providerEnabled != null ? providerEnabled : providerDescription.isEnabledByDefault();
    }

    private boolean isOptionEnabledInSettings(InlayProviderOption option) {
        Boolean enabled = settings.isOptionEnabled(option.requireOptionId(), getId());
        return enabled != null ? enabled : option.getEnabledByDefault();
    }

    private class MutableOption {
        InlayProviderOption description;
        boolean isEnabled;

        MutableOption(InlayProviderOption description, boolean isEnabled) {
            this.description = description;
            this.isEnabled = isEnabled;
        }
    }

    private static class PreviewEntries {
        String caseId;
        List<Pair<Integer, String>> offsetToContent;

        PreviewEntries(String caseId, List<Pair<Integer, String>> offsetToContent) {
            this.caseId = caseId;
            this.offsetToContent = offsetToContent;
        }

        public String getCaseId() {
            return caseId;
        }
    }

    @SuppressWarnings("NullableProblems")
    private static class DefaultSettingsProvider implements InlayHintsCustomSettingsProvider<Void> {
        private final JPanel component = new JPanel();

        @Override
        public JComponent createComponent(Project project, Language language) {
            return component;
        }

        @Override
        public Void getSettingsCopy() {
            return null;
        }

        @Override
        public void persistSettings(Project project, Void settings, Language language) {
        }

        @Override
        public void putSettings(Project project, Void settings, Language language) {
        }

        @Override
        public boolean isDifferentFrom(Project project, Void settings) {
            return false;
        }
    }
}
