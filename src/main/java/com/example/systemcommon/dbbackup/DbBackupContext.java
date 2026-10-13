package com.example.systemcommon.dbbackup;

import java.nio.file.Path;
import java.time.LocalDate;

public record DbBackupContext(LocalDate operationDate, Path backupRootDirectory, String backupId) {}
