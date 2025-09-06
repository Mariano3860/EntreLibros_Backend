CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO users (id, email, password, role) VALUES
    (gen_random_uuid(), 'user@entrelibros.com', crypt('correcthorsebatterystaple', gen_salt('bf')), 'USER'),
    (gen_random_uuid(), 'admin@entrelibros.com', crypt('adminpass', gen_salt('bf')), 'ADMIN');
