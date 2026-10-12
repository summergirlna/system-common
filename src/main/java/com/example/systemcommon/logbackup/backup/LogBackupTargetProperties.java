package com.example.systemcommon.logbackup.backup;

import java.nio.file.Path;

public record LogBackupTargetProperties(String productName, Path sourcePath) {}
