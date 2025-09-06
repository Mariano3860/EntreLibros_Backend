CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (id, email, password, role) VALUES
    (gen_random_uuid(), 'user@entrelibros.com', crypt('correcthorsebatterystaple', gen_salt('bf')), 'USER'),
    (gen_random_uuid(), 'admin@entrelibros.com', crypt('adminpass', gen_salt('bf')), 'ADMIN'),
    (gen_random_uuid(), 'alice@entrelibros.com', crypt('alicepass', gen_salt('bf')), 'USER'),
    (gen_random_uuid(), 'bob@entrelibros.com', crypt('bobpass', gen_salt('bf')), 'USER'),
    (gen_random_uuid(), 'carol@entrelibros.com', crypt('carolpass', gen_salt('bf')), 'USER');
