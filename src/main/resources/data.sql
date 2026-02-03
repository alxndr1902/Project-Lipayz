CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO roles (id, created_at, created_by, version, code, name)
SELECT uuid_generate_v4(), NOW(), uuid_generate_v4(), 0, 'SA', 'Super Admin'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'SA');

INSERT INTO roles (id, created_at, created_by, version, code, name)
SELECT uuid_generate_v4(),NOW(), uuid_generate_v4(), 0, 'CUST', 'Customer'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'CUST');

INSERT INTO roles (id, created_at, created_by, version, code, name)
SELECT uuid_generate_v4(), NOW(), uuid_generate_v4(), 0, 'PGA', 'Payment Gateway Admin'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'PGA');

INSERT INTO roles(id, created_at, created_by, version, code, name)
SELECT uuid_generate_v4(), NOW(), uuid_generate_v4(), 0, 'SYS', 'System'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'SYS');


INSERT INTO transaction_status (id, created_at, created_by, version, code, name)
SELECT uuid_generate_v4(),NOW(), uuid_generate_v4(), 0, 'SCS', 'Success'
    WHERE NOT EXISTS (SELECT 1 FROM transaction_status WHERE code = 'SCS');

INSERT INTO transaction_status (id, created_at, created_by, version, code, name)
SELECT uuid_generate_v4(), NOW(), uuid_generate_v4(), 0, 'RJC', 'Reject'
    WHERE NOT EXISTS (SELECT 1 FROM transaction_status WHERE code = 'RJC');

INSERT INTO transaction_status (id, created_at, created_by, version, code, name)
SELECT uuid_generate_v4(), NOW(), uuid_generate_v4(), 0, 'PRCS', 'Processing'
    WHERE NOT EXISTS (SELECT 1 FROM transaction_status WHERE code = 'PRCS');


INSERT INTO users (
    id,
    created_at,
    created_by,
    version,
    email,
    password,
    full_name,
    role_id,
    is_activated
)
SELECT
    uuid_generate_v4(),
    NOW(),
    uuid_generate_v4(),
    0,
    'sa@gmail.com',
    '$2a$12$GljTtavZT7.Z12MYlPWyHuV1dFJ5diadg7.K/ZJ1wo5hpIbKfewZC',
    'Super Admin',
    (SELECT id FROM roles WHERE code = 'SA'),
    true
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'sa@gmail.com'
);

INSERT INTO users (
    id,
    created_at,
    created_by,
    version,
    email,
    password,
    full_name,
    role_id,
    is_activated
)
SELECT
    uuid_generate_v4(),
    NOW(),
    uuid_generate_v4(),
    0,
    'system@gmail.com',
    '$2a$12$.5x6fNmCh.M2RcayKfXasuuZggPSQ07sUjskPaK7xakB6mFxcMbLe',
    'System',
    (SELECT id FROM roles WHERE code = 'SYS'),
    true
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'system@gmail.com'
);
