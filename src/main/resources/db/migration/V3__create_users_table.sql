CREATE TABLE IF NOT EXISTS users
(
    id UUID            PRIMARY KEY,
    telegram_id BIGINT NOT NULL UNIQUE,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);