package codeemoji.core.provider;

class CEProviderException extends RuntimeException {

    CEProviderException() {
        super("Settings must be 'CEDefaultSettings' or 'PersistentStateComponent' type.");
    }
}
