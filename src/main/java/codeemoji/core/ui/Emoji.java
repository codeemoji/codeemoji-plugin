package codeemoji.core.ui;

public record Emoji(String symbol, String category, String description) {
    @Override
    public String toString() {
        return String.format("%1s %2s", symbol, description);
    }
}



