package codeemoji.inlay.vcs;

import codeemoji.core.util.CESymbol;

public enum VCSSymbols {
    ;

    public static final CESymbol LAST_COMMIT = CESymbol.of(0x1F58B);
    public static final CESymbol RECENTLY_MODIFIED = CESymbol.of(0x2712);
    public static final CESymbol FREQUENTLY_MODIFIED = CESymbol.of(0x1F525);
    public static final CESymbol NAME_CHANGED = CESymbol.of(0x1F3F7);
    public static final CESymbol TOO_MANY_OWNERS = CESymbol.of(0x1F468, 0x1F469, 0x1F467, 0x1F466);
    public static final CESymbol FIXES_BUG = CESymbol.of(0x1FAB2);
    public static final CESymbol NEWLY_ADDED = CESymbol.of(0x1F476);
    public static final CESymbol UN_FREQUENTLY_MODIFIED = CESymbol.of(0x1F996);
}
