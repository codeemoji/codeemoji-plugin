package codeemoji.core.wip;

import com.intellij.codeInsight.hints.declarative.DeclarativeInlayHintsSettings;
import com.intellij.codeInsight.hints.declarative.InlayHintsProviderExtensionBean;
import com.intellij.codeInsight.hints.settings.InlayProviderSettingsModel;
import com.intellij.codeInsight.hints.settings.InlaySettingsProvider;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TestSettings implements InlaySettingsProvider {

    @Override
    public List<InlayProviderSettingsModel> createModels(Project project, Language language) {
        List<InlayHintsProviderExtensionBean> providerDescriptions = InlayHintsProviderExtensionBean.Companion.getEP().getExtensionList();
        DeclarativeInlayHintsSettings settings = DeclarativeInlayHintsSettings.Companion.getInstance();

        return providerDescriptions.stream()
                .filter(bean -> Language.findLanguageByID(bean.getLanguage()) == language)
                .map(bean -> {
                    Boolean isEnabled = settings.isProviderEnabled(bean.requiredProviderId());
                    if (isEnabled == null) {
                        isEnabled = bean.isEnabledByDefault();
                    }
                    return new TestModel(bean, isEnabled, language, project);
                })
                .collect(Collectors.toList());
    }

    /*
    @Override
    public List<InlayProviderSettingsModel> createModels(Project project, Language language) {
        InlayHintsSettings config = InlayHintsSettings.instance();
        return HintUtils.INSTANCE.getHintProvidersForLanguage(language).stream()
                .filter(providerWithSettings -> providerWithSettings.getProvider().isVisibleInSettings())
                .map(providerWithSettings -> new NewInlayProviderSettingsModel<>(providerWithSettings.withSettingsCopy(), config))
                .collect(Collectors.toList());
    }*/

    @Override
    public Collection<Language> getSupportedLanguages(Project project) {
        return InlayHintsProviderExtensionBean.Companion.getEP().getExtensionList().stream()
                .map(bean -> Language.findLanguageByID(bean.getLanguage()))
                .filter(language -> language != null)
                .collect(Collectors.toSet());
    }
}
