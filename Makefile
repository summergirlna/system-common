MVN := /bin/sh /Users/kuritayu/Library/Application\ Support/JetBrains/IntelliJIdea2026.2/plugins/maven-plugin/lib/maven3/bin/mvn
JAR := target/system-common-1.0-SNAPSHOT.jar
LOG_DIR := logs
OPERATION_DATE_FILE := var/operation-date.txt
LOG_ARCHIVE_DIR := log_arc
AUDIT_LOG_DIR := logs
AUDIT_LOG_ARCHIVE_DIR := audit_log_arc
JAVA := SYSTEM_COMMON_LOG_DIR=$(LOG_DIR) \
	SYSTEM_COMMON_OPERATION_DATE_FILE=$(OPERATION_DATE_FILE) \
	SYSTEM_COMMON_LOG_ARCHIVE_DIR=$(LOG_ARCHIVE_DIR) \
	SYSTEM_COMMON_AUDIT_LOG_DIR=$(AUDIT_LOG_DIR) \
	SYSTEM_COMMON_AUDIT_LOG_ARCHIVE_DIR=$(AUDIT_LOG_ARCHIVE_DIR) \
	java -jar $(JAR)

.PHONY: help clean package build verify test format format-check lint check run \
	run-monday-morning run-weekday-morning run-weekday-night run-friday-night

help:
	@echo "Available targets:"
	@echo "  make clean                      - Remove build artifacts"
	@echo "  make package                    - Build jar package"
	@echo "  make build                      - Clean and package"
	@echo "  make verify                     - Run Maven verify phase"
	@echo "  make test                       - Run tests"
	@echo "  make format                     - Apply code formatter"
	@echo "  make format-check               - Check code formatting"
	@echo "  make lint                       - Run Checkstyle"
	@echo "  make check                      - Run format check, lint, and tests"
	@echo "  make run JOB=<job-name>         - Run arbitrary job"
	@echo "  make run-monday-morning         - Run Monday morning operation"
	@echo "  make run-weekday-morning        - Run Tuesday-Friday morning operation"
	@echo "  make run-weekday-night          - Run Monday-Thursday night operation"
	@echo "  make run-friday-night           - Run Friday night operation"

clean:
	$(MVN) clean

package:
	$(MVN) package

build:
	$(MVN) clean package

verify:
	$(MVN) clean verify

test:
	$(MVN) test

format:
	$(MVN) spotless:apply

format-check:
	$(MVN) spotless:check

lint:
	$(MVN) checkstyle:check

check:
	$(MVN) spotless:check checkstyle:check test

run: package
	@test -n "$(JOB)" || (echo "JOB is required. Usage: make run JOB=<job-name>" && exit 1)
	$(JAVA) $(JOB)

run-monday-morning: package
	$(JAVA) operation-date-update
	$(JAVA) cluster-pre-check
	$(JAVA) cluster-start
	$(JAVA) cluster-check
	$(JAVA) middleware-start
	$(JAVA) middleware-check
	$(JAVA) online-start

run-weekday-morning: package
	$(JAVA) operation-date-update
	$(JAVA) application-start
	$(JAVA) middleware-check
	$(JAVA) online-start

run-weekday-night: package
	$(JAVA) online-stop
	$(JAVA) application-stop
	$(JAVA) log-backup
	$(JAVA) log-compress
	$(JAVA) log-cleanup
	$(JAVA) audit-log-backup
	$(JAVA) audit-log-compress
	$(JAVA) audit-log-cleanup

run-friday-night: package
	$(JAVA) online-stop
	$(JAVA) application-stop
	$(JAVA) log-backup
	$(JAVA) log-compress
	$(JAVA) log-cleanup
	$(JAVA) audit-log-backup
	$(JAVA) audit-log-compress
	$(JAVA) audit-log-cleanup
	$(JAVA) middleware-stop
	$(JAVA) cluster-stop