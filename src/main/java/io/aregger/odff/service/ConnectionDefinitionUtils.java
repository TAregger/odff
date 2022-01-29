package io.aregger.odff.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public final class ConnectionDefinitionUtils {

    private ConnectionDefinitionUtils() {
        throw new AssertionError("non-instantiable class");
    }

    public static Optional<ConnectionDefinition> getValidConnectionDefinitionFromFile(File file, String name, String password) {
        return ConnectionFileReader.getConnectionFromFile(file, name)
            .map(c -> new ConnectionDefinition(c.name(), c.tnsString(), c.username(), password))
            .map(ConnectionDefinitionUtils::validated);
    }

    public static Optional<ConnectionDefinition> getValidConnectionDefinitionFromFile(File file, String name) {
        return ConnectionFileReader.getConnectionFromFile(file, name)
            .map(ConnectionDefinitionUtils::validated);
    }

    private static ConnectionDefinition validated(ConnectionDefinition connectionDefinition) {
        List<String> validationErrors = connectionDefinition.validate();
        if (!validationErrors.isEmpty()) {
            throw new InvalidConnectionDefinitionFileException(
                String.format("Connection with name %s has the following errors: ", connectionDefinition.name()) + String.join(", ", validationErrors) + '.');
        }
        return connectionDefinition;
    }

    private static class ConnectionFileReader {
        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        private static Optional<ConnectionDefinition> getConnectionFromFile(File file, String name) {
            requireNonNull(name, "'name' must not be null");
            List<ConnectionDefinition> connectionDefinitions = read(file);
            if (connectionDefinitions == null) {
                return Optional.empty();
            }
            List<ConnectionDefinition> connectionDefinitionsForName = connectionDefinitions.stream().filter(c -> name.equalsIgnoreCase(c.name())).toList();
            if (connectionDefinitionsForName.size() == 1) {
                return Optional.of(connectionDefinitionsForName.get(0));
            } else if (connectionDefinitionsForName.size() == 0) {
                return Optional.empty();
            } else {
                throw new InvalidConnectionDefinitionFileException("More than 1 connection with name " + name + " found in file " + file.getAbsoluteFile() + ".");
            }
        }

        private static List<ConnectionDefinition> read(File file) {
            try {
                return OBJECT_MAPPER.readValue(file, new TypeReference<>() {});
            } catch (JsonParseException e) {
                throw new InvalidConnectionDefinitionFileException("Error reading connections file. " + e.getMessage(), e);
            } catch (IOException e) {
                throw new UncheckedIOException("Error reading connections file. + " + e.getMessage(), e);
            }

        }

    }


}

