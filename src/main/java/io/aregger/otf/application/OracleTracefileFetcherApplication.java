package io.aregger.otf.application;

import io.aregger.otf.service.ConnectionIdentifier;
import io.aregger.otf.service.TracefileService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.invoke.MethodHandles;

@SpringBootApplication
public class OracleTracefileFetcherApplication implements ApplicationRunner {

    private static final Logger log = LogManager.getLogger(MethodHandles.lookup().lookupClass());

    public static void main(String[] args) {
        SpringApplication.run(OracleTracefileFetcherApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if(!args.containsOption("file") || !args.containsOption("url")) {
            log.error("Specify --url and --file");
            log.error("E.g. --url=jdbc:oracle:thin:c##dbzuser/dbz@localhost:1521/ORCLCDB --file=ORCLCDB_mz00_1444.trc");
            return;
        }
        String url = args.getOptionValues("url").get(0);
        String file = args.getOptionValues("file").get(0);

        tracefileService().connect(new ConnectionIdentifier(url));
        tracefileService().fetchTracefile(file);
    }

    @Bean
    TracefileService tracefileService() {
        return new TracefileService();
    }
}
