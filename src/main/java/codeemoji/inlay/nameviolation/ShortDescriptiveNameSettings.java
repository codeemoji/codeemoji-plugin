package codeemoji.inlay.nameviolation;

import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import static codeemoji.inlay.nameviolation.NameViolationSymbols.CONFUSED;
import static codeemoji.inlay.nameviolation.NameViolationSymbols.SMALL_NAME;

@EqualsAndHashCode(callSuper = true)
@Data
@State(name = "ShortDescriptiveNameSettings", storages = @Storage("codeemoji-short-descriptive-name-settings.xml"))
public class ShortDescriptiveNameSettings extends CEBaseSettings<ShortDescriptiveNameSettings> {

    public ShortDescriptiveNameSettings() {
        super(ShortDescriptiveName.class, SMALL_NAME);
    }

    private int numberOfLetters = 1;

}