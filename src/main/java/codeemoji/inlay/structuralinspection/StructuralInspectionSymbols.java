package codeemoji.inlay.structuralinspection;

import codeemoji.core.util.CESymbol;

public enum StructuralInspectionSymbols {

    ;
    public static final CESymbol PURE_ACCESSOR = new CESymbol(0x1F1EC);
    public static final CESymbol PURE_MUTATOR = new CESymbol(0x1F1F8);
    public static final CESymbol STATE_INDEPENDENT_METHOD = new CESymbol(0x1F5FF);
    public static final CESymbol STATE_CHANGING_METHOD = new CESymbol(0x1F3AD);
    public static final CESymbol SIDE_EFFECTED_METHOD = new CESymbol(0x1F39B);
}
