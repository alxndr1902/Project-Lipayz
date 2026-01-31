CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

INSERT INTO roles (created_at, created_by, version, code, name)
SELECT NOW(), NULL, 0, 'SA', 'Super Admin'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'SA');

INSERT INTO roles (created_at, created_by, version, code, name)
SELECT NOW(), NULL, 0, 'CUST', 'Customer'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'CUST');

INSERT INTO roles (created_at, created_by, version, code, name)
SELECT NOW(), NULL, 0, 'PGA', 'Payment Gateway Admin'
    WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'PGA');


INSERT INTO transaction_status (created_at, created_by, version, code, name)
SELECT NOW(), NULL, 0, 'SCS', 'Success'
    WHERE NOT EXISTS (SELECT 1 FROM transaction_status WHERE code = 'SCS');

INSERT INTO transaction_status (created_at, created_by, version, code, name)
SELECT NOW(), NULL, 0, 'RJC', 'Reject'
    WHERE NOT EXISTS (SELECT 1 FROM transaction_status WHERE code = 'RJC');

INSERT INTO transaction_status (created_at, created_by, version, code, name)
SELECT NOW(), NULL, 0, 'PRCS', 'Processing'
    WHERE NOT EXISTS (SELECT 1 FROM transaction_status WHERE code = 'PRCS');


INSERT INTO users (
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
    NOW(),
    NULL,
    0,
    'sa@gmail.com',
    '$2a$12$GljTtavZT7.Z12MYlPWyHuV1dFJ5diadg7.K/ZJ1wo5hpIbKfewZC',
    'Super Admin',
    (SELECT id FROM roles WHERE code = 'SA'),
    true
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'sa@gmail.com'
);
