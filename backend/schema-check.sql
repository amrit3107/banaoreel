-- Mirrors the JPA entities exactly, used here only to validate the schema
-- design against real Postgres (Hibernate's ddl-auto:update will normally
-- create this automatically at app startup — this file is a manual check).

CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- for gen_random_uuid()

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    fcm_token VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id),
    balance_paise BIGINT NOT NULL DEFAULT 0,
    version BIGINT
);

CREATE TABLE wallet_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    type VARCHAR(20) NOT NULL CHECK (type IN ('RECHARGE', 'DEBIT', 'REFUND')),
    amount_paise BIGINT NOT NULL,
    ref_job_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE video_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    prompt VARCHAR(500) NOT NULL,
    duration_sec INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'QUEUED'
        CHECK (status IN ('QUEUED','SCRIPTING','RENDERING_VISUALS','VOICING','ASSEMBLING','DONE','FAILED')),
    video_url VARCHAR(500),
    cost_paise BIGINT NOT NULL,
    failure_reason VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE recharge_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    amount_paise BIGINT NOT NULL,
    gateway_order_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'created'
);
