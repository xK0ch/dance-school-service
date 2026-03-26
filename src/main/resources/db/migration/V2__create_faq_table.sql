CREATE TABLE faq (
    id            BIGSERIAL    PRIMARY KEY,
    question      TEXT         NOT NULL,
    answer        TEXT         NOT NULL,
    display_order INTEGER      NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT now()
);
