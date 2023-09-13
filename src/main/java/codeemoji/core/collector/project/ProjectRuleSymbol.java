package codeemoji.core.collector.project;

import codeemoji.core.collector.config.CERuleFeature;
import codeemoji.core.util.CESymbol;
import org.jetbrains.annotations.NotNull;

public final class ProjectRuleSymbol {

    public static final CESymbol ANNOTATIONS_SYMBOL = new CESymbol(0x1F4DD); //memo
    public static final CESymbol EXTENDS_SYMBOL = new CESymbol(0x1F517); //link
    public static final CESymbol IMPLEMENTS_SYMBOL = new CESymbol(0x1F91D); //handshake
    public static final CESymbol TYPES_SYMBOL = new CESymbol(0x1F4D1); //bookmark
    public static final CESymbol RETURNS_SYMBOL = new CESymbol(0x21A9); //right arrow curving left

    private ProjectRuleSymbol() {
    }

    public static CESymbol detectDefaultSymbol(@NotNull CERuleFeature feature) {
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
            default -> {
                return new CESymbol();
            }
        }
    }

}
