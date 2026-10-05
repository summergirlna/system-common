package com.example.systemcommon.job;

import com.example.systemcommon.command.CommandExecutor;
import com.example.systemcommon.command.CommandResult;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ClusterStartJob implements Job {

    private final CommandExecutor commandExecutor;

    @Override
    public String name() {
        return "cluster-start";
    }

    @Override
    public JobResult execute() {
        log.info("クラスタ起動処理を開始します。");

        CommandResult result = commandExecutor.execute(List.of("echo", "cluster start"), Duration.ofSeconds(30));

        if (result.isFailure()) {
            log.error("クラスタ起動コマンドが異常終了しました。exitCode={}, stderr={}", result.exitCode(), result.stderr());
            return JobResult.failure(JobExitCode.SYSTEM_ERROR, "クラスタ起動コマンドが異常終了しました。exitCode=" + result.exitCode());
        }

        log.info("クラスタ起動処理が正常に終了しました。");
        return JobResult.success("クラスタ起動処理が正常に終了しました。");
    }
}
