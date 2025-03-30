CREATE TABLE IF NOT EXISTS files
(
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    original_file_name   VARCHAR(255) NOT NULL,
    content_type         VARCHAR(255) NOT NULL,
    size                 BIGINT       NOT NULL,
    file_data            BYTEA        NOT NULL,
    organization_card_id UUID         NOT NULL,
    created_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (organization_card_id) REFERENCES organization_cards (id) ON DELETE CASCADE
);
