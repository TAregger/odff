package io.aregger.odff.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionDefinitionUtilsTest {

    private File file;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        this.file = ResourceUtils.getFile(this.getClass().getResource("/connections.json"));
        assertThat(this.file).isNotEmpty();
    }

    @Test
    void getValidConnectionDefinitionFromFile() {
        // Act
        Optional<ConnectionDefinition> connectionDefinition = ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "OCDB1");

        // Assert
        assertThat(connectionDefinition).isPresent();
        assertThat(connectionDefinition.get().name()).isEqualTo("OCDB1");
        assertThat(connectionDefinition.get().validate()).isEmpty();

    }

    @Test
    void getValidConnectionDefinitionFromFileNotValid() {
        // Act
        Optional<ConnectionDefinition> connectionDefinition = ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "OCDB2");

        // Assert
        assertThat(connectionDefinition).isEmpty();
    }

    @Test
    void getValidConnectionDefinitionFromNotFound() {
        // Act
        Optional<ConnectionDefinition> connectionDefinition = ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "OCDBx");

        // Assert
        assertThat(connectionDefinition).isEmpty();
    }

    @Test
    void getValidConnectionDefinitionFromFileTooMany() {
        // Act
        Optional<ConnectionDefinition> connectionDefinition = ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "OCDB3");

        // Assert
        assertThat(connectionDefinition).isEmpty();
    }

    @Test
    void getValidConnectionDefinitionFromFileInvalidJsonFile() throws FileNotFoundException {
        // Arrange
        this.file = ResourceUtils.getFile(this.getClass().getResource("/connections-invalid-json.json"));
        assertThat(this.file).isNotEmpty();

        // Act
        Optional<ConnectionDefinition> connectionDefinition = ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "OCDB3");

        // Assert
        assertThat(connectionDefinition).isEmpty();
    }

    @Test
    void getValidConnectionDefinitionFromFilePasswordOverwrite() {
        // Act
        Optional<ConnectionDefinition> connectionDefinition = ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "OCDB1", "newpass");

        // Assert
        assertThat(connectionDefinition).isPresent();
        assertThat(connectionDefinition.get().name()).isEqualTo("OCDB1");
        assertThat(connectionDefinition.get().password()).isEqualTo("newpass");
    }

    @Test
    void getValidConnectionDefinitionFromFileWithPassword() {
        // Act
        Optional<ConnectionDefinition> connectionDefinition = ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "OCDB4", "newpass");

        // Assert
        assertThat(connectionDefinition).isPresent();
        assertThat(connectionDefinition.get().name()).isEqualTo("OCDB4");
        assertThat(connectionDefinition.get().password()).isEqualTo("newpass");
        assertThat(connectionDefinition.get().validate()).isEmpty();
    }
}