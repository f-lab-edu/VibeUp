CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,  -- 자동 증가 PK
    email VARCHAR(100) NOT NULL UNIQUE,   -- 이메일 (Unique)
    nickname VARCHAR(50) NOT NULL UNIQUE,  -- 닉네임 (Unique)
    password VARCHAR(255) NOT NULL,    -- 비밀번호
    name VARCHAR(50), -- 이름 (옵션)
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 생성일시
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP -- 수정일시
);