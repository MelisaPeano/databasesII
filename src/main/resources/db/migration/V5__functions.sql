DELIMITER $$

DROP FUNCTION IF EXISTS fn_email_canonical $$
CREATE FUNCTION fn_email_canonical(p_email VARCHAR(255))
    RETURNS VARCHAR(255)
    DETERMINISTIC
    RETURN LOWER(TRIM(p_email)) $$

DROP FUNCTION IF EXISTS fn_default_color $$
CREATE FUNCTION fn_default_color()
    RETURNS CHAR(7)
    DETERMINISTIC
    RETURN '#999999' $$

DROP FUNCTION IF EXISTS fn_priority_order $$
CREATE FUNCTION fn_priority_order(p_priority VARCHAR(10))
    RETURNS TINYINT
    DETERMINISTIC
BEGIN
    RETURN CASE UPPER(p_priority)
               WHEN 'LOW'    THEN 1
               WHEN 'MIDDLE' THEN 2
               WHEN 'HIGH'   THEN 3
               ELSE NULL
        END;
END $$

DROP FUNCTION IF EXISTS fn_status_order $$
CREATE FUNCTION fn_status_order(p_status VARCHAR(15))
    RETURNS TINYINT
    DETERMINISTIC
BEGIN
    RETURN CASE UPPER(p_status)
               WHEN 'NEW'         THEN 1
               WHEN 'IN_PROGRESS' THEN 2
               WHEN 'DONE'        THEN 3
               WHEN 'CANCELLED'   THEN 4
               ELSE NULL
        END;
END $$

DROP FUNCTION IF EXISTS fn_is_overdue_by_dates $$
CREATE FUNCTION fn_is_overdue_by_dates(p_expires DATETIME, p_status VARCHAR(15))
    RETURNS TINYINT
    NOT DETERMINISTIC
BEGIN
    RETURN (p_expires IS NOT NULL AND p_expires < NOW() AND UPPER(p_status) <> 'DONE');
END $$

DROP FUNCTION IF EXISTS fn_is_overdue_by_id $$
CREATE FUNCTION fn_is_overdue_by_id(p_task_id INT)
    RETURNS TINYINT
    READS SQL DATA
    NOT DETERMINISTIC
BEGIN
    DECLARE v_expires DATETIME;
    DECLARE v_status  VARCHAR(15);
    SELECT expires_in, task_status INTO v_expires, v_status
    FROM `Task` WHERE task_id = p_task_id;
    RETURN fn_is_overdue_by_dates(v_expires, v_status);
END $$

DROP FUNCTION IF EXISTS fn_list_owner_user_id $$
CREATE FUNCTION fn_list_owner_user_id(p_list_id INT)
    RETURNS INT
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_user_id INT;
    SELECT c.user_id INTO v_user_id
    FROM `List` l
             JOIN `Category` c ON c.category_id = l.category_id
    WHERE l.list_id = p_list_id;
    RETURN v_user_id;
END $$

DELIMITER ;