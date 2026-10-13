package com.example.systemcommon.backup.db;

import com.example.systemcommon.fw.script.ScriptOperationProperties;
import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.db-backup")
public record DbBackupProperties(Path backupRootDirectory, ScriptOperationProperties backup) {}
