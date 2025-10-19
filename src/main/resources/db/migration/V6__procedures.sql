DELIMITER $$

/* USUARIOS */
DROP PROCEDURE IF EXISTS sp_create_user $$
CREATE PROCEDURE sp_create_user(
    IN  p_user_name   VARCHAR(100),
    IN  p_user_email  VARCHAR(100),
    IN  p_user_pass   VARCHAR(100),
    OUT p_user_id     INT
)
BEGIN
    DECLARE v_email VARCHAR(100);
    SET v_email = fn_email_canonical(p_user_email);

    IF EXISTS (SELECT 1 FROM AppUser WHERE user_email = v_email) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Email ya registrado';
    END IF;

    INSERT INTO AppUser(user_name, user_email, user_password)
    VALUES(p_user_name, v_email, p_user_pass);

    SET p_user_id = LAST_INSERT_ID();
END $$


/* CATEGORÍAS */
DROP PROCEDURE IF EXISTS sp_create_category $$
CREATE PROCEDURE sp_create_category(
    IN  p_user_id INT,
    IN  p_name    VARCHAR(60),
    IN  p_color   CHAR(7),
    OUT p_category_id INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM AppUser WHERE user_id = p_user_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Usuario inexistente';
    END IF;

    IF p_color IS NULL OR LENGTH(p_color) = 0 THEN
        SET p_color = fn_default_color();
    END IF;

    IF EXISTS (
        SELECT 1 FROM Category WHERE user_id = p_user_id AND LOWER(category_name) = LOWER(p_name)
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Categoria duplicada para el usuario';
    END IF;

    INSERT INTO Category(user_id, category_name, category_color)
    VALUES(p_user_id, p_name, p_color);

    SET p_category_id = LAST_INSERT_ID();
END $$


/* LISTAS */
DROP PROCEDURE IF EXISTS sp_create_list $$
CREATE PROCEDURE sp_create_list(
    IN  p_category_id INT,
    IN  p_name        VARCHAR(60),
    IN  p_desc        VARCHAR(255),
    OUT p_list_id     INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `Category` WHERE category_id = p_category_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Categoria inexistente';
    END IF;

    IF EXISTS (
        SELECT 1 FROM `ListTable` WHERE category_id = p_category_id AND LOWER(list_name) = LOWER(p_name)
    ) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Lista duplicada en la categoría';
    END IF;

    INSERT INTO `ListTable`(category_id, list_name, list_description)
    VALUES(p_category_id, p_name, p_desc);

    SET p_list_id = LAST_INSERT_ID();
END $$


/* TAREAS CRUD */
DROP PROCEDURE IF EXISTS sp_create_task $$
CREATE PROCEDURE sp_create_task(
    IN  p_list_id   INT,
    IN  p_title     VARCHAR(100),
    IN  p_desc      TEXT,
    IN  p_expires   DATETIME,
    IN  p_priority  VARCHAR(10),
    OUT p_task_id   INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `ListTable` WHERE list_id = p_list_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Lista inexistente';
    END IF;

    SET p_priority = UPPER(IFNULL(p_priority,'LOW'));
    IF p_priority NOT IN ('LOW','MIDDLE','HIGH') THEN
        SET p_priority = 'LOW';
    END IF;

    IF p_expires IS NOT NULL AND p_expires < NOW() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha de expiración no puede ser pasada';
    END IF;

    INSERT INTO `Task`(list_id, task_title, task_description, expires_in, priority, task_status)
    VALUES(p_list_id, p_title, p_desc, p_expires, p_priority, 'NEW');

    SET p_task_id = LAST_INSERT_ID();
END $$

DROP PROCEDURE IF EXISTS sp_update_task $$
CREATE PROCEDURE sp_update_task(
    IN p_task_id   INT,
    IN p_title     VARCHAR(100),
    IN p_desc      TEXT,
    IN p_expires   DATETIME,
    IN p_priority  VARCHAR(10)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `Task` WHERE task_id = p_task_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tarea inexistente';
    END IF;

    IF p_expires IS NOT NULL AND p_expires < NOW() THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La fecha de expiración no puede ser pasada';
    END IF;

    IF p_priority IS NOT NULL THEN
        SET p_priority = UPPER(p_priority);
        IF p_priority NOT IN ('LOW','MIDDLE','HIGH') THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Prioridad inválida';
        END IF;
    END IF;

    UPDATE `Task` t
    SET
        t.task_title       = COALESCE(p_title, t.task_title),
        t.task_description = COALESCE(p_desc,  t.task_description),
        t.expires_in       = COALESCE(p_expires, t.expires_in),
        t.priority         = COALESCE(p_priority, t.priority)
    WHERE t.task_id = p_task_id;
END $$


/* TAREAS (tanto estado como movimiento) */
DROP PROCEDURE IF EXISTS sp_change_task_status $$
CREATE PROCEDURE sp_change_task_status(
    IN p_task_id INT,
    IN p_new_status VARCHAR(15),
    IN p_comment VARCHAR(100)
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM `Task` WHERE task_id = p_task_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tarea inexistente';
    END IF;

    SET p_new_status = UPPER(p_new_status);
    IF p_new_status NOT IN ('NEW','IN_PROGRESS','DONE','CANCELLED') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Estado inválido';
    END IF;

    START TRANSACTION;

    UPDATE `Task`
    SET task_status = p_new_status
    WHERE task_id = p_task_id;

    IF p_comment IS NOT NULL AND CHAR_LENGTH(p_comment) > 0 THEN
        UPDATE `Task_status`
        SET comment = p_comment
        WHERE task_id = p_task_id
        ORDER BY changed_in DESC, task_status_id DESC
        LIMIT 1;
    END IF;

    COMMIT;
END $$

DROP PROCEDURE IF EXISTS sp_move_task $$
CREATE PROCEDURE sp_move_task(
    IN p_task_id INT,
    IN p_target_list_id INT
)
BEGIN
    DECLARE v_src_list INT;
    DECLARE v_src_user INT;
    DECLARE v_dst_user INT;

    SELECT list_id INTO v_src_list FROM `Task` WHERE task_id = p_task_id;
    IF v_src_list IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Tarea inexistente';
    END IF;

    IF NOT EXISTS (SELECT 1 FROM `ListTable` WHERE list_id = p_target_list_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Lista destino inexistente';
    END IF;

    SET v_src_user = fn_list_owner_user_id(v_src_list);
    SET v_dst_user = fn_list_owner_user_id(p_target_list_id);

    IF v_src_user IS NULL OR v_dst_user IS NULL OR v_src_user <> v_dst_user THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Las listas no pertenecen al mismo usuario';
    END IF;

    UPDATE `Task` SET list_id = p_target_list_id WHERE task_id = p_task_id;
END $$


/* BORRADOS SEGUROS */
DROP PROCEDURE IF EXISTS sp_delete_task $$
CREATE PROCEDURE sp_delete_task(IN p_task_id INT)
BEGIN
    START TRANSACTION;
    DELETE FROM `Task_status` WHERE task_id = p_task_id;
    DELETE FROM `Task` WHERE task_id = p_task_id;
    COMMIT;
END $$

DROP PROCEDURE IF EXISTS sp_delete_list $$
CREATE PROCEDURE sp_delete_list(IN p_list_id INT)
BEGIN
    START TRANSACTION;
    DELETE ts FROM `Task_status` ts
                       JOIN `Task` t ON t.task_id = ts.task_id
    WHERE t.list_id = p_list_id;

    DELETE FROM `Task` WHERE list_id = p_list_id;
    DELETE FROM `ListTable` WHERE list_id = p_list_id;
    COMMIT;
END $$

DROP PROCEDURE IF EXISTS sp_delete_category $$
CREATE PROCEDURE sp_delete_category(IN p_category_id INT)
BEGIN
    START TRANSACTION;
    DELETE ts FROM `Task_status` ts
                       JOIN `Task` t ON t.task_id = ts.task_id
                       JOIN `ListTable` l ON l.list_id = t.list_id
    WHERE l.category_id = p_category_id;

    DELETE t FROM `Task` t
                      JOIN `ListTable` l ON l.list_id = t.list_id
    WHERE l.category_id = p_category_id;

    DELETE FROM `ListTable` WHERE category_id = p_category_id;
    DELETE FROM `Category` WHERE category_id = p_category_id;
    COMMIT;
END $$


/* LISTADOS / REPORTES */
DROP PROCEDURE IF EXISTS sp_list_tasks $$
CREATE PROCEDURE sp_list_tasks(
    IN p_user_id     INT,
    IN p_category_id INT,
    IN p_list_id     INT,
    IN p_status      VARCHAR(15),
    IN p_priority    VARCHAR(10),
    IN p_due_from    DATETIME,
    IN p_due_to      DATETIME,
    IN p_query       VARCHAR(100)
)
BEGIN
    SELECT
        t.task_id, t.task_title, t.task_description,
        t.expires_in, t.priority, t.task_status,
        fn_priority_order(t.priority) AS priority_order,
        fn_status_order(t.task_status) AS status_order,
        fn_is_overdue_by_dates(t.expires_in, t.task_status) AS is_overdue,
        t.created_in, t.completed_in,
        l.list_id, l.list_name,
        c.category_id, c.category_name
    FROM `Task` t
             JOIN `ListTable` l ON l.list_id = t.list_id
             JOIN `Category` c ON c.category_id = l.category_id
    WHERE c.user_id = p_user_id
      AND (p_category_id IS NULL OR c.category_id = p_category_id)
      AND (p_list_id     IS NULL OR l.list_id     = p_list_id)
      AND (p_status      IS NULL OR UPPER(t.task_status) = UPPER(p_status))
      AND (p_priority    IS NULL OR UPPER(t.priority)    = UPPER(p_priority))
      AND (p_due_from    IS NULL OR t.expires_in >= p_due_from)
      AND (p_due_to      IS NULL OR t.expires_in <  p_due_to)
      AND (p_query       IS NULL OR
           t.task_title LIKE CONCAT('%', p_query, '%')
        OR t.task_description LIKE CONCAT('%', p_query, '%'))
    ORDER BY
        is_overdue DESC,
        CASE
            WHEN t.expires_in IS NULL THEN CAST('2999-12-31 00:00:00' AS DATETIME)
            ELSE t.expires_in
            END ASC,
        fn_priority_order(t.priority) DESC,
        t.created_in DESC;
END $$

DROP PROCEDURE IF EXISTS sp_get_dashboard_counts $$
CREATE PROCEDURE sp_get_dashboard_counts(IN p_user_id INT)
BEGIN
    SELECT
        COUNT(*)                                                   AS total,
        SUM(t.task_status = 'NEW')                                 AS cnt_new,
        SUM(t.task_status = 'IN_PROGRESS')                         AS cnt_in_progress,
        SUM(t.task_status = 'DONE')                                AS cnt_done,
        SUM(t.task_status = 'CANCELLED')                           AS cnt_cancelled,
        SUM(fn_is_overdue_by_dates(t.expires_in, t.task_status))   AS cnt_overdue,
        SUM(DATE(t.expires_in) = CURDATE() AND t.task_status <> 'DONE') AS cnt_due_today
    FROM `Task` t
             JOIN `ListTable` l ON l.list_id = t.list_id
             JOIN `Category` c ON c.category_id = l.category_id
    WHERE c.user_id = p_user_id;
END $$

DROP PROCEDURE IF EXISTS sp_tasks_next_due $$
CREATE PROCEDURE sp_tasks_next_due(
    IN p_user_id INT,
    IN p_limit   INT
)
BEGIN
    SET p_limit = IFNULL(p_limit, 10);

    SELECT
        t.task_id, t.task_title, t.expires_in, t.priority, t.task_status,
        l.list_name, c.category_name
    FROM `Task` t
             JOIN `ListTable` l ON l.list_id = t.list_id
             JOIN `Category` c ON c.category_id = l.category_id
    WHERE c.user_id = p_user_id
      AND t.task_status <> 'DONE'
      AND t.expires_in IS NOT NULL
    ORDER BY t.expires_in ASC
    LIMIT p_limit;
END $$

DELIMITER ;
