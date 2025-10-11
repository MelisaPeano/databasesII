-- ----------
-- Indice para obtener solo el nombre de una tarea
-- ----------
CREATE INDEX task_name ON Task(task_title);

-- ----------
-- Indice para obtener el estado de una tarea
-- ----------
CREATE INDEX task_status ON Task(task_status);

-- ----------
-- Indice para obtener solo el nombre de una lista
-- ----------
CREATE INDEX list_name ON List(list_name);

-- ----------
-- Indice para obtener el nombre de categoria
-- ----------
CREATE INDEX name_category ON Category(category_name);

-- ----------
-- Indice para obtener el usuario
-- ----------
CREATE INDEX user_name ON AppUser(user_name);

-- ----------
-- Indice para obtener la fecha de expiración de una tarea
-- ----------
CREATE INDEX task_expiration ON Task(expires_in);

-- ----------
-- Indices para mejorar los futuros joins
-- ----------
CREATE INDEX id_task_list_id ON Task(list_id);
CREATE INDEX id_list_category_id ON List(category_id);
CREATE INDEX id_category_user_id ON Category(user_id);
CREATE INDEX id_refresh_token_user_id ON refresh_token(user_id);
CREATE INDEX id_task_created_in ON Task(created_in);
