CREATE TABLE IF NOT EXISTS organization_category
(
    organization_id UUID NOT NULL,
    category_id UUID NOT NULL,
    PRIMARY KEY (organization_id, category_id),
    FOREIGN KEY (organization_id) REFERENCES organization_cards (id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE CASCADE
);
