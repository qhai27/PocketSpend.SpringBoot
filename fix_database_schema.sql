-- Fix Database Schema Issues for PocketSpend
-- Run this script in your MySQL database to fix foreign key constraint issues

USE pocketspend;

-- Drop existing foreign key constraints that are causing issues
ALTER TABLE budgets DROP FOREIGN KEY IF EXISTS budgets_ibfk_1;
ALTER TABLE expenses DROP FOREIGN KEY IF EXISTS expenses_ibfk_1;

-- Ensure all ID columns are BIGINT
ALTER TABLE users MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE budgets MODIFY COLUMN user_id BIGINT NOT NULL;
ALTER TABLE expenses MODIFY COLUMN user_id BIGINT NOT NULL;

-- Recreate foreign key constraints with proper types
ALTER TABLE budgets ADD CONSTRAINT budgets_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE expenses ADD CONSTRAINT expenses_ibfk_1 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Ensure Splitwise tables have proper foreign key constraints
ALTER TABLE group_members DROP FOREIGN KEY IF EXISTS group_members_ibfk_1;
ALTER TABLE split_expenses DROP FOREIGN KEY IF EXISTS split_expenses_ibfk_1;
ALTER TABLE split_expenses DROP FOREIGN KEY IF EXISTS split_expenses_ibfk_2;
ALTER TABLE expense_splits DROP FOREIGN KEY IF EXISTS expense_splits_ibfk_1;
ALTER TABLE expense_splits DROP FOREIGN KEY IF EXISTS expense_splits_ibfk_2;

-- Recreate Splitwise foreign key constraints
ALTER TABLE group_members ADD CONSTRAINT group_members_ibfk_1 FOREIGN KEY (group_id) REFERENCES splitwise_groups(id) ON DELETE CASCADE;
ALTER TABLE split_expenses ADD CONSTRAINT split_expenses_ibfk_1 FOREIGN KEY (group_id) REFERENCES splitwise_groups(id) ON DELETE CASCADE;
ALTER TABLE split_expenses ADD CONSTRAINT split_expenses_ibfk_2 FOREIGN KEY (paid_by_id) REFERENCES group_members(id) ON DELETE CASCADE;
ALTER TABLE expense_splits ADD CONSTRAINT expense_splits_ibfk_1 FOREIGN KEY (expense_id) REFERENCES split_expenses(id) ON DELETE CASCADE;
ALTER TABLE expense_splits ADD CONSTRAINT expense_splits_ibfk_2 FOREIGN KEY (member_id) REFERENCES group_members(id) ON DELETE CASCADE;

-- Verify the schema
SHOW TABLES;
DESCRIBE users;
DESCRIBE budgets;
DESCRIBE expenses;
DESCRIBE splitwise_groups;
DESCRIBE group_members;
DESCRIBE split_expenses;
DESCRIBE expense_splits; 