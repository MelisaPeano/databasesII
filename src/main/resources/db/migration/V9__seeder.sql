-- Poblar AppUser
INSERT INTO AppUser (user_name, user_email, user_password, created_in)
VALUES
    ('Admin', 'admin@example.com', '$2a$10$abcdefghijklmnopqrstuO5QZ7Nq6aF5bC3dE1fG2hI4jK6lM8nO0pQ2', NOW());

-- Poblar Category
INSERT INTO Category (user_id, category_name, category_color, created_in, is_default)
VALUES
    (1, 'Trabajo', '#FF5733', NOW(), TRUE),
    (1, 'Personal', '#33FF57', NOW(), FALSE),
    (1, 'Estudio', '#3357FF', NOW(), TRUE),
    (1, 'Hogar', '#F1C40F', NOW(), FALSE),
    (1, 'Fitness', '#9B59B6', NOW(), FALSE);

-- Poblar ListTable (👈 actualizado)
INSERT INTO ListTable (category_id, list_name, list_description, created_in)
VALUES
    (1, 'Backend', 'Tareas del backend', NOW()),
    (1, 'Frontend', 'Interfaz del proyecto', NOW()),
    (2, 'Compras', 'Lista del super', NOW()),
    (3, 'Exámenes', 'Preparación de exámenes', NOW()),
    (4, 'Rutina', 'Plan de ejercicios', NOW());

-- Poblar Task
INSERT INTO Task (list_id, task_title, task_description, expires_in, priority, task_status, created_in)
VALUES
    (1, 'Configurar base de datos', 'Revisar conexiones JDBC', DATE_ADD(NOW(), INTERVAL 5 DAY), 'HIGH', 'NEW', NOW()),
    (2, 'Diseñar UI', 'Pantallas principales', DATE_ADD(NOW(), INTERVAL 10 DAY), 'MIDDLE', 'IN_PROGRESS', NOW()),
    (3, 'Comprar frutas', 'Ir al supermercado', DATE_ADD(NOW(), INTERVAL 2 DAY), 'LOW', 'NEW', NOW()),
    (4, 'Estudiar SQL', 'Practicar subconsultas', DATE_ADD(NOW(), INTERVAL 7 DAY), 'HIGH', 'DONE', NOW()),
    (5, 'Entrenamiento piernas', 'Sesión completa', DATE_ADD(NOW(), INTERVAL 3 DAY), 'MIDDLE', 'IN_PROGRESS', NOW());

-- Poblar Task_status
INSERT INTO Task_status (task_id, status, changed_in, comment)
VALUES
    (1, 'NEW', NOW(), 'Pendiente de iniciar'),
    (2, 'IN_PROGRESS', NOW(), 'UI en progreso'),
    (3, 'NEW', NOW(), 'Aún no compradas'),
    (4, 'DONE', NOW(), 'Completado con éxito'),
    (5, 'IN_PROGRESS', NOW(), 'Ejercicios en marcha');

-- Poblar refresh_token
INSERT INTO refresh_token (token, expiry_date, user_id)
VALUES
    ('token1', DATE_ADD(NOW(), INTERVAL 30 DAY), 1);
