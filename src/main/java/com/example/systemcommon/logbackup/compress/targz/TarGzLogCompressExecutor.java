package com.example.systemcommon.logbackup.compress.targz;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.LogBackupProperties;
import com.example.systemcommon.logbackup.compress.LogCompressExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TarGzLogCompressExecutor implements LogCompressExecutor {

    private final LogBackupProperties properties;

    private final TarGzLogCompressService compressService;

    @Override
    public OperationResult compress() {
        return compressService.compress(properties);
    }
}
