package com.example.systemcommon.application.script;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.systemcommon.application.ApplicationProperties;
import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.script.ScriptOperationExecutor;
import com.example.systemcommon.fw.script.ScriptOperationProperties;
import com.example.systemcommon.fw.script.ScriptStepProperties;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ScriptApplicationStopperTest {

    @Test
    void success() {
        ScriptOperationExecutor executor = Mockito.mock(ScriptOperationExecutor.class);

        List<ScriptStepProperties> steps = List.of(new ScriptStepProperties(
                "uCosminexus Application Server",
                "/etc/cmcluster/pp/uas.sh",
                List.of("stop"),
                Duration.ofSeconds(300),
                List.of(0)));

        ApplicationProperties properties = new ApplicationProperties(
                new ScriptOperationProperties(List.of()), new ScriptOperationProperties(steps));

        OperationResult expected = OperationResult.success("stopped");

        when(executor.execute("アプリケーション", "停止", steps)).thenReturn(expected);

        ScriptApplicationStopper stopper = new ScriptApplicationStopper(properties, executor);

        OperationResult actual = stopper.stop();

        assertThat(actual).isSameAs(expected);
        verify(executor).execute("アプリケーション", "停止", steps);
    }
}
