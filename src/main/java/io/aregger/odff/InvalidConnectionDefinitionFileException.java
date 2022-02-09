package io.aregger.odff;

class InvalidConnectionDefinitionFileException extends RuntimeException {

    public InvalidConnectionDefinitionFileException(String message) {
        super(message);
    }

    public InvalidConnectionDefinitionFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
