package codeemoji.inlay.vulnerabilities;

import codeemoji.core.util.CESymbol;

public enum VulnerableDependencySymbols {

    ;
    public static final CESymbol VULNERABLE_METHOD = CESymbol.of(0x26A0); // attention
    public static final CESymbol INDIRECT_VULNERABLE_METHOD = CESymbol.of(0x26D4); // stop
    public static final CESymbol VULNERABLE_DEPENDENCY_CALL = CESymbol.of(0x1F480); // skull
}
