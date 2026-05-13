CREATE TABLE knowledge_document (
                                    id BIGSERIAL PRIMARY KEY,
                                    file_name VARCHAR(255) NOT NULL,
                                    file_type VARCHAR(32),
                                    content TEXT,
                                    chunk_num INT,
                                    status VARCHAR(32) DEFAULT 'UPLOADED',
                                    create_time TIMESTAMP DEFAULT NOW()
);