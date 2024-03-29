package com.cegeka.horizon.camis.sync_timesheet_legacy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.cegeka.horizon.camis.*")
public class SyncTimesheetAppLegacy {

   @Autowired
    private OperationExecutor executor;

    public static void main(String[] args) {
        new SpringApplicationBuilder(SyncTimesheetAppLegacy.class).web(WebApplicationType.NONE).run(args);
    }

    @Bean
    public CommandLineRunner runOperation(ApplicationContext ctx) {
        return args -> executor.run();
    }
}
