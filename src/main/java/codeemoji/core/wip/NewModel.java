package codeemoji.core.wip;// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

/*
public class NewModel<T> extends InlayProviderSettingsModel {
    private final ProviderWithSettings<T> providerWithSettings;
    private final InlayHintsSettings config;
    // Lazy initialization of the component.
    private JComponent component;

    public NewModel(@NotNull ProviderWithSettings<T> providerWithSettings, @NotNull InlayHintsSettings config) {
        // Call to the super constructor. Assuming the base class constructor is defined as:
        // InlayProviderSettingsModel(boolean isEnabled, String id, Language language)
        super(config.hintsEnabled(providerWithSettings.getProvider().getKey(), providerWithSettings.getLanguage()),
                providerWithSettings.getProvider().getKey().getId(),
                providerWithSettings.getLanguage());
        this.providerWithSettings = providerWithSettings;
        this.config = config;
    }

    @Override
    public @NotNull String getName() {
        return providerWithSettings.getProvider().getName();
    }

    @Override
    public @NotNull String getMainCheckBoxLabel() {
        return providerWithSettings.getConfigurable().getMainCheckboxText();
    }

    @Override
    public @NotNull InlayGroup getGroup() {
        return providerWithSettings.getProvider().getGroup();
    }

    @Override
    public PsiFile createFile(@NotNull Project project, @NotNull FileType fileType, @NotNull Document document) {
        return providerWithSettings.getProvider().createFile(project, fileType, document);
    }

    @Override
    public @Nullable String getDescription() {
        return providerWithSettings.getProvider().getDescription();
    }

    @Override
    public @NotNull JComponent getComponent() {
        if (component == null) {
            // onChangeListener is assumed to be provided by the base class (non-null).
            component = providerWithSettings.getConfigurable().createComponent(getOnChangeListener());
        }
        return component;
    }

    @Override
    public Runnable collectData(@NotNull Editor editor, @NotNull PsiFile file) {
        // Prepare preview first.
        providerWithSettings.getProvider().preparePreview(editor, file, providerWithSettings.getSettings());
        InlayHintsSinkImpl inlaySink = new InlayHintsSinkImpl(editor);
        Object collectorWrapper = providerWithSettings.getCollectorWrapperFor(file, editor, providerWithSettings.getLanguage(), inlaySink);
        if (collectorWrapper == null) {
            return new Runnable() {
                @Override
                public void run() {
                    // no-op
                }
            };
        }
        // CASE_KEY is assumed to be a key to retrieve some case settings attached to the editor.
        ImmediateConfigurable.Case cas = CASE_KEY.get(editor);
        boolean enabled = cas != null ? cas.getValue() : isEnabled();
        // Backup current values.
        List<Boolean> backup = new ArrayList<>();
        for (ImmediateConfigurable.Case c : getCases()) {
            backup.add(c.getValue());
        }
        HintsBuffer hintsBuffer;
        try {
            // Set all cases to false.
            for (ImmediateConfigurable.Case c : getCases()) {
                c.setValue(false);
            }
            if (cas != null) {
                cas.setValue(true);
            }
            // The collectorWrapper is expected to have a method collectTraversing(...)
            hintsBuffer = ((InlayHintsCollectorWrapper)collectorWrapper).collectTraversing(editor, file, true);
            if (!enabled) {
                // Strikeout the hints.
                // strikeOutBuilder(editor) and addStrikeout(...) are assumed to be accessible helper methods.
                Object builder = strikeOutBuilder(editor);
                addStrikeout(hintsBuffer.getInlineHints(), builder, (root, constraints) -> new HorizontalConstrainedPresentation(root, constraints));
                addStrikeout(hintsBuffer.getBlockAboveHints(), builder, (root, constraints) -> new BlockConstrainedPresentation(root, constraints));
                addStrikeout(hintsBuffer.getBlockBelowHints(), builder, (root, constraints) -> new BlockConstrainedPresentation(root, constraints));
            }
        }
        finally {
            // Restore the backed up values.
            for (int i = 0; i < getCases().size(); i++) {
                getCases().get(i).setValue(backup.get(i));
            }
        }
        return new Runnable() {
            @Override
            public void run() {
                ((InlayHintsCollectorWrapper)collectorWrapper).applyToEditor(file, editor, hintsBuffer);
            }
        };
    }

    @Override
    public @NotNull List<ImmediateConfigurable.Case> getCases() {
        return providerWithSettings.getConfigurable().getCases();
    }

    @Override
    public @Nullable String getPreviewText() {
        return providerWithSettings.getProvider().getPreviewText();
    }

    @Override
    public @Nullable String getCasePreview(@Nullable ImmediateConfigurable.Case aCase) {
        return getCasePreview(providerWithSettings.getLanguage(), providerWithSettings.getProvider(), aCase);
    }

    @Override
    public @NotNull Language getCasePreviewLanguage(@Nullable ImmediateConfigurable.Case aCase) {
        return providerWithSettings.getLanguage();
    }

    @Override
    public @Nullable String getCaseDescription(@NotNull ImmediateConfigurable.Case aCase) {
        // The key is built but not used in the Kotlin code.
        String key = "inlay." + providerWithSettings.getProvider().getKey().getId() + "." + aCase.getId();
        return providerWithSettings.getProvider().getCaseDescription(aCase);
    }

    @Override
    public void apply() {
        ProviderWithSettings<T> copy = providerWithSettings.withSettingsCopy();
        config.storeSettings(copy.getProvider().getKey(), copy.getLanguage(), copy.getSettings());
        config.changeHintTypeStatus(copy.getProvider().getKey(), copy.getLanguage(), isEnabled());
    }

    @Override
    public boolean isModified() {
        if (isEnabled() != config.hintsEnabled(providerWithSettings.getProvider().getKey(), providerWithSettings.getLanguage())) {
            return true;
        }
        T inSettings = providerWithSettings.getSettings();
        T stored = providerWithSettings.getProvider().getActualSettings(config, providerWithSettings.getLanguage());
        return !inSettings.equals(stored);
    }

    @Override
    public String toString() {
        return getLanguage().getDisplayName() + ": " + getName();
    }

    @Override
    public void reset() {
        // Workaround for deep copy.
        T obj = providerWithSettings.getProvider().getActualSettings(config, providerWithSettings.getLanguage());
        Element element = serialize(obj, (o, field) -> true);
        if (element != null) {
            deserializeInto(element, providerWithSettings.getSettings());
        }
        providerWithSettings.getConfigurable().reset();
        setEnabled(config.hintsEnabled(providerWithSettings.getProvider().getKey(), providerWithSettings.getLanguage()));
    }

    // --- Static methods translated from the Kotlin file ---

    public static @Nullable String getCasePreview(@NotNull Language language, @NotNull Object provider, @Nullable ImmediateConfigurable.Case aCase) {
        String key;
        if (provider instanceof com.intellij.codeInsight.hints.InlayHintsProvider) {
            key = ((com.intellij.codeInsight.hints.InlayHintsProvider<?>) provider).getKey().getId();
        }
        else {
            key = "Parameters";
        }
        FileType fileType = language.getAssociatedFileType();
        if (fileType == null) {
            fileType = PlainTextFileType.INSTANCE;
        }
        String preview = getStream(key, aCase, provider, fileType.getDefaultExtension());
        if (preview == null) {
            preview = getStream(key, aCase, provider, "dockerfile");
        }
        return preview;
    }

    private static @Nullable String getStream(@NotNull String key,
                                              @Nullable ImmediateConfigurable.Case aCase,
                                              @NotNull Object provider,
                                              @NotNull String extension) {
        String caseId = aCase != null ? aCase.getId() : "preview";
        String path = "inlayProviders/" + key + "/" + caseId + "." + extension;
        InputStream stream = provider.getClass().getClassLoader().getResourceAsStream(path);
        if (stream != null) {
            return ResourceUtil.loadText(stream);
        }
        return null;
    }

    // ---
    // The following methods are placeholders. In a real conversion, these should refer to the actual implementations.
    //
    // Returns the onChangeListener from the base class or elsewhere.
    private Object getOnChangeListener() {
        // Assuming that the base class or context provides this.
        return onChangeListener;
    }

    // Placeholder for strikeOutBuilder.
    private Object strikeOutBuilder(Editor editor) {
        // Actual implementation should return an object used by addStrikeout.
        return new Object();
    }

    // Placeholder for addStrikeout. The lambda is represented as a functional interface.
    private <P> void addStrikeout(List<P> hints, Object builder, StrikeoutPresentationFactory<P> factory) {
        // Actual implementation should process hints.
        // This is only a placeholder.
    }

    // Functional interface to simulate the lambda.
    @FunctionalInterface
    private interface StrikeoutPresentationFactory<P> {
        Object create(P root, Object constraints);
    }

}
*/