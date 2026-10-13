package com.example.systemcommon.backup.log.compress.targz;

import com.example.systemcommon.backup.log.AuditLogBackupProperties;
import com.example.systemcommon.backup.log.compress.LogCompressExecutor;
import com.example.systemcommon.fw.operation.OperationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TarGzAuditLogCompressExecutor implements LogCompressExecutor {
    private final AuditLogBackupProperties properties;

    private final TarGzLogCompressService compressService;

    @Override
    public OperationResult compress() {
        return compressService.compress(properties);
    }
}
