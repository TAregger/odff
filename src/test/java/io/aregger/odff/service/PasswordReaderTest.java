package io.aregger.odff.service;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordReaderTest {

    @Test
    void readPassword() {
        // Arrange
        var reader = new PasswordReader(prompt -> "secret".toCharArray());

        // Act
        String password = reader.readPassword();

        // Assert
        assertThat(password).isEqualTo("secret");
    }

    @Test
    void readPasswordNull() {
        // Arrange
        var reader = new PasswordReader(prompt -> null);

        // Act
        String password = reader.readPassword();

        // Assert
        assertThat(password).isNull();
    }

    @Test
    void readPasswordEnteredOnThirdPrompt() {
        // Arrange
        var firstPrompt = new AtomicReference<String>();
        var lastPrompt = new AtomicReference<String>();
        var reader = new PasswordReader(mockReader(firstPrompt, lastPrompt));

        // Act
        String password = reader.readPassword();

        // Assert
        assertThat(password).isEqualTo("secret");
        assertThat(firstPrompt.get()).isEqualTo("Enter password: ");
        assertThat(lastPrompt.get()).isEqualTo("Enter password (or hit Ctl-C): ");
    }

    // A mock reader simulating that the first real password is entered only on the third prompt.
    // Also stores the first and last prompt in the given AtomicReferences for verification.
    private static Function<String, char[]> mockReader(AtomicReference<String> firstPrompt, AtomicReference<String> lastPrompt) {
        AtomicInteger promptCount = new AtomicInteger(0);
        return prompt -> {
            if (promptCount.getAndIncrement() == 0) {
                firstPrompt.set(prompt);
                return new char[]{};
            } else if (promptCount.getAndIncrement() == 1) {
                return " ".toCharArray();
            } else {
                lastPrompt.set(prompt);
                return "secret".toCharArray();
            }
        };
    }

}