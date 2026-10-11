package com.example.systemcommon.logbackup;

import java.nio.file.Path;

public record LogBackupTargetProperties(String productName, Path sourcePath) {}
