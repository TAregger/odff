package io.aregger.otf.application;

import io.aregger.otf.service.ConnectionIdentifier;
import io.aregger.otf.service.TracefileService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OracleTracefileFetcherApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(OracleTracefileFetcherApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        tracefileService().connect(new ConnectionIdentifier("jdbc:oracle:thin:c##dbzuser/dbz@localhost:1521/ORCLCDB"));
        tracefileService().fetchTracefile("ORCLCDB_mz00_1444.trc");
    }

    @Bean
    TracefileService tracefileService() {
        return new TracefileService();
    }
}
