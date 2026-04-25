CREATE TABLE admin_user (
    id       UUID         PRIMARY KEY DEFAULT uuidv7(),
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
