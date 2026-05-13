CREATE TABLE agent_task (
                            id BIGSERIAL PRIMARY KEY,
                            task_id VARCHAR(64) NOT NULL UNIQUE,
                            prd_content TEXT,
                            status VARCHAR(32) NOT NULL DEFAULT 'INIT',
                            create_time TIMESTAMP NOT NULL DEFAULT NOW(),
                            update_time TIMESTAMP NOT NULL DEFAULT NOW()
);