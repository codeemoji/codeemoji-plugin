package codeemoji.inlay.showingspecifics;

import codeemoji.core.CEMultiProvider;
import codeemoji.core.config.CEConfigFile;
import codeemoji.core.config.CEProjectRule;
import codeemoji.core.enums.CEElementRule;
import codeemoji.core.enums.CEFeatureRule;
import codeemoji.core.util.CEEnumUtils;
import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.NoSettings;
import com.intellij.openapi.editor.Editor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static codeemoji.inlay.showingspecifics.ShowingSpecificsConstants.*;

public class ShowingSpecifics extends CEMultiProvider<NoSettings> {

    @Nullable
    @Override
    public String getPreviewText() {
        return null;
    }

    @Override
    public List<InlayHintsCollector> buildCollectors(Editor editor) {

        List<InlayHintsCollector> list = new ArrayList<>();

        CEConfigFile configFile = new CEConfigFile(editor);
        for (CEProjectRule rule : configFile.getProjectRules()) {
            CEElementRule elementRule = CEEnumUtils.getEnumByValue(CEElementRule.class, rule.element());
            CEFeatureRule featureRule = CEEnumUtils.getEnumByValue(CEFeatureRule.class, rule.feature());
            if (elementRule != null && featureRule != null) {
                switch (elementRule) {
                    case CLASS -> {
                        switch (featureRule) {
                            case ANNOTATIONS -> list.add(
                                    new ShowingSpecificsClassAnnotationsCollector(editor, getKeyId(), ANNOTATIONS_SYMBOL, rule.values()));
                            case EXTENDS -> list.add(
                                    new ShowingSpecificsClassExtendsCollector(editor, getKeyId(), EXTENDS_SYMBOL, rule.values()));
                            case IMPLEMENTS -> list.add(
                                    new ShowingSpecificsClassImplementsCollector(editor, getKeyId(), IMPLEMENTS_SYMBOL, rule.values()));
                        }
                    }
                    case FIELD -> {
                        switch (featureRule) {
                            case ANNOTATIONS -> list.add(
                                    new ShowingSpecificsFieldAnnotationsCollector(editor, getKeyId(), ANNOTATIONS_SYMBOL, rule.values()));
                            case TYPES -> list.add(
                                    new ShowingSpecificsFieldTypesCollector(editor, getKeyId(), TYPES_SYMBOL, rule.values()));
                        }
                    }
                    case METHOD -> {
                        switch (featureRule) {
                            case ANNOTATIONS -> list.add(
                                    new ShowingSpecificsMethodAnnotationsCollector(editor, getKeyId(), ANNOTATIONS_SYMBOL, rule.values()));
                            case RETURNS -> list.add(
                                    new ShowingSpecificsMethodReturnsCollector(editor, getKeyId(), RETURNS_SYMBOL, rule.values()));
                        }
                    }
                    case LOCALVARIABLE -> {
                        if (featureRule == CEFeatureRule.TYPES) {
                            list.add(
                                    new ShowingSpecificsLocalVariableTypesCollector(editor, getKeyId(), TYPES_SYMBOL, rule.values()));
                        }
                    }
                }
            }
        }
        return list;
    }

}








