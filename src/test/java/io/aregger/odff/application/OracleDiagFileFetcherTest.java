package io.aregger.odff.application;

import io.aregger.odff.service.TracefileService;
import io.aregger.odff.service.TracefileServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
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
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(0);
        verify(tracefileService).initialize(any(), any());
        verify(tracefileService).fetchAlertLog();
        verifyNoMoreInteractions(tracefileService);
    }

    @Test
    void testFetchTracefile() {
        // Arrange
        String[] args = new String[]{"--url=url", "--tracefile=myFile.trc"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(0);
        verify(tracefileService).initialize(any(), any());
        verify(tracefileService).fetchTracefile("myFile.trc");
        verifyNoMoreInteractions(tracefileService);
    }

    @Test
    void testFetchTracefileNoFilenameProvided() {
        // Arrange
        String[] args = new String[]{"--url=url", "--tracefile"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(2);
        verifyNoInteractions(tracefileService);
    }

    @Test
    void testMissingUrl() {
        // Arrange
        String[] args = new String[]{"--alertlog"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(2);
        verifyNoInteractions(tracefileService);
    }

    @Test
    void testMissingTracefileType() {
        // Arrange
        String[] args = new String[]{"--url=url"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

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
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(1);
        verify(tracefileService).initialize(any(), any());
        verify(tracefileService).fetchAlertLog();
        verifyNoMoreInteractions(tracefileService);
    }

    @Test
    void testUrlAndConnectionName() {
        // Arrange
        String[] args = new String[]{"--url=url", "--name=name", "--alertlog"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(2);
        verifyNoInteractions(tracefileService);
    }

    @Test
    void testConnectionNameWithDefaultFileLocation() throws IOException {
        // Arrange
        String[] args = new String[]{"--name=OCDB1", "--alertlog"};
        File connectionDefinitions = ResourceUtils.getFile(this.getClass().getResource("/connections.json"));
        this.fetcher = new OracleDiagFileFetcher(this.tracefileService, connectionDefinitions.getParentFile().toPath());

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(0);
        verify(tracefileService).initialize(any(), any());
        verify(tracefileService).fetchAlertLog();
        verifyNoMoreInteractions(tracefileService);
    }

    @Test
    void testConnectionNameNotExistent() throws IOException {
        // Arrange
        String[] args = new String[]{"--name=OCDBx", "--alertlog"};
        File connectionDefinitions = ResourceUtils.getFile(this.getClass().getResource("/connections.json"));
        this.fetcher = new OracleDiagFileFetcher(this.tracefileService, connectionDefinitions.getParentFile().toPath());

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(1);
        verifyNoMoreInteractions(tracefileService);
    }

    @Test
    void testConnectionNameWithGivenInvalidFile() throws IOException {
        // Arrange
        File connectionDefinitions = ResourceUtils.getFile(this.getClass().getResource("/connections-invalid-json.json"));
        String[] args = new String[]{"--name=OCDB1", "--connections=" + connectionDefinitions, "--alertlog"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(1);
        verifyNoInteractions(tracefileService);
    }

}