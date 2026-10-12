package com.example.systemcommon.job;

import com.example.systemcommon.fw.job.Job;
import com.example.systemcommon.fw.job.JobExitCode;
import com.example.systemcommon.fw.job.JobResult;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class LogBackupJob implements Job {

    private final LogBackupOperation logBackupOperation;

    @Override
    public String name() {
        return "log-backup";
    }

    @Override
    public JobResult execute() {
        OperationResult result = logBackupOperation.execute();

        if (result.isFailure()) {
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, result.message());
        }

        return JobResult.success(result.message());
    }
}
