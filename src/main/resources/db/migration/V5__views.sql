-- ----------
-- Vista de usuarios y sus listas
-- ----------
CREATE VIEW UserLists AS
SELECT U.user_name AS User_Name, C.category_name AS Category, L.list_name AS List, L.created_in AS Created_In
FROM AppUser U
JOIN Category C ON U.user_id = C.user_id
JOIN List L ON C.category_id = L.category_id;

-- ----------
-- Vista de usuarios y su numero de categorias
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