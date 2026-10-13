package com.example.systemcommon.backup.log;

import com.example.systemcommon.backup.log.backup.LogBackupTargetProperties;
import java.nio.file.Path;
import java.util.List;

public interface LogArchiveProperties {

    Path archiveRootDirectory();

    int compressAfterDays();

    int deleteAfterDays();

    List<LogBackupTargetProperties> targets();
}
