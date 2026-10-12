package com.example.systemcommon.logbackup.compress;

import java.nio.file.Path;
import java.time.LocalDate;

public record LogCompressTarget(Path dateDirectory, LocalDate date) {}
