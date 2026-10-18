package com.example.systemcommon.resource.graph;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.resource.common.graph.ResourceGraphReportGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ResourceGraphReportService {

    private final List<ResourceGraphReportGenerator> generators;

    public OperationResult generate() {
        log.info("リソースグラフ生成を開始します。");

        if (generators.isEmpty()) {
            log.error("リソースグラフ生成処理が登録されていません。");
            return OperationResult.failure("リソースグラフ生成処理が登録されていません。");
        }

        for (ResourceGraphReportGenerator generator : generators) {
            OperationResult result = generator.generate();
            if (result.isFailure()) {
                log.error("リソースグラフ生成が異常終了しました。message={}", result.message());
                return result;
            }
        }

        log.info("リソースグラフ生成が正常終了しました。");
        return OperationResult.success("リソースグラフ生成が正常に終了しました。");
    }
}
