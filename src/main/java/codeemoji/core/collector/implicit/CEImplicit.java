package codeemoji.core.collector.implicit;

import codeemoji.core.util.CESymbol;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Data
public class CEImplicit {

    private final String baseName;
    private final List<CEImplicitAnnotation> forFields = new ArrayList<>();
    private final List<CEImplicitAnnotation> forMethods = new ArrayList<>();

    public void addAnnotationForField(@NotNull String annotationName, @NotNull CESymbol annotationSymbol, @NotNull String defaultAttribute) {
        processList(annotationName, annotationSymbol, defaultAttribute, forFields);
    }

    public void addAnnotationForMethod(@NotNull String annotationName, @NotNull CESymbol annotationSymbol, @NotNull String defaultAttribute) {
        processList(annotationName, annotationSymbol, defaultAttribute, forMethods);
    }

    private void processList(@NotNull String annotationName, @NotNull CESymbol annotationSymbol, @NotNull String defaultAttribute,
                             @NotNull List<CEImplicitAnnotation> listForProcess) {
        var hasAnnotation = false;
        for (var ia : listForProcess) {
            if (ia.getName().equalsIgnoreCase(annotationName)) {
                var hasAttribute = false;
                for (String attr : ia.getDefaultAttributes()) {
                    if (attr.equalsIgnoreCase(defaultAttribute)) {
                        hasAttribute = true;
                        break;
                    }
                }
                if (!hasAttribute) {
                    ia.getDefaultAttributes().add(defaultAttribute);
                }
                hasAnnotation = true;
                break;
            }
        }
        if (!hasAnnotation) {
            var ia = new CEImplicitAnnotation(annotationName, annotationSymbol);
            ia.addAttribute(defaultAttribute);
            listForProcess.add(ia);
        }
    }

    @Data
    public static class CEImplicitAnnotation {
        private final String name;
        private final CESymbol symbol;
        List<String> defaultAttributes = new ArrayList<>();

        public void addAttribute(String attribute) {
            getDefaultAttributes().add(attribute);
        }
    }
}
