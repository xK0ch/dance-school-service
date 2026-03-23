CREATE TABLE faq (
    id         BIGSERIAL    PRIMARY KEY,
    question   TEXT         NOT NULL,
    answer     TEXT         NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at TIMESTAMP    NOT NULL DEFAULT now()
);
