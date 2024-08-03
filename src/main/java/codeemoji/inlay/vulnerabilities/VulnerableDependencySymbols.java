package codeemoji.inlay.vulnerabilities;

import codeemoji.core.util.CESymbol;

public enum VulnerableDependencySymbols {

    ;
    public static final CESymbol VULNERABLE_NOT_VULNERABLE = new CESymbol(0xFFFFF);
    public static final CESymbol VULNERABLE_LOW = new CESymbol(0x1F41E); // bug
    public static final CESymbol VULNERABLE_MEDIUM = new CESymbol(0x26A0); // attention
    public static final CESymbol VULNERABLE_HIGH = new CESymbol(0x26D4); // stop
    public static final CESymbol VULNERABLE_CRITICAL = new CESymbol(0x1F480); // skull
}
