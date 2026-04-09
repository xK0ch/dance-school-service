CREATE TABLE faq (
    id            UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    question      TEXT         NOT NULL,
    answer        TEXT         NOT NULL,
    display_order INTEGER      NOT NULL DEFAULT 0,
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP    NOT NULL DEFAULT now()
);
