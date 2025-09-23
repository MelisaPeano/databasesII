CREATE TABLE IF NOT EXISTS `AppUser` (
   user_id INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,
   user_name VARCHAR(100) NOT NULL,
    user_email VARCHAR(100) NOT NULL UNIQUE,
    user_password VARCHAR(100) NOT NULL,
    created_in DATETIME
);
CREATE TABLE IF NOT EXISTS Category (
    category_id INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,
    user_id INT NOT NULL,
    category_name VARCHAR(60) NOT NULL,
    category_color CHAR(7) NULL,
    created_in DATETIME,
    FOREIGN KEY (user_id) REFERENCES AppUser(user_id)
);

CREATE TABLE IF NOT EXISTS List (
    list_id INT NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    list_name VARCHAR(60) NOT NULL,
    list_description VARCHAR(255) NULL,
    created_in DATETIME,
    FOREIGN KEY (category_id) REFERENCES Category(category_id)
);
CREATE TABLE IF NOT EXISTS Task (
   task_id INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,
   list_id INT NOT NULL,
    task_title VARCHAR(100) NOT NULL,
    task_description TEXT NULL,
    expires_in DATETIME NULL,
    priority ENUM('LOW', 'HIGH', 'MIDDLE'),
    task_status ENUM('NEW', 'IN_PROGRESS', 'DONE', 'CANCELLED'),
    created_in DATETIME,
    completed_in DATETIME NULL,
    FOREIGN KEY (list_id) REFERENCES List(list_id)
);
CREATE TABLE IF NOT EXISTS Task_status (
    task_status_id INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,
    task_id INT NOT NULL,
    status ENUM('NEW', 'IN_PROGRESS', 'DONE', 'CANCELLED'),
    changed_in DATETIME,
    comment VARCHAR(100) NULL,
    FOREIGN KEY (task_id) REFERENCES Task(task_id)
);


-- ----------
-- Vista de Usuarios y sus Listas
-- ----------
CREATE VIEW UserLists AS
SELECT U.user_name AS User_Name, C.category_name AS Category, L.list_name AS List, L.created_in AS Created_In
FROM AppUser U
JOIN Category C ON U.user_id = C.user_id
JOIN List L ON C.category_id = L.category_id;

-- ----------
-- Vista de Usuarios y su numero de categorias
-- ----------
CREATE VIEW UserCategoryCount AS
SELECT U.user_id, U.user_name, COUNT(C.category_id) AS total_categories
FROM AppUser U
LEFT JOIN Category C ON U.user_id = C.user_id
GROUP BY U.user_id, U.user_name;

-- ----------
-- Vista de lista con numero de tareas
-- ----------
CREATE VIEW ListTaskCount AS
SELECT L.list_id, L.list_name, COUNT(T.task_id) AS total_tasks
FROM List L
LEFT JOIN Task T ON L.list_id = T.list_id
GROUP BY L.list_id, L.list_name;

-- ----------
-- Vista de tareas pendientes por usuario
-- ----------
CREATE VIEW PendingTasksByUser AS
SELECT U.user_name, T.task_title, T.task_status, T.expires_in
FROM AppUser U
JOIN Category C ON U.user_id = C.user_id
JOIN List L ON C.category_id = L.category_id
JOIN Task T ON L.list_id = T.list_id
WHERE T.task_status IN ('NEW', 'IN_PROGRESS');

-- ----------
-- Vista de tareas atrasadas
-- ----------
CREATE VIEW OverdueTasks AS
SELECT U.user_name, T.task_title, T.expires_in, T.task_status
FROM AppUser U
JOIN Category C ON U.user_id = C.user_id
JOIN List L ON C.category_id = L.category_id
JOIN Task T ON L.list_id = T.list_id
WHERE T.expires_in < NOW() AND T.task_status <> 'DONE';