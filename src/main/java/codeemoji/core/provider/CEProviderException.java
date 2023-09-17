package codeemoji.core.provider;

class CEProviderException extends RuntimeException {

    CEProviderException() {
        super("Settings must be 'NoSettings' or 'PersistentStateComponent' type.");
    }
}
