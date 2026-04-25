CREATE TABLE event_cleanup_config (
    id UUID PRIMARY KEY DEFAULT uuidv7(),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);

INSERT INTO event_cleanup_config DEFAULT VALUES;
