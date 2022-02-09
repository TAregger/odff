package io.aregger.odff;

import java.util.function.Function;

class PasswordReader {

    private final Function<String, char[]> reader;

    public PasswordReader() {
        this.reader = prompt -> System.console().readPassword(prompt);
    }

    // @VisibleForTesting
    PasswordReader(Function<String, char[]> reader) {
        this.reader = reader;
    }

    /**
     * Returns the password read from the console.
     *
     * @return the password; or null when the user provides an empty password or enters control-D on Unix or control-Z on Windows
     */
    public String readPassword() {
        char[] pass = this.reader.apply("Enter password: ");
        return pass == null || pass.length == 0 ? null : new String(pass);
    }

}
