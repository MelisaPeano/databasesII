DROP TRIGGER IF EXISTS trg_task_no_duplicates_ins;
DELIMITER //
CREATE TRIGGER trg_task_no_duplicates_ins
    BEFORE INSERT ON `Task`
    FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM `Task`
        WHERE list_id = NEW.list_id
          AND LOWER(task_title) = LOWER(NEW.task_title)
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se permiten tareas duplicadas en la misma lista.';
    END IF;
END//
DELIMITER ;

DROP TRIGGER IF EXISTS trg_task_no_duplicates_upd;
DELIMITER //
CREATE TRIGGER trg_task_no_duplicates_upd
    BEFORE UPDATE ON `Task`
    FOR EACH ROW
BEGIN
    IF EXISTS (
        SELECT 1 FROM `Task` t
        WHERE t.list_id = NEW.list_id
          AND LOWER(t.task_title) = LOWER(NEW.task_title)
          AND t.task_id <> OLD.task_id
    ) THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se permiten tareas duplicadas en la misma lista.';
    END IF;
END//
DELIMITER ;
