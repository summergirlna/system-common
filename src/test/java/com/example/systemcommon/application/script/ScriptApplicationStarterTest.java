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

class ScriptApplicationStarterTest {

    @Test
    void startExecutesApplicationStartScript() {
        ScriptOperationExecutor executor = Mockito.mock(ScriptOperationExecutor.class);

        List<ScriptStepProperties> steps = List.of(new ScriptStepProperties(
                "uCosminexus Application Server",
                "/etc/cmcluster/pp/uas.sh",
                List.of("start"),
                Duration.ofSeconds(300),
                List.of(0)));

        ApplicationProperties properties = new ApplicationProperties(
                new ScriptOperationProperties(steps), new ScriptOperationProperties(List.of()));

        OperationResult expected = OperationResult.success("started");

        when(executor.execute("アプリケーション", "起動", steps)).thenReturn(expected);

        ScriptApplicationStarter starter = new ScriptApplicationStarter(properties, executor);

        OperationResult actual = starter.start();

        assertThat(actual).isSameAs(expected);
        verify(executor).execute("アプリケーション", "起動", steps);
    }
}
