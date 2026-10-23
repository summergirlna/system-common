package com.example.systemcommon.middleware.script;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.fw.script.ScriptOperationExecutor;
import com.example.systemcommon.fw.script.ScriptOperationProperties;
import com.example.systemcommon.fw.script.ScriptStepProperties;
import com.example.systemcommon.middleware.MiddlewareProperties;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ScriptMiddlewareStarterTest {

    @Test
    void success() {
        ScriptOperationExecutor executor = Mockito.mock(ScriptOperationExecutor.class);

        List<ScriptStepProperties> steps = List.of(new ScriptStepProperties(
                "uCosminexus Application Server",
                "/etc/cmcluster/pp/uas.sh",
                List.of("start"),
                Duration.ofSeconds(300),
                List.of(0)));

        MiddlewareProperties properties = new MiddlewareProperties(
                new ScriptOperationProperties(steps),
                new ScriptOperationProperties(List.of()),
                new ScriptOperationProperties(List.of()));

        OperationResult expected = OperationResult.success("stopped");

        when(executor.execute("ミドルウェア", "起動", steps)).thenReturn(expected);

        ScriptMiddlewareStarter starter = new ScriptMiddlewareStarter(properties, executor);

        OperationResult actual = starter.start();

        assertThat(actual).isSameAs(expected);
        verify(executor).execute("ミドルウェア", "起動", steps);
    }
}
