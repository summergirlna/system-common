MVN := /bin/sh /Users/kuritayu/Library/Application\ Support/JetBrains/IntelliJIdea2026.2/plugins/maven-plugin/lib/maven3/bin/mvn
RUN_JOB := ./system-common-run.sh

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
	$(RUN_JOB) $(JOB)

run-monday-morning: package
	$(RUN_JOB) operation-date-update
	$(RUN_JOB) cluster-pre-check
	$(RUN_JOB) cluster-start
	$(RUN_JOB) cluster-check
	$(RUN_JOB) middleware-start
	$(RUN_JOB) middleware-check
	$(RUN_JOB) online-start

run-weekday-morning: package
	$(RUN_JOB) operation-date-update
	$(RUN_JOB) application-start
	$(RUN_JOB) middleware-check
	$(RUN_JOB) online-start

run-weekday-night: package
	$(RUN_JOB) online-stop
	$(RUN_JOB) application-stop
	$(RUN_JOB) log-backup
	$(RUN_JOB) log-compress
	$(RUN_JOB) log-cleanup
	$(RUN_JOB) audit-log-backup
	$(RUN_JOB) audit-log-compress
	$(RUN_JOB) audit-log-cleanup

run-friday-night: package
	$(RUN_JOB) online-stop
	$(RUN_JOB) application-stop
	$(RUN_JOB) log-backup
	$(RUN_JOB) log-compress
	$(RUN_JOB) log-cleanup
	$(RUN_JOB) audit-log-backup
	$(RUN_JOB) audit-log-compress
	$(RUN_JOB) audit-log-cleanup
	$(RUN_JOB) middleware-stop
	$(RUN_JOB) cluster-stop