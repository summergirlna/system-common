package com.example.systemcommon.job;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobRunner implements CommandLineRunner {

    private final Map<String, Job> jobs;

    public JobRunner(List<Job> jobs) {
        this.jobs = jobs.stream().collect(Collectors.toUnmodifiableMap(Job::name, Function.identity()));
    }

    @Override
    public void run(String @NonNull ... args) {
        JobResult result = execute(args);
        log.info("ジョブ終了: exitCode={}, message={}", result.code(), result.message());
        System.exit(result.code());
    }

    private JobResult execute(String... args) {
        if (args.length == 0) {
            log.error("ジョブ名が指定されていません。");
            return JobResult.failure(JobExitCode.INVALID_ARGUMENT, "ジョブ名が指定されていません。");
        }

        String jobName = args[0];
        Job job = jobs.get(jobName);

        if (job == null) {
            log.error("未定義のジョブが指定されました。jobName={}", jobName);
            return JobResult.failure(JobExitCode.INVALID_ARGUMENT, "未定義のジョブが指定されました: " + jobName);
        }

        try {
            log.info("ジョブ開始: jobName={}", jobName);
            return job.execute();
        } catch (Exception e) {
            log.error("ジョブ実行中に想定外のエラーが発生しました。jobName={}", jobName, e);
            return JobResult.failure(JobExitCode.UNEXPECTED_ERROR, "ジョブ実行中に想定外のエラーが発生しました: " + jobName);
        }
    }
}
