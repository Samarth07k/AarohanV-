-- Migrate status column in opportunities table from custom enum to VARCHAR to resolve Hibernate mapping mismatch
ALTER TABLE opportunities ALTER COLUMN status TYPE VARCHAR(50);
ALTER TABLE opportunities ALTER COLUMN status SET DEFAULT 'OPEN';
