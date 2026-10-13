package com.example.systemcommon.backup.log.cleanup;

import java.nio.file.Path;
import java.time.LocalDate;

public record LogCleanupTarget(Path tarGzPath, LocalDate date) {}
