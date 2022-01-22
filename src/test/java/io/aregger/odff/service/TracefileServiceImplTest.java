package io.aregger.odff.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TracefileServiceImplTest {

    @Test
    void testNotInitialized() {
        // Arrange
        var tracefileService = new TracefileServiceImpl();

        // Act, Assert
        assertThrows(IllegalStateException.class, () -> tracefileService.fetchTracefile("myFile.trc"));
        assertThrows(IllegalStateException.class, tracefileService::fetchAlertLog);
    }

    @Test
    void testFetchAlertLog() throws IOException {
        // Arrange
        var tracefileService = new TracefileServiceImpl();
        var writer = mock(TracefileWriter.class);
        tracefileService.initialize(writer, "url");

        // Act
        tracefileService.fetchAlertLog();

        // Assert
        verify(writer).writeFile(eq("alert.log"), any(DatabaseFileFetcher.class));
    }

    @ParameterizedTest
    @ValueSource(classes = {FileAlreadyExistsException.class, IOException.class})
    void testFetchAlertLogExceptional(Class<? extends Throwable> exception) throws IOException {
        // Arrange
        var tracefileService = new TracefileServiceImpl();
        var writer = mock(TracefileWriter.class);
        tracefileService.initialize(writer, "url");
        doThrow(exception).when(writer).writeFile(anyString(), any(DatabaseFileFetcher.class));

        // Act, Assert
        assertThrows(TracefileServiceException.class, tracefileService::fetchAlertLog);
    }

    @Test
    void testFetchTracefile() throws IOException {
        // Arrange
        var tracefileService = new TracefileServiceImpl();
        var writer = mock(TracefileWriter.class);
        tracefileService.initialize(writer, "url");

        // Act
        tracefileService.fetchTracefile("myFile.trc");

        // Assert
        verify(writer).writeFile(eq("myFile.trc"), any(DatabaseFileFetcher.class));
    }
}