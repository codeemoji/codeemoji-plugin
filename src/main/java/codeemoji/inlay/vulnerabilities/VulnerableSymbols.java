package codeemoji.inlay.vulnerabilities;

import codeemoji.core.util.CESymbol;

public enum VulnerableSymbols {

    ;
    public static final CESymbol VULNERABLE_LOW = new CESymbol(0x1F9FF);
    public static final CESymbol VULNERABLE_MEDIUM = new CESymbol(0x1FFFF);
    public static final CESymbol VULNERABLE_HIGH = new CESymbol(0xFF9FF);
}
