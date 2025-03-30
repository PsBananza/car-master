CREATE TABLE IF NOT EXISTS organization_subcategory
(
    organization_id UUID NOT NULL,
    subcategory_id UUID NOT NULL,
    PRIMARY KEY (organization_id, subcategory_id),
    FOREIGN KEY (organization_id) REFERENCES organization_cards (id) ON DELETE CASCADE,
    FOREIGN KEY (subcategory_id) REFERENCES sub_categories (id) ON DELETE CASCADE
);