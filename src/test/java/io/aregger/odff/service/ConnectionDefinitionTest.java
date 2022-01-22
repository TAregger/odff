package io.aregger.odff.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectionDefinitionTest {

    @Test
    void validate() {
        // Arrange, Act, Assert
        assertThat(new ConnectionDefinition("alias", "tnsString", "username", "password").validate()).hasSize(0);
        assertThat(new ConnectionDefinition("alias", null, "username", "password").validate()).hasSize(1);
        assertThat(new ConnectionDefinition(null, null, null, null).validate()).hasSize(4);
    }

    @Test
    void buildJdbcConnectionString() {
        // Arrange
        var c = new ConnectionDefinition("alias", "tnsString", "username", "password");
        // Act, Assert
        assertThat(c.buildJdbcConnectionString()).isEqualTo("jdbc:oracle:thin:username/password@tnsString");
    }

}