package com.example.systemcommon.online.script;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.script.ScriptOperationExecutor;
import com.example.systemcommon.fw.script.ScriptOperationProperties;
import com.example.systemcommon.fw.script.ScriptStepProperties;
import com.example.systemcommon.online.OnlineProperties;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ScriptOnlineStopperTest {

    @Test
    void success() {
        ScriptOperationExecutor executor = Mockito.mock(ScriptOperationExecutor.class);

        List<ScriptStepProperties> steps = List.of(new ScriptStepProperties(
                "負荷分散装置振り分け停止", "/etc/cmcluster/pp/bigip.sh", List.of("stop"), Duration.ofSeconds(300), List.of(0)));

        OnlineProperties properties =
                new OnlineProperties(new ScriptOperationProperties(List.of()), new ScriptOperationProperties(steps));

        OperationResult expected = OperationResult.success("stopped");

        when(executor.execute("オンライン業務", "停止", steps)).thenReturn(expected);

        ScriptOnlineStopper stopper = new ScriptOnlineStopper(properties, executor);

        OperationResult actual = stopper.stop();

        assertThat(actual).isSameAs(expected);
        verify(executor).execute("オンライン業務", "停止", steps);
    }
}
