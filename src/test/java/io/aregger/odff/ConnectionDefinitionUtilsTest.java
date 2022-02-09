package io.aregger.odff;

import io.aregger.odff.ConnectionDefinition;
import io.aregger.odff.ConnectionDefinitionUtils;
import io.aregger.odff.InvalidConnectionDefinitionFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        Optional<ConnectionDefinition> connectionDefinition = ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "ocdb1");

        // Assert
        assertThat(connectionDefinition).isPresent();
        assertThat(connectionDefinition.get().name()).isEqualTo("OCDB1");
        assertThat(connectionDefinition.get().validate()).isEmpty();

    }

    @Test
    void getValidConnectionDefinitionFromFileNotValid() {
        // Act, Assert
        assertThatThrownBy(() -> ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "OCDB2"))
            .isInstanceOf(InvalidConnectionDefinitionFileException.class);
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
        // Act, Assert
        assertThatThrownBy(() -> ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "OCDB3"))
            .isInstanceOf(InvalidConnectionDefinitionFileException.class);
    }

    @Test
    void getValidConnectionDefinitionFromFileInvalidJsonFile() throws FileNotFoundException {
        // Arrange
        this.file = ResourceUtils.getFile(this.getClass().getResource("/connections-invalid-json.json"));
        assertThat(this.file).isNotEmpty();

        // Act, Assert
        assertThatThrownBy(() -> ConnectionDefinitionUtils.getValidConnectionDefinitionFromFile(this.file, "OCDB3"))
            .isInstanceOf(InvalidConnectionDefinitionFileException.class);
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