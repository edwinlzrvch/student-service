-- Drop the existing trigger
DROP TRIGGER IF EXISTS update_enrollments_last_updated ON enrollments;

-- Create a new function specifically for enrollments last_updated column
CREATE OR REPLACE FUNCTION update_enrollments_last_updated_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.last_updated = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create the trigger for enrollments table
CREATE TRIGGER update_enrollments_last_updated
    BEFORE UPDATE ON enrollments
    FOR EACH ROW
    EXECUTE FUNCTION update_enrollments_last_updated_column();
