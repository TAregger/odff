package io.aregger.odff.service;

import java.util.function.Function;

public class PasswordReader {

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
     * @return the password; or null when the user enters control-D on Unix or control-Z on Windows
     */
    public String readPassword() {
        String password;
        String prompt = "Enter password: ";
        do {
            password = prompt(prompt);
            prompt = "Enter password (or hit Ctl-C): ";
        } while (isEmpty(password));
        return password;
    }

    private String prompt(String prompt) {
        char[] pass = this.reader.apply(prompt);
        return pass == null ? null : new String(pass);
    }

    private boolean isEmpty(String password) {
        return (password != null && password.trim().length() == 0);
    }

}
