CREATE TABLE IF NOT EXISTS organization_cards
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL,
    company    VARCHAR(255),
    description TEXT,
    phone      VARCHAR(255),
    district   VARCHAR(255),
    city       VARCHAR(255),
    address    TEXT,
    is_active  Boolean DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);