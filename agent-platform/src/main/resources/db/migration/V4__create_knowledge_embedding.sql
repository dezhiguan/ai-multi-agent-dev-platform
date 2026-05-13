CREATE TABLE knowledge_embedding (
                                     id BIGSERIAL PRIMARY KEY,
                                     task_id VARCHAR(64),
                                     content TEXT,
                                     embedding vector(1536),
                                     create_time TIMESTAMP DEFAULT NOW()
);