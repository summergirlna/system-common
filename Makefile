MVN := /bin/sh /Users/kuritayu/Library/Application\ Support/JetBrains/IntelliJIdea2026.2/plugins/maven-plugin/lib/maven3/bin/mvn
JAR := target/system-common-1.0-SNAPSHOT.jar

.PHONY: help clean package build verify test format format-check lint check run-cluster-start

help:
	@echo "Available targets:"
	@echo "  make clean              - Remove build artifacts"
	@echo "  make package            - Build jar package"
	@echo "  make build              - Clean and package"
	@echo "  make verify             - Run Maven verify phase"
	@echo "  make test               - Run tests"
	@echo "  make format             - Apply code formatter"
	@echo "  make format-check       - Check code formatting"
	@echo "  make lint               - Run Checkstyle"
	@echo "  make check              - Run format check, lint, and tests"
	@echo "  make run-cluster-start  - Run cluster-start job"

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

run-cluster-start: package
	java -jar $(JAR) cluster-start
