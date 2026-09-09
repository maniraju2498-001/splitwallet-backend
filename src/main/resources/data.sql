-- ============================================================
-- SplitWallet Sample Seed Data
-- ============================================================

-- ----------------------------------------------------------------
-- Users (passwords are BCrypt-hashed "password123")
-- ----------------------------------------------------------------
INSERT INTO users (full_name, email, mobile, password, role) VALUES
('Mani Kumar',    'mani@splitwallet.com',    '+919876543210', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_USER'),
('Rahul Sharma',  'rahul@example.com',       '+919812345678', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_USER'),
('Arun Kumar',    'arun@example.com',        '+919823456789', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_USER'),
('Priya Patel',   'priya@example.com',       '+919834567890', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_USER'),
('Sneha Reddy',   'sneha@example.com',       '+919845678901', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_USER');

-- ----------------------------------------------------------------
-- Wallets (auto-created on register)
-- ----------------------------------------------------------------
INSERT INTO wallets (user_id, balance) VALUES
(1, 1800.00),  -- Mani
(2, 400.00),   -- Rahul (top-up ₹1000, paid ₹600 splits)
(3, 1000.00),  -- Arun
(4, 2500.00),  -- Priya
(5, 750.00);   -- Sneha

-- ----------------------------------------------------------------
-- Transactions
-- ----------------------------------------------------------------
INSERT INTO transactions (user_id, transaction_type, amount, balance_after, description, reference_id, status) VALUES
-- Mani: Top-up ₹2000
(1, 'TOP_UP',       2000.00, 2000.00, 'Wallet Top Up via UPI',                      'TOPUP-A1B2C3D4', 'COMPLETED'),
-- Rahul: Top-up ₹1000
(2, 'TOP_UP',       1000.00, 1000.00, 'Wallet Top Up via Credit Card',               'TOPUP-E5F6G7H8', 'COMPLETED'),
-- Rahul pays Mani ₹300 for Dinner split
(2, 'SPLIT_DEBIT',   300.00,  700.00, 'Split Share Paid - Dinner at Nobu',           'SPL-1-X1Y2Z3',  'COMPLETED'),
-- Mani receives ₹300 credit from Rahul
(1, 'SPLIT_CREDIT',  300.00, 2300.00, 'Split Share Received from Rahul Sharma (Dinner at Nobu)', 'SPL-1-X1Y2Z3', 'COMPLETED'),
-- Arun pays Mani ₹300 for Dinner split
(3, 'SPLIT_DEBIT',   300.00,  700.00, 'Split Share Paid - Dinner at Nobu',           'SPL-1-A2B3C4',  'COMPLETED'),
-- Mani receives ₹300 from Arun
(1, 'SPLIT_CREDIT',  300.00, 2600.00, 'Split Share Received from Arun Kumar (Dinner at Nobu)',   'SPL-1-A2B3C4', 'COMPLETED'),
-- Mani top-up again
(1, 'TOP_UP',        500.00, 3100.00, 'Wallet Top Up via NetBanking',                'TOPUP-I9J0K1L2', 'COMPLETED'),
-- Rahul top-up again
(2, 'TOP_UP',        200.00,  900.00, 'Wallet Top Up via UPI',                       'TOPUP-M3N4O5P6', 'COMPLETED'),
-- Rahul split debit for Goa trip
(2, 'SPLIT_DEBIT',   500.00,  400.00, 'Split Share Paid - Goa Weekend Airbnb',       'SPL-2-Q7R8S9',  'COMPLETED'),
-- Mani receives credit from Rahul for Goa
(1, 'SPLIT_CREDIT',  500.00, 3600.00, 'Split Share Received from Rahul Sharma (Goa Weekend Airbnb)', 'SPL-2-Q7R8S9', 'COMPLETED'),
-- Mani own debit for Goa trip
(1, 'SPLIT_DEBIT',  1800.00, 1800.00, 'Split Share Paid - Goa Weekend Airbnb (Own Share)', 'SPL-2-SELF', 'COMPLETED');

-- ----------------------------------------------------------------
-- Expense Splits
-- ----------------------------------------------------------------
INSERT INTO expense_splits (creator_id, title, total_amount, description, status) VALUES
(1, 'Dinner at Nobu',         900.00,  'Group dinner party with team',            'ACTIVE'),
(1, 'Goa Weekend Airbnb',    4500.00,  'Villa booking for 3 nights in Goa',       'ACTIVE'),
(4, 'Office Pizza Party',    1050.00,  'Dominos order split between team members', 'ACTIVE');

-- ----------------------------------------------------------------
-- Split Participants
-- ----------------------------------------------------------------
-- Split 1: Dinner at Nobu (Total ₹900)
INSERT INTO split_participants (split_id, user_id, amount, approval_status, debited, approved_at) VALUES
(1, 1, 300.00, 'DEBITED', 1, NOW()),   -- Mani (creator, self)
(1, 2, 300.00, 'DEBITED', 1, NOW()),   -- Rahul (approved & debited)
(1, 3, 300.00, 'DEBITED', 1, NOW());   -- Arun (approved & debited)

-- Split 2: Goa Weekend Airbnb (Total ₹4500)
INSERT INTO split_participants (split_id, user_id, amount, approval_status, debited, approved_at, rejected_at) VALUES
(2, 1, 1800.00, 'DEBITED', 1, NOW(),  NULL),   -- Mani (creator, self)
(2, 2,  500.00, 'DEBITED', 1, NOW(),  NULL),   -- Rahul (paid)
(2, 5, 2200.00, 'PENDING', 0, NULL,   NULL);   -- Sneha (still pending)

-- Split 3: Office Pizza Party (Total ₹1050 - created by Priya)
INSERT INTO split_participants (split_id, user_id, amount, approval_status, debited, approved_at, rejected_at) VALUES
(3, 4,  350.00, 'DEBITED', 1, NOW(),  NULL),   -- Priya (creator)
(3, 1,  350.00, 'PENDING', 0, NULL,   NULL),   -- Mani (pending)
(3, 2,  350.00, 'REJECTED',0, NULL,   NOW());  -- Rahul (rejected)

-- ----------------------------------------------------------------
-- Notifications
-- ----------------------------------------------------------------
INSERT INTO notifications (user_id, title, message, type, is_read) VALUES
(1, 'Split Approved',    'Rahul Sharma approved & paid ₹300 for "Dinner at Nobu"',             'APPROVAL',  1),
(1, 'Split Approved',    'Arun Kumar approved & paid ₹300 for "Dinner at Nobu"',               'APPROVAL',  1),
(1, 'Split Approved',    'Rahul Sharma approved & paid ₹500 for "Goa Weekend Airbnb"',         'APPROVAL',  1),
(1, 'New Split Request', 'Priya Patel requested ₹350 for "Office Pizza Party"',                 'REQUEST',   0),
(2, 'New Split Request', 'Mani Kumar requested ₹300 for "Dinner at Nobu"',                     'REQUEST',   1),
(2, 'Successful Debit',  '₹300 debited for "Dinner at Nobu"',                                  'DEBIT',     1),
(2, 'New Split Request', 'Mani Kumar requested ₹500 for "Goa Weekend Airbnb"',                 'REQUEST',   1),
(2, 'Successful Debit',  '₹500 debited for "Goa Weekend Airbnb"',                              'DEBIT',     1),
(2, 'New Split Request', 'Priya Patel requested ₹350 for "Office Pizza Party"',                 'REQUEST',   0),
(3, 'New Split Request', 'Mani Kumar requested ₹300 for "Dinner at Nobu"',                     'REQUEST',   1),
(3, 'Successful Debit',  '₹300 debited for "Dinner at Nobu"',                                  'DEBIT',     1),
(4, 'Top Up Successful', '₹2500 added to your SplitWallet balance',                            'TOP_UP',    1),
(4, 'Split Rejected',    'Rahul Sharma rejected your split request for "Office Pizza Party"',   'REJECTION', 0),
(5, 'New Split Request', 'Mani Kumar requested ₹2200 for "Goa Weekend Airbnb"',                'REQUEST',   0);
