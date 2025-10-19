-- Migration Script: Thêm các cột cho Email Verification
-- Database: PostgreSQL
-- Date: 2025

-- Kiểm tra và thêm cột email_verified
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'email_verified'
    ) THEN
        ALTER TABLE users ADD COLUMN email_verified BOOLEAN DEFAULT FALSE NOT NULL;
        COMMENT ON COLUMN users.email_verified IS 'Trạng thái xác thực email của user';
    END IF;
END $$;

-- Kiểm tra và thêm cột verification_token
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'verification_token'
    ) THEN
        ALTER TABLE users ADD COLUMN verification_token VARCHAR(500);
        COMMENT ON COLUMN users.verification_token IS 'Token JWT để xác thực email';
    END IF;
END $$;

-- Kiểm tra và thêm cột verification_token_expiry
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'users' AND column_name = 'verification_token_expiry'
    ) THEN
        ALTER TABLE users ADD COLUMN verification_token_expiry TIMESTAMP;
        COMMENT ON COLUMN users.verification_token_expiry IS 'Thời gian hết hạn của verification token';
    END IF;
END $$;

-- Tạo index cho verification_token để tăng tốc độ tìm kiếm
CREATE INDEX IF NOT EXISTS idx_users_verification_token 
ON users(verification_token) 
WHERE verification_token IS NOT NULL;

-- Cập nhật tất cả user hiện tại có email_verified = true (nếu muốn)
-- Uncomment dòng dưới nếu muốn tất cả user cũ được tự động verify
-- UPDATE users SET email_verified = TRUE WHERE email_verified IS NULL OR email_verified = FALSE;

-- Kiểm tra kết quả
SELECT 
    column_name, 
    data_type, 
    is_nullable, 
    column_default
FROM information_schema.columns
WHERE table_name = 'users' 
AND column_name IN ('email_verified', 'verification_token', 'verification_token_expiry')
ORDER BY ordinal_position;
