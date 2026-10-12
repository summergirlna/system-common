package com.example.systemcommon.fw.audit;

import com.example.systemcommon.fw.job.Job;
import com.example.systemcommon.fw.job.JobExitCode;
import com.example.systemcommon.fw.job.JobResult;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class JobAuditAspect {

    private final AuditLogger auditLogger;

    @Around(
            "target(job) && execution(com.example.systemcommon.fw.job.JobResult com.example.systemcommon.fw.job.Job.execute())")
    public Object auditJobExecution(ProceedingJoinPoint joinPoint, Job job) throws Throwable {
        String jobName = job.name();

        auditLogger.jobStarted(jobName);

        try {
            JobResult result = (JobResult) joinPoint.proceed();

            if (result.exitCode() == JobExitCode.SUCCESS) {
                auditLogger.jobSucceeded(jobName, result.message());
            } else {
                auditLogger.jobFailed(jobName, result.exitCode(), result.message());
            }

            return result;
        } catch (Throwable e) {
            auditLogger.jobFailedByException(jobName, e);
            throw e;
        }
    }
}
