-- Drop and recreate the database
DROP DATABASE IF EXISTS `pocketspend`;
CREATE DATABASE `pocketspend`;
USE `pocketspend`;

-- Users Table
CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Budgets Table
CREATE TABLE `budgets` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `total_budget` DECIMAL(10,2) DEFAULT '0.00',
  `total_expenses` DECIMAL(10,2) DEFAULT '0.00',
  `budget_left` DECIMAL(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `budgets_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Expenses Table (✅ FIXED: added user_id foreign key)
CREATE TABLE `expenses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `date` DATE NOT NULL,
  `description` TEXT,
  `receipt_image_data` LONGBLOB,
  `receipt_image_type` VARCHAR(100),
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `expenses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Splitwise Tables
-- Groups Table
CREATE TABLE `splitwise_groups` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Group Members Table
CREATE TABLE `group_members` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `group_id` (`group_id`),
  CONSTRAINT `group_members_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `splitwise_groups` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Split Expenses Table
CREATE TABLE `split_expenses` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `group_id` BIGINT NOT NULL,
  `description` VARCHAR(255) NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `paid_by_id` BIGINT NOT NULL,
  `split_type` ENUM('EQUAL', 'PERCENTAGE', 'CUSTOM') NOT NULL,
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `group_id` (`group_id`),
  KEY `paid_by_id` (`paid_by_id`),
  CONSTRAINT `split_expenses_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `splitwise_groups` (`id`) ON DELETE CASCADE,
  CONSTRAINT `split_expenses_ibfk_2` FOREIGN KEY (`paid_by_id`) REFERENCES `group_members` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Expense Splits Table
CREATE TABLE `expense_splits` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `expense_id` BIGINT NOT NULL,
  `member_id` BIGINT NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `expense_id` (`expense_id`),
  KEY `member_id` (`member_id`),
  CONSTRAINT `expense_splits_ibfk_1` FOREIGN KEY (`expense_id`) REFERENCES `split_expenses` (`id`) ON DELETE CASCADE,
  CONSTRAINT `expense_splits_ibfk_2` FOREIGN KEY (`member_id`) REFERENCES `group_members` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

ALTER TABLE split_expenses MODIFY COLUMN paid_by_id BIGINT NOT NULL;
ALTER TABLE split_expenses ADD CONSTRAINT fk_paid_by FOREIGN KEY (paid_by_id) REFERENCES group_members(id) ON DELETE CASCADE;

ALTER TABLE budgets DROP FOREIGN KEY budgets_ibfk_1;
ALTER TABLE budgets ADD CONSTRAINT budgets_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE expenses MODIFY COLUMN user_id BIGINT NOT NULL;
