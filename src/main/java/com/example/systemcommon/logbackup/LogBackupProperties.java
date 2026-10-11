package com.example.systemcommon.logbackup;

import java.nio.file.Path;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.log-backup")
public record LogBackupProperties(
        Path archiveRootDirectory,
        int compressAfterDays,
        int deleteAfterDays,
        List<LogBackupTargetProperties> targets) {}
