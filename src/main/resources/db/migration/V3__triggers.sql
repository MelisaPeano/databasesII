-- Trigger 1: created_in para AppUser
CREATE TRIGGER set_created_in_user
    BEFORE INSERT ON AppUser
    FOR EACH ROW
    SET NEW.created_in = NOW();

-- Trigger 2: created_in para Task
CREATE TRIGGER set_created_in_task
    BEFORE INSERT ON Task
    FOR EACH ROW
    SET NEW.created_in = NOW();

-- Trigger 3: created_in para Category
CREATE TRIGGER set_created_in_category
    BEFORE INSERT ON Category
    FOR EACH ROW
    SET NEW.created_in = NOW();

-- Trigger 4: created_in para List
CREATE TRIGGER set_created_in_list
    BEFORE INSERT ON ListTable
    FOR EACH ROW
    SET NEW.created_in = NOW();

-- Trigger 5: completed_in para tareas completadas
CREATE TRIGGER set_completed_in_task
    BEFORE UPDATE ON Task
    FOR EACH ROW
BEGIN
    IF NEW.task_status = 'DONE' AND OLD.task_status <> 'DONE' THEN
        SET NEW.completed_in = NOW();
    END IF;
END;

-- Trigger 6: registrar cambios de estado
CREATE TRIGGER log_task_status
    AFTER UPDATE ON Task
    FOR EACH ROW
BEGIN
    IF NEW.task_status <> OLD.task_status THEN
        INSERT INTO Task_status (task_id, status, changed_in, comment)
        VALUES (NEW.task_id, NEW.task_status, NOW(), 'Cambio automático por trigger');
    END IF;
END;

-- Trigger 7: eliminar listas con categoría
CREATE TRIGGER delete_lists_with_category
    AFTER DELETE ON Category
    FOR EACH ROW
BEGIN
    DELETE FROM ListTable WHERE category_id = OLD.category_id;
END;

-- Trigger 8: validar fecha de expiración
CREATE TRIGGER validate_task_expiration
    BEFORE INSERT ON Task
    FOR EACH ROW
BEGIN
    IF NEW.expires_in IS NOT NULL AND NEW.expires_in < NOW() THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La fecha de expiración no puede ser pasada.';
    END IF;
END;

-- Trigger 8: Crear la categoria por default --
CREATE TRIGGER after_user_insert
    AFTER INSERT ON AppUser
    FOR EACH ROW
BEGIN
    INSERT INTO Category (user_id, category_name, category_color, created_in, is_default)
    VALUES (NEW.user_id, 'General', '#808080', NOW(), TRUE);
END;