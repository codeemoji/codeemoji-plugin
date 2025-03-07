package codeemoji.core.collector.project;

import codeemoji.core.config.CERuleFeature;
import codeemoji.core.util.CESymbol;
import org.jetbrains.annotations.NotNull;

public final class ProjectRuleSymbol {

    public static final CESymbol ANNOTATIONS_SYMBOL = CESymbol.of(0x1F4DD); //memo
    public static final CESymbol EXTENDS_SYMBOL = CESymbol.of(0x1F517); //link
    public static final CESymbol IMPLEMENTS_SYMBOL = CESymbol.of(0x1F91D); //handshake
    public static final CESymbol TYPES_SYMBOL = CESymbol.of(0x1F4D1); //bookmark
    public static final CESymbol RETURNS_SYMBOL = CESymbol.of(0x21A9); //right arrow curving left
    public static final CESymbol PACKAGES_SYMBOL = CESymbol.of(0x1F4E6); //package


    private ProjectRuleSymbol() {
    }

    public static @NotNull CESymbol detectDefaultSymbol(@NotNull CERuleFeature feature) {
        switch (feature) {
            case ANNOTATIONS -> {
                return ANNOTATIONS_SYMBOL;
            }
            case EXTENDS -> {
                return EXTENDS_SYMBOL;
            }
            case IMPLEMENTS -> {
                return IMPLEMENTS_SYMBOL;
            }
            case RETURNS -> {
                return RETURNS_SYMBOL;
            }
            case TYPES -> {
                return TYPES_SYMBOL;
            }
            case PACKAGES -> {
                return PACKAGES_SYMBOL;
            }
            default -> {
                return CESymbol.empty();
            }
        }
    }

}
