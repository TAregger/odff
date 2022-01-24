package io.aregger.odff.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class ConnectionDefinitionUtils {

    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    public ConnectionDefinitionUtils() {
        throw new AssertionError("non-instantiable class");
    }

    public static Optional<ConnectionDefinition> getValidConnectionDefinitionFromFile(File file, String name, String password) {
        return ConnectionFileReader.getConnectionFromFile(file, name).map(c -> {
            var connectionWithProvidedPassword = new ConnectionDefinition(c.name(), c.tnsString(), c.username(), password);
            boolean valid = ConnectionDefinitionUtils.validate(connectionWithProvidedPassword);
            if (!valid) {
                return null;
            } else {
                return connectionWithProvidedPassword;
            }
        });
    }

    public static Optional<ConnectionDefinition> getValidConnectionDefinitionFromFile(File file, String name) {
        return ConnectionFileReader.getConnectionFromFile(file, name).map(c -> {
            boolean valid = ConnectionDefinitionUtils.validate(c);
            if (!valid) {
                return null;
            } else {
                return c;
            }
        });
    }

    private static boolean validate(ConnectionDefinition connectionDefinition) {
        boolean result = true;
        List<String> validationErrors = connectionDefinition.validate();
        if (!validationErrors.isEmpty()) {
            logError(connectionDefinition.name(), validationErrors);
            result = false;
        }
        return result;
    }

    private static void logError(String name, List<String> validationErrors) {
        String logMessage = String.format("Connection with name %s has the following errors: ", name)
                            + String.join(", ", validationErrors) + '.';
        log.error(logMessage);
    }

    private static class ConnectionFileReader {
        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        private static Optional<ConnectionDefinition> getConnectionFromFile(File file, String name) {
            requireNonNull(name, "'name' must not be null");
            List<ConnectionDefinition> connectionDefinitions = read(file);
            if (connectionDefinitions == null) {
                return Optional.empty();
            }
            List<ConnectionDefinition> connectionDefinitionsForName = connectionDefinitions.stream().filter(c -> name.equals(c.name())).toList();
            if (connectionDefinitionsForName.size() == 1) {
                return Optional.of(connectionDefinitionsForName.get(0));
            } else if (connectionDefinitionsForName.size() == 0) {
                log.error("Connection with name '{}' not found in file {}.", name, file.getAbsoluteFile().toString());
                return Optional.empty();
            } else {
                log.error("More than 1 connection with name '{}' found in file {}.", name, file.getAbsoluteFile().toString());
                return Optional.empty();
            }
        }

        private static List<ConnectionDefinition> read(File file) {
            try {
                return OBJECT_MAPPER.readValue(file, new TypeReference<>() {});
            } catch (JsonParseException e) {
                log.error("Error reading connections file. {}", e.getMessage());
                log.debug("Stacktrace:", e);
                return null;
            } catch (IOException e) {
                log.error("Error reading connections file. {}", e.getMessage());
                log.debug("Stacktrace:", e);
                return null;
            }

        }

    }


}

