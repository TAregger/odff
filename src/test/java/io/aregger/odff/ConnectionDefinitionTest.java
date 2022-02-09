package io.aregger.odff;

import io.aregger.odff.ConnectionDefinition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionDefinitionTest {

    @Test
    void validate() {
        // Arrange, Act, Assert
        assertThat(new ConnectionDefinition("name", "tnsString", "username", "password").validate()).hasSize(0);
        assertThat(new ConnectionDefinition("name", null, "username", "password").validate()).hasSize(1);
        assertThat(new ConnectionDefinition(null, null, null, null).validate()).hasSize(4);
    }

    @Test
    void buildJdbcConnectionString() {
        // Arrange
        var c = new ConnectionDefinition("name", "tnsString", "username", "password");
        // Act, Assert
        assertThat(c.buildJdbcConnectionString()).isEqualTo("jdbc:oracle:thin:username/password@tnsString");
    }

}