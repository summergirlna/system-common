package com.example.systemcommon.logbackup.compress.targz;

import com.example.systemcommon.fw.operation.OperationResult;
import com.example.systemcommon.logbackup.AuditLogBackupProperties;
import com.example.systemcommon.logbackup.compress.LogCompressExecutor;
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
