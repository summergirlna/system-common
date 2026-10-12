#!/usr/bin/env bash
set -euo pipefail

BASE_DIR="$(cd "$(dirname "$0")" && pwd)"
ENV_FILE="${SYSTEM_COMMON_ENV_FILE:-${BASE_DIR}/system-common-env.sh}"
JAR_FILE="${SYSTEM_COMMON_JAR_FILE:-${BASE_DIR}/target/system-common-1.0-SNAPSHOT.jar}"

if [ "$#" -ne 1 ]; then
  echo "Usage: $0 <job-name>" >&2
  exit 1
fi

JOB_NAME="$1"

if [ ! -f "${ENV_FILE}" ]; then
  echo "Environment file not found: ${ENV_FILE}" >&2
  exit 1
fi

# shellcheck disable=SC1090
. "${ENV_FILE}"

exec java -jar "${JAR_FILE}" "${JOB_NAME}"