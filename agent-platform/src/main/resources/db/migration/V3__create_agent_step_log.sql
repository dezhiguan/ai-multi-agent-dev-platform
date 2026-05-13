CREATE TABLE agent_step_log (
                                id BIGSERIAL PRIMARY KEY,
                                task_id VARCHAR(64) NOT NULL,
                                step_name VARCHAR(64) NOT NULL,
                                status VARCHAR(32) NOT NULL,
                                log_content TEXT,
                                create_time TIMESTAMP DEFAULT NOW()
);

