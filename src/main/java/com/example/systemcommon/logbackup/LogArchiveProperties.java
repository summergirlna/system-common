package com.example.systemcommon.logbackup;

import com.example.systemcommon.logbackup.backup.LogBackupTargetProperties;
import java.nio.file.Path;
import java.util.List;

public interface LogArchiveProperties {

    Path archiveRootDirectory();

    int compressAfterDays();

    int deleteAfterDays();

    List<LogBackupTargetProperties> targets();
}
