-- Pgcrypto for uuid generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;
--;;

-- Create table for discussion
CREATE TABLE discussion (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ
);
--;;
