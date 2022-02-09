package io.aregger.odff;

import io.aregger.odff.PasswordReader;
import org.junit.jupiter.api.Test;

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
    void readPasswordEmpty() {
        // Arrange
        var reader = new PasswordReader(prompt -> "".toCharArray());

        // Act
        String password = reader.readPassword();

        // Assert
        assertThat(password).isNull();
    }
}