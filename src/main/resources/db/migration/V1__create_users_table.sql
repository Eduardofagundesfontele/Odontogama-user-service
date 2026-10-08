CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    email VARCHAR(254) NOT NULL,

    password VARCHAR(255) NOT NULL,

    role VARCHAR(30) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT uk_users_email UNIQUE (email)
);