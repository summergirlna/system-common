package com.example.systemcommon.logbackup;

import java.nio.file.Path;
import java.time.LocalDate;

public record LogCleanupTarget(Path tarGzPath, LocalDate date) {}
