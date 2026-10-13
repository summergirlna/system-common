package com.example.systemcommon.backup.log;

import com.example.systemcommon.backup.log.backup.LogBackupTargetProperties;
import java.nio.file.Path;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.audit-log-backup")
public record AuditLogBackupProperties(
        Path archiveRootDirectory, int compressAfterDays, int deleteAfterDays, List<LogBackupTargetProperties> targets)
        implements LogArchiveProperties {}
