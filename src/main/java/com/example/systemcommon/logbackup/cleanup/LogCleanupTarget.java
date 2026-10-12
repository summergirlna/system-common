package com.example.systemcommon.logbackup.cleanup;

import java.nio.file.Path;
import java.time.LocalDate;

public record LogCleanupTarget(Path tarGzPath, LocalDate date) {}
