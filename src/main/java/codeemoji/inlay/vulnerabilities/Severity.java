package codeemoji.inlay.vulnerabilities;

public enum Severity {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW,
    UNKNOWN;

    public static Severity parse(String score) {
        try {
            return Severity.valueOf(score.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Severity.UNKNOWN;
        }
    }
}