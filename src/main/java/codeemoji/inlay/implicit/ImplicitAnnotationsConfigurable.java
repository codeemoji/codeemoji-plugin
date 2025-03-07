package codeemoji.inlay.implicit;

import codeemoji.core.settings.CEConfigurableWindow;
import codeemoji.core.util.CEUtils;
import com.intellij.codeInsight.hints.ChangeListener;
import com.intellij.codeInsight.hints.ImmediateConfigurable;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@SuppressWarnings({"DuplicatedCode"})
class ImplicitAnnotationsConfigurable extends CEConfigurableWindow<ImplicitAnnotationsSettings> {

    @Override
    public @NotNull JComponent createComponent(ImplicitAnnotationsSettings settings, @Nullable String preview, Project project, Language language, ChangeListener changeListener) {
        
        var implicitPanel = new JPanel();

        var jpaAnnotations = CEUtils.createBasicInnerPanel("inlay.implicitannotations.options.title.jpa", 10, 1);
        var springAnnotations = CEUtils.createBasicInnerPanel("inlay.implicitannotations.options.title.spring", 10, 1);

        var jpaEntity = new JLabel("@Entity");
        var jpaEmbeddable = new JLabel("@Embeddable");
        var jpaTable = new JLabel("@Table");
        var jpaBasic = new JLabel("@Basic");
        var jpaColumn = new JLabel("@Column");
        var jpaUniqueConstraint = new JLabel("@UniqueConstraint");

        implicitPanel.add(jpaEntity);
        implicitPanel.add(jpaEmbeddable);
        implicitPanel.add(jpaTable);
        implicitPanel.add(jpaBasic);
        implicitPanel.add(jpaColumn);
        implicitPanel.add(jpaUniqueConstraint);

        var springConfiguration = new JLabel("@Configuration");
        var springController = new JLabel("@Controller");
        var springRestController = new JLabel("@RestController");
        var springBean = new JLabel("@Bean");
        var springRequestMapping = new JLabel("@RequestMapping");
        var springGetMapping = new JLabel("@GetMapping");
        var springPostMapping = new JLabel("@PostMapping");
        var springDeleteMapping = new JLabel("@DeleteMapping");
        var springPutMapping = new JLabel("@PutMapping");
        var springPatchMapping = new JLabel("@PatchMapping");

        jpaAnnotations.add(jpaEntity);
        jpaAnnotations.add(jpaEmbeddable);
        jpaAnnotations.add(jpaTable);
        jpaAnnotations.add(jpaBasic);
        jpaAnnotations.add(jpaColumn);
        jpaAnnotations.add(jpaUniqueConstraint);

        springAnnotations.add(springConfiguration);
        springAnnotations.add(springController);
        springAnnotations.add(springRestController);
        springAnnotations.add(springBean);
        springAnnotations.add(springRequestMapping);
        springAnnotations.add(springGetMapping);
        springAnnotations.add(springPostMapping);
        springAnnotations.add(springDeleteMapping);
        springAnnotations.add(springPutMapping);
        springAnnotations.add(springPatchMapping);

        implicitPanel.add(jpaAnnotations);
        implicitPanel.add(springAnnotations);

        return implicitPanel;
    }
}
