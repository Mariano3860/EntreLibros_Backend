CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (id, email, password, role) VALUES
    (gen_random_uuid(), 'admin@entrelibros.com', crypt('adminpass', gen_salt('bf')), 'ADMIN'),
    (gen_random_uuid(), 'super@entrelibros.com', crypt('superpass', gen_salt('bf')), 'ADMIN'),
    (gen_random_uuid(), 'dev@entrelibros.com', crypt('devpass', gen_salt('bf')), 'USER'),
    (gen_random_uuid(), 'test@entrelibros.com', crypt('testpass', gen_salt('bf')), 'USER'),
    (gen_random_uuid(), 'qa@entrelibros.com', crypt('qapass', gen_salt('bf')), 'USER');
