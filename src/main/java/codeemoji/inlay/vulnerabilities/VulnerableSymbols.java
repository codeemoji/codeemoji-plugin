package codeemoji.inlay.vulnerabilities;

import codeemoji.core.util.CESymbol;

public enum VulnerableSymbols {

    ;
    public static final CESymbol VULNERABLE_NOT_VULNERABLE = new CESymbol(0xFFFFF);
    public static final CESymbol VULNERABLE_LOW = new CESymbol(0x1F41E); // ghost
    public static final CESymbol VULNERABLE_MEDIUM = new CESymbol(0x26A0); //
    public static final CESymbol VULNERABLE_HIGH = new CESymbol(0x26D4); // unicorn
    public static final CESymbol VULNERABLE_CRITICAL = new CESymbol(0xFFFFF); // unicorn
}
