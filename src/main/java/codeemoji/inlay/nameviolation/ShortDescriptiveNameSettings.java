package codeemoji.inlay.nameviolation;

import codeemoji.core.config.CEPSIType;
import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.SMALL_NAME;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "ShortDescriptiveNameSettings", storages = @Storage("codeemoji-short-descriptive-name-settings.xml"))
public class ShortDescriptiveNameSettings extends CEBaseSettings<ShortDescriptiveNameSettings> {

    public ShortDescriptiveNameSettings() {
        super(builder(), ShortDescriptiveName.class, SMALL_NAME);
    }

    private int numberOfLetters = 1;

}