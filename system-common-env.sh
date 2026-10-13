# -----------------------------------------
# 運用日付関連
# -----------------------------------------
# 運用日付出力パス
export SYSTEM_COMMON_OPERATION_DATE_FILE="${SYSTEM_COMMON_OPERATION_DATE_FILE:-var/operation-date.txt}"

# -----------------------------------------
# ログ退避関連
# -----------------------------------------
# 退避元ログパス
export SYSTEM_COMMON_LOG_DIR="${SYSTEM_COMMON_LOG_DIR:-logs}"
# 退避先ログパス
export SYSTEM_COMMON_LOG_ARCHIVE_DIR="${SYSTEM_COMMON_LOG_ARCHIVE_DIR:-log_arc}"
# 圧縮期限(この日を過ぎたら圧縮する)
export SYSTEM_COMMON_LOG_COMPRESS_AFTER_DAYS="${SYSTEM_COMMON_LOG_COMPRESS_AFTER_DAYS:-5}"
# 削除期限(この日を過ぎたら削除する)
export SYSTEM_COMMON_LOG_DELETE_AFTER_DAYS="${SYSTEM_COMMON_LOG_DELETE_AFTER_DAYS:-31}"

# -----------------------------------------
# 監査証跡ログ退避関連
# -----------------------------------------
# 退避元ログパス
export SYSTEM_COMMON_AUDIT_LOG_DIR="${SYSTEM_COMMON_AUDIT_LOG_DIR:-logs}"
# 退避先ログパス
export SYSTEM_COMMON_AUDIT_LOG_ARCHIVE_DIR="${SYSTEM_COMMON_AUDIT_LOG_ARCHIVE_DIR:-audit_log_arc}"
# 圧縮期限(この日を過ぎたら圧縮する)
export SYSTEM_COMMON_AUDIT_LOG_COMPRESS_AFTER_DAYS="${SYSTEM_COMMON_AUDIT_LOG_COMPRESS_AFTER_DAYS:-31}"
# 削除期限(この日を過ぎたら削除する)
export SYSTEM_COMMON_AUDIT_LOG_DELETE_AFTER_DAYS="${SYSTEM_COMMON_AUDIT_LOG_DELETE_AFTER_DAYS:-366}"

# -----------------------------------------
# DBバックアップ関連関連
# -----------------------------------------
# バックアップ先パス
export SYSTEM_COMMON_DB_BACKUP_DIR="${SYSTEM_COMMON_DB_BACKUP_DIR:-db_backup}"