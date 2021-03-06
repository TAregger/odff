package io.aregger.odff;

import io.aregger.odff.OracleDiagFileFetcher;
import io.aregger.odff.PasswordReader;
import io.aregger.odff.TracefileService;
import io.aregger.odff.TracefileServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class OracleDiagFileFetcherTest {

    private TracefileService tracefileService;
    private OracleDiagFileFetcher fetcher;
    private PasswordReader passwordReader;

    @BeforeEach
    void setUp() {
        this.tracefileService = mock(TracefileService.class);
        this.passwordReader = mock(PasswordReader.class);
        this.fetcher = new OracleDiagFileFetcher(this.tracefileService, this.passwordReader, mock(Path.class));
    }

    @Test
    void testFetchAlertLog() {
        // Arrange
        String[] args = new String[]{"--url=url", "--alertlog"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(0);
        verify(this.tracefileService).initialize(any(), any());
        verify(this.tracefileService).fetchAlertLog();
        verifyNoMoreInteractions(this.tracefileService);
    }

    @Test
    void testFetchTracefile() {
        // Arrange
        String[] args = new String[]{"--url=url", "--tracefile=myFile.trc"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(0);
        verify(this.tracefileService).initialize(any(), any());
        verify(this.tracefileService).fetchTracefile("myFile.trc");
        verifyNoMoreInteractions(this.tracefileService);
    }

    @Test
    void testFetchTracefileNoFilenameProvided() {
        // Arrange
        String[] args = new String[]{"--url=url", "--tracefile"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(2);
        verifyNoInteractions(this.tracefileService);
    }

    @Test
    void testMissingUrl() {
        // Arrange
        String[] args = new String[]{"--alertlog"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(2);
        verifyNoInteractions(this.tracefileService);
    }

    @Test
    void testMissingTracefileType() {
        // Arrange
        String[] args = new String[]{"--url=url"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(2);
        verifyNoInteractions(this.tracefileService);
    }

    @Test
    void testFileExists() {
        // Arrange
        String[] args = new String[]{"--url=url", "--alertlog"};
        doThrow(TracefileServiceException.class).when(this.tracefileService).fetchAlertLog();

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(1);
        verify(this.tracefileService).initialize(any(), any());
        verify(this.tracefileService).fetchAlertLog();
        verifyNoMoreInteractions(this.tracefileService);
    }

    @Test
    void testUrlAndConnectionName() {
        // Arrange
        String[] args = new String[]{"--url=url", "--name=name", "--alertlog"};

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(2);
        verifyNoInteractions(this.tracefileService);
    }

    @Test
    void testConnectionNameWithDefaultFileLocation() throws IOException {
        // Arrange
        String[] args = new String[]{"--name=OCDB1", "--alertlog"};
        File connectionDefinitions = ResourceUtils.getFile(this.getClass().getResource("/connections.json"));
        this.fetcher = new OracleDiagFileFetcher(this.tracefileService, this.passwordReader, connectionDefinitions.getParentFile().toPath());

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(0);
        verify(this.tracefileService).initialize(any(), any());
        verify(this.tracefileService).fetchAlertLog();
        verifyNoMoreInteractions(this.tracefileService);
    }

    @Test
    void testConnectionNameNotExistent() throws IOException {
        // Arrange
        String[] args = new String[]{"--name=OCDBx", "--alertlog"};
        File connectionDefinitions = ResourceUtils.getFile(this.getClass().getResource("/connections.json"));
        this.fetcher = new OracleDiagFileFetcher(this.tracefileService, this.passwordReader, connectionDefinitions.getParentFile().toPath());

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(1);
        verifyNoMoreInteractions(this.tracefileService);
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
        verifyNoInteractions(this.tracefileService);
    }

    @Test
    void testConnectionNameWithPasswordAsArgument() throws IOException {
        // Arrange
        String[] args = new String[]{"--name=OCDB1", "--password=secret", "--alertlog"};
        File connectionDefinitions = ResourceUtils.getFile(this.getClass().getResource("/connections.json"));
        this.fetcher = new OracleDiagFileFetcher(this.tracefileService, this.passwordReader, connectionDefinitions.getParentFile().toPath());

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(0);
        verify(this.tracefileService).initialize(any(), eq("jdbc:oracle:thin:scott/secret@localhost:1521/ORCLCDB"));
        verify(this.tracefileService).fetchAlertLog();
        verifyNoMoreInteractions(this.tracefileService);
        verifyNoInteractions(this.passwordReader);
    }

    @Test
    void testConnectionNameWithPasswordOnPrompt() throws IOException {
        // Arrange
        String[] args = new String[]{"--name=OCDB1", "--password", "--alertlog"};
        File connectionDefinitions = ResourceUtils.getFile(this.getClass().getResource("/connections.json"));
        this.fetcher = new OracleDiagFileFetcher(this.tracefileService, this.passwordReader, connectionDefinitions.getParentFile().toPath());
        when(this.passwordReader.readPassword()).thenReturn("secret");

        // Act
        int exitCode = OracleDiagFileFetcher.main(args, this.fetcher);

        // Assert
        assertThat(exitCode).isEqualTo(0);
        verify(this.tracefileService).initialize(any(), eq("jdbc:oracle:thin:scott/secret@localhost:1521/ORCLCDB"));
        verify(this.tracefileService).fetchAlertLog();
        verifyNoMoreInteractions(this.tracefileService);
        verify(this.passwordReader).readPassword();
    }

}