package com.example.systemcommon.backup.log.backup;

import java.nio.file.Path;

public record LogBackupTargetProperties(String productName, Path sourcePath) {}
