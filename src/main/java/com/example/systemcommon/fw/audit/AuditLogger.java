package com.example.systemcommon.fw.audit;

import com.example.systemcommon.fw.job.JobExitCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {

    private static final Logger AUDIT_LOG = LoggerFactory.getLogger("AUDIT");

    public void jobStarted(String jobName) {
        AUDIT_LOG.info("event=job.started jobName={}", jobName);
    }

    public void jobSucceeded(String jobName, String message) {
        AUDIT_LOG.info("event=job.succeeded jobName={} message={}", jobName, message);
    }

    public void jobFailed(String jobName, JobExitCode exitCode, String message) {
        AUDIT_LOG.warn("event=job.failed jobName={} exitCode={} message={}", jobName, exitCode, message);
    }

    public void jobFailedByException(String jobName, Throwable throwable) {
        AUDIT_LOG.warn(
                "event=job.failed jobName={} reason=exception exceptionType={} message={}",
                jobName,
                throwable.getClass().getName(),
                throwable.getMessage());
    }
}
