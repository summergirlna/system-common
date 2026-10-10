MVN := /bin/sh /Users/kuritayu/Library/Application\ Support/JetBrains/IntelliJIdea2026.2/plugins/maven-plugin/lib/maven3/bin/mvn
JAR := target/system-common-1.0-SNAPSHOT.jar
LOG_DIR := logs
JAVA := SYSTEM_COMMON_LOG_DIR=$(LOG_DIR) java -jar $(JAR)

.PHONY: help clean package build verify test format format-check lint check \
	run-cluster-pre-check run-cluster-start run-cluster-check \
	run-middleware-start run-middleware-check \
	run-application-start run-application-stop \
	run-online-start run-online-stop \
	run-monday-morning run-weekday-morning run-weekday-night run-friday-night

help:
	@echo "Available targets:"
	@echo "  make clean                  - Remove build artifacts"
	@echo "  make package                - Build jar package"
	@echo "  make build                  - Clean and package"
	@echo "  make verify                 - Run Maven verify phase"
	@echo "  make test                   - Run tests"
	@echo "  make format                 - Apply code formatter"
	@echo "  make format-check           - Check code formatting"
	@echo "  make lint                   - Run Checkstyle"
	@echo "  make check                  - Run format check, lint, and tests"
	@echo "  make run-cluster-pre-check  - Run cluster-pre-check job"
	@echo "  make run-cluster-start      - Run cluster-start job"
	@echo "  make run-cluster-check      - Run cluster-check job"
	@echo "  make run-middleware-start   - Run middleware-start job"
	@echo "  make run-middleware-check   - Run middleware-check job"
	@echo "  make run-application-start  - Run application-start job"
	@echo "  make run-application-stop   - Run application-stop job"
	@echo "  make run-online-start       - Run online-start job"
	@echo "  make run-online-stop        - Run online-stop job"
	@echo "  make run-monday-morning     - Run Monday morning operation"
	@echo "  make run-weekday-morning    - Run Tuesday-Friday morning operation"
	@echo "  make run-weekday-night      - Run Monday-Thursday night operation"
	@echo "  make run-friday-night       - Run Friday night operation"

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

run-cluster-pre-check: package
	$(JAVA) cluster-pre-check

run-cluster-start: package
	$(JAVA) cluster-start

run-cluster-check: package
	$(JAVA) cluster-check

run-middleware-start: package
	$(JAVA) middleware-start

run-middleware-check: package
	$(JAVA) middleware-check

run-application-start: package
	$(JAVA) application-start

run-application-stop: package
	$(JAVA) application-stop

run-online-start: package
	$(JAVA) online-start

run-online-stop: package
	$(JAVA) online-stop

run-monday-morning: package
	$(JAVA) cluster-pre-check
	$(JAVA) cluster-start
	$(JAVA) cluster-check
	$(JAVA) middleware-start
	$(JAVA) middleware-check
	$(JAVA) online-start

run-weekday-morning: package
	$(JAVA) application-start
	$(JAVA) middleware-check
	$(JAVA) online-start

run-weekday-night: package
	$(JAVA) online-stop
	$(JAVA) application-stop

run-friday-night: package
	$(JAVA) online-stop
	$(JAVA) application-stop
	$(JAVA) cluster-stop