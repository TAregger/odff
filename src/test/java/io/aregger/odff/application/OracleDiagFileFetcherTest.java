package io.aregger.odff.application;

import io.aregger.odff.service.TracefileService;
import io.aregger.odff.service.TracefileServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

class OracleDiagFileFetcherTest {

    private TracefileService tracefileService;
    private OracleDiagFileFetcher fetcher;

    @BeforeEach
    void setUp() {
        this.tracefileService = mock(TracefileService.class);
        this.fetcher = new OracleDiagFileFetcher(tracefileService, mock(Path.class));
    }

    @Test
    void testFetchAlertLog() {
        // Arrange
        String[] args = new String[]{"--url=url", "--alertlog"};

        // Act
        int exitCode = new CommandLine(fetcher).execute(args);

        // Assert
        assertThat(exitCode).isEqualTo(0);
        verify(tracefileService).initialize(any(), any());
        verify(tracefileService).fetchAlertLog();
        verifyNoMoreInteractions(tracefileService);
    }

    @Test
    void testFetchTracefile() {
        // Arrange
        String[] args = new String[]{"--url=url", "--tracefileName=myFile.trc"};

        // Act
        int exitCode = new CommandLine(fetcher).execute(args);

        // Assert
        assertThat(exitCode).isEqualTo(0);
        verify(tracefileService).initialize(any(), any());
        verify(tracefileService).fetchTracefile("myFile.trc");
        verifyNoMoreInteractions(tracefileService);
    }

    @Test
    void testFetchTracefileNoFilenameProvided() {
        // Arrange
        String[] args = new String[]{"--url=url", "--tracefileName"};

        // Act
        int exitCode = new CommandLine(fetcher).execute(args);

        // Assert
        assertThat(exitCode).isEqualTo(2);
        verifyNoInteractions(tracefileService);
    }

    @Test
    void testMissingUrl() {
        // Arrange
        String[] args = new String[]{"--alertlog"};

        // Act
        int exitCode = new CommandLine(fetcher).execute(args);

        // Assert
        assertThat(exitCode).isEqualTo(2);
        verifyNoInteractions(tracefileService);
    }

    @Test
    void testMissingTracefileType() {
        // Arrange
        String[] args = new String[]{"--url=url"};

        // Act
        int exitCode = new CommandLine(fetcher).execute(args);

        // Assert
        assertThat(exitCode).isEqualTo(2);
        verifyNoInteractions(tracefileService);
    }

    @Test
    void testFileExists() {
        // Arrange
        String[] args = new String[]{"--url=url", "--alertlog"};
        doThrow(TracefileServiceException.class).when(tracefileService).fetchAlertLog();

        // Act
        int exitCode = new CommandLine(fetcher).execute(args);

        // Assert
        assertThat(exitCode).isEqualTo(1);
        verify(tracefileService).initialize(any(), any());
        verify(tracefileService).fetchAlertLog();
        verifyNoMoreInteractions(tracefileService);
    }

}