package codeemoji.inlay.implicit;

import codeemoji.core.settings.CEBaseSettings;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
@Data
@State(name = "ImplicitAnnotationsSettings", storages = @Storage("codeemoji-implicit-annotations-settings.xml"))
public class ImplicitAnnotationsSettings extends CEBaseSettings<ImplicitAnnotationsSettings> {

}