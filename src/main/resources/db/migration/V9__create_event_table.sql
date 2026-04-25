CREATE TABLE event (
    id UUID PRIMARY KEY DEFAULT uuidv7(),
    name VARCHAR(255) NOT NULL,
    event_date DATE NOT NULL,
    entry_cost DECIMAL(10, 2),
    entry_cost_with_customer_card DECIMAL(10, 2),
    remark TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
