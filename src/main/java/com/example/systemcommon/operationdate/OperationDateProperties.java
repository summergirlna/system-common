package com.example.systemcommon.operationdate;

import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "system-common.operation-date")
public record OperationDateProperties(Path filePath) {}
