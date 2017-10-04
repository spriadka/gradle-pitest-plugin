package info.solidsoft.gradle.pitest

class UnsupportedScmVersionException extends Exception {
    UnsupportedScmVersionException(String message) {
        super(message)
    }

    UnsupportedScmVersionException(String message, Throwable cause) {
        super(message, cause)
    }
}
