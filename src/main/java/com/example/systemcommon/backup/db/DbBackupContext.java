package com.example.systemcommon.backup.db;

import java.nio.file.Path;
import java.time.LocalDate;

public record DbBackupContext(LocalDate operationDate, Path backupRootDirectory, String backupId) {}
