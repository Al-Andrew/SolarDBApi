package com.solardb;

import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Writes coarse startup milestones to stdout/stderr so container logs show progress even when
 * log shipping is lossy or the process dies before much of Logback's pipeline runs.
 */
final class StartupDiagnosticsListener implements ApplicationListener<ApplicationEvent> {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationFailedEvent failed) {
            Throwable ex = failed.getException();
            System.err.println("[solardb-api] Startup FAILED: " + ex);
            ex.printStackTrace(System.err);
            System.err.flush();
            return;
        }

        if (event instanceof ApplicationEnvironmentPreparedEvent
                || event instanceof ApplicationContextInitializedEvent
                || event instanceof ApplicationPreparedEvent
                || event instanceof ApplicationStartedEvent
                || event instanceof ApplicationReadyEvent) {
            System.out.println("[solardb-api] Startup phase: " + event.getClass().getSimpleName());
            System.out.flush();
        }
    }
}
