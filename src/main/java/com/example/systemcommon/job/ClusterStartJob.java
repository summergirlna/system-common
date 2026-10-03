package com.example.systemcommon.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ClusterStartJob implements Job {

    @Override
    public String name() {
        return "cluster-start";
    }

    @Override
    public JobResult execute() {
        log.info("クラスタ起動処理を開始します。");

        // todo 実際のクラスタ起動コマンド実行は後続で実装する

        log.info("クラスタ起動処理が正常に終了しました。");
        return JobResult.success("クラスタ起動処理が正常に終了しました。");
    }
}
