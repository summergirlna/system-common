package com.example.systemcommon.backup.log.compress.targz;

import com.example.systemcommon.backup.log.LogBackupProperties;
import com.example.systemcommon.backup.log.compress.LogCompressExecutor;
import com.example.systemcommon.fw.operation.OperationResult;
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
