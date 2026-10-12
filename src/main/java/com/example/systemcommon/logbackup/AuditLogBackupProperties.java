package com.example.systemcommon.logbackup;

import com.example.systemcommon.logbackup.backup.LogBackupTargetProperties;
import java.nio.file.Path;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.audit-log-backup")
public record AuditLogBackupProperties(
        Path archiveRootDirectory, int compressAfterDays, int deleteAfterDays, List<LogBackupTargetProperties> targets)
        implements LogArchiveProperties {}
