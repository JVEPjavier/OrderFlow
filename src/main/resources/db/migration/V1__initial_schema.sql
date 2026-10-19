CREATE TABLE schema_version_note (
    id SMALLINT PRIMARY KEY,
    note VARCHAR(255) NOT NULL
);
INSERT INTO schema_version_note (id, note) VALUES (1, 'OrderFlow initialized');
