-- ============================================================
-- SplitWallet MySQL Database Schema
-- Virtual Wallet Ledger Prototype
-- ============================================================

-- Drop existing tables (order matters due to foreign keys)
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS split_participants;
DROP TABLE IF EXISTS expense_splits;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS wallets;
DROP TABLE IF EXISTS users;

-- ============================================================
-- TABLE 1: users
-- ============================================================
CREATE TABLE users (
    id           BIGINT          NOT NULL AUTO_INCREMENT,
    full_name    VARCHAR(100)    NOT NULL,
    email        VARCHAR(150)    NOT NULL,
    mobile       VARCHAR(20)     NOT NULL,
    password     VARCHAR(255)    NOT NULL,  -- BCrypt hashed
    role         VARCHAR(30)     NOT NULL DEFAULT 'ROLE_USER',
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for fast lookup
CREATE INDEX idx_users_email ON users (email);

-- ============================================================
-- TABLE 2: wallets
-- ============================================================
CREATE TABLE wallets (
    id           BIGINT          NOT NULL AUTO_INCREMENT,
    user_id      BIGINT          NOT NULL,
    balance      DECIMAL(12, 2)  NOT NULL DEFAULT 0.00,
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_wallets_user_id (user_id),
    CONSTRAINT fk_wallets_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_wallets_balance CHECK (balance >= 0.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_wallets_user_id ON wallets (user_id);

-- ============================================================
-- TABLE 3: transactions
-- ============================================================
CREATE TABLE transactions (
    id               BIGINT          NOT NULL AUTO_INCREMENT,
    user_id          BIGINT          NOT NULL,
    transaction_type ENUM('TOP_UP', 'SPLIT_DEBIT', 'SPLIT_CREDIT')
                                     NOT NULL,
    amount           DECIMAL(12, 2)  NOT NULL,
    balance_after    DECIMAL(12, 2)  NOT NULL,
    description      VARCHAR(500)    NOT NULL,
    reference_id     VARCHAR(60)     NOT NULL,
    status           ENUM('COMPLETED', 'FAILED', 'PENDING')
                                     NOT NULL DEFAULT 'COMPLETED',
    created_at       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_transactions_amount CHECK (amount > 0.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_transactions_user_id   ON transactions (user_id);
CREATE INDEX idx_transactions_type      ON transactions (transaction_type);
CREATE INDEX idx_transactions_ref       ON transactions (reference_id);
CREATE INDEX idx_transactions_created   ON transactions (created_at DESC);

-- ============================================================
-- TABLE 4: expense_splits
-- ============================================================
CREATE TABLE expense_splits (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    creator_id    BIGINT          NOT NULL,
    title         VARCHAR(200)    NOT NULL,
    total_amount  DECIMAL(12, 2)  NOT NULL,
    description   VARCHAR(500)    NULL,
    status        ENUM('ACTIVE', 'COMPLETED', 'CANCELLED')
                                  NOT NULL DEFAULT 'ACTIVE',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_expense_splits_creator
        FOREIGN KEY (creator_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_expense_splits_total CHECK (total_amount > 0.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_expense_splits_creator ON expense_splits (creator_id);
CREATE INDEX idx_expense_splits_status  ON expense_splits (status);
CREATE INDEX idx_expense_splits_created ON expense_splits (created_at DESC);

-- ============================================================
-- TABLE 5: split_participants
-- ============================================================
CREATE TABLE split_participants (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    split_id        BIGINT          NOT NULL,
    user_id         BIGINT          NOT NULL,
    amount          DECIMAL(12, 2)  NOT NULL,
    approval_status ENUM('PENDING', 'APPROVED', 'REJECTED', 'DEBITED')
                                    NOT NULL DEFAULT 'PENDING',
    debited         TINYINT(1)      NOT NULL DEFAULT 0,
    approved_at     DATETIME        NULL,
    rejected_at     DATETIME        NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uq_split_participants (split_id, user_id),
    CONSTRAINT fk_split_participants_split
        FOREIGN KEY (split_id) REFERENCES expense_splits (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_split_participants_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_split_participants_amount CHECK (amount > 0.00)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_split_participants_split_id ON split_participants (split_id);
CREATE INDEX idx_split_participants_user_id  ON split_participants (user_id);
CREATE INDEX idx_split_participants_status   ON split_participants (approval_status);

-- ============================================================
-- TABLE 6: notifications
-- ============================================================
CREATE TABLE notifications (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    user_id     BIGINT          NOT NULL,
    title       VARCHAR(200)    NOT NULL,
    message     VARCHAR(500)    NOT NULL,
    type        ENUM('REQUEST', 'APPROVAL', 'REJECTION', 'TOP_UP', 'DEBIT')
                                NOT NULL,
    is_read     TINYINT(1)      NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user
        FOREIGN KEY (user_id) REFERENCES users (id)
        ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_notifications_user_id  ON notifications (user_id);
CREATE INDEX idx_notifications_is_read  ON notifications (user_id, is_read);
CREATE INDEX idx_notifications_created  ON notifications (created_at DESC);
