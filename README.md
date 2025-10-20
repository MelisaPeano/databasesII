## Manual de Usuario Técnico
## 1. Inicio de Sesión

Al iniciar la aplicación, se muestra la pantalla de Inicio de Sesión, donde el usuario debe ingresar sus credenciales:

Email: dirección registrada en el sistema.

Contraseña: clave asociada al usuario.

## Botones disponibles:

Iniciar Sesión: valida las credenciales e ingresa al panel principal.

Regístrate aquí: redirige al formulario de creación de cuenta.

Si el login es exitoso, se carga el panel principal con el Dashboard.
## 2. Dashboard Principal

El Dashboard es el punto central de navegación.
Contiene un menú lateral con dos secciones principales:

Categorías: permite gestionar las categorías de listas del usuario.

Mis Tareas: muestra las listas y tareas asignadas.

En la parte superior derecha se visualiza el nombre del usuario activo (Admin) y el botón Logout, que cierra la sesión.

## 3. Gestión de Categorías

Desde la sección Categorías, el usuario puede visualizar una tabla con las categorías existentes.
Cada fila muestra:

Campo:	Descripción
Nombre:	Nombre de la categoría
Color	Código hexadecimal del color asignado
Fecha de Creación	Fecha en formato ISO de creación
Número de Listas	Cantidad de listas asociadas

Botones disponibles:

Nueva Categoría: abre un formulario modal para crear una nueva.

Editar: permite modificar el nombre o color de una categoría seleccionada.

Eliminar: borra la categoría seleccionada (si no tiene dependencias activas).

## 4. Creación de Categorías

Al presionar Nueva Categoría, se abre una ventana modal con los campos:

Nombre: nombre descriptivo de la categoría (obligatorio).

Color: color en formato hexadecimal (opcional).

Botones:

Guardar: confirma la creación de la categoría.

Cancelar: cierra el formulario sin guardar.

## 5. Listas dentro de una Categoría

Dentro de cada categoría, el usuario puede crear y gestionar listas personalizadas.

Botones disponibles:

Volver: regresa al panel anterior de categorías.

Nueva Lista: abre un cuadro de diálogo que solicita el nombre de la nueva lista.

Las listas se muestran en una tabla con columnas de nombre, fecha de creación y acciones disponibles.

## 6. Cierre de Sesión

Al presionar Logout, el sistema muestra un mensaje de agradecimiento con los nombres de los desarrolladores:

“Gracias por usar nuestra app de listas
Creadores: Ira Frias, Antonio Blinda y Melisa Peano”

Luego de confirmar con OK, la aplicación se cierra automáticamente.

Flujo General de Uso

El usuario ingresa sus credenciales en la pantalla de login.

Accede al Dashboard principal.

Gestiona sus Categorías y Listas.

Crea nuevas categorías o listas según necesidad.

Cierra sesión desde el botón Logout, lo que finaliza la ejecución del sistema.

Entorno de Ejecución

Lenguaje: Java

Framework: JavaFX

Base de Datos: MySQL

Arquitectura: MVC

Versión: 1.0.0

Sistema: “Sistema de Gestión de Tareas”
# Triggers de Base de Datos

Esta aplicación usa **triggers** para mantener integridad, auditar cambios y aplicar reglas de negocio directamente en la capa de datos. A continuación se documentan **qué hace cada trigger**, **cuándo se dispara**, **qué valida**, y **cómo probarlo**.

## Convenciones

* **Prefijo**: `set_`, `validate_`, `tracking_`, `prevent_`, `after_` describen la intención.
* **Timing**: `BEFORE` para normalizar/validar datos; `AFTER` para auditoría/historial.
* **Errores**: validaciones usan `SIGNAL SQLSTATE '45000'` con mensajes claros.

## Resumen rápido

| Trigger                   | Tabla      | Momento | Evento        | Objetivo                                                               |
| ------------------------- | ---------- | ------- | ------------- | ---------------------------------------------------------------------- |
| `set_created_in_user`     | `AppUser`  | BEFORE  | INSERT        | Settea `created_in = NOW()`                                            |
| `set_created_in_task`     | `Task`     | BEFORE  | INSERT        | Settea `created_in = NOW()`                                            |
| `set_created_in_category` | `Category` | BEFORE  | INSERT        | Settea `created_in = NOW()`                                            |
| `prevent_past_expires_in` | `Task`     | BEFORE  | INSERT/UPDATE | Impide fechas de vencimiento en pasado                                 |
| `validate_task_dates`     | `Task`     | BEFORE  | INSERT/UPDATE | Consistencia entre `created_in` y `expires_in`                         |
| `validate_task_status`    | `Task`     | BEFORE  | UPDATE        | Valida transición de estado (p. ej., `DONE` no vuelve a `IN_PROGRESS`) |
| `tracking_task_status`    | `Task`     | AFTER   | UPDATE        | Registra cambios de estado en `Task_status` (historial)                |
| `after_user_insert`       | `AppUser`  | AFTER   | INSERT        | Provisiona datos por defecto (p. ej., categoría/lista inicial)         |

> Nota: si en tu dump aparece alguno con nombre levemente distinto, se mantiene la **intención** descrita aquí.

---

## Detalle por trigger

### `set_created_in_user` (AppUser, BEFORE INSERT)

**Propósito:** timestamp automático de creación de usuario.
**Definición (esqueleto):**

```sql
DROP TRIGGER IF EXISTS set_created_in_user;
DELIMITER $$
CREATE TRIGGER set_created_in_user
BEFORE INSERT ON AppUser
FOR EACH ROW
BEGIN
  SET NEW.created_in = NOW();
END$$
DELIMITER ;
```

**Prueba:**

```sql
INSERT INTO AppUser(user_email,user_name,user_password) VALUES('test@example.com','Test','hash');
SELECT created_in FROM AppUser WHERE user_email='test@example.com';
```

---

### `set_created_in_task` (Task, BEFORE INSERT)

**Propósito:** timestamp de creación de tarea.
**Definición:**

```sql
DROP TRIGGER IF EXISTS set_created_in_task;
DELIMITER $$
CREATE TRIGGER set_created_in_task
BEFORE INSERT ON Task
FOR EACH ROW
BEGIN
  SET NEW.created_in = NOW();
END$$
DELIMITER ;
```

---

### `set_created_in_category` (Category, BEFORE INSERT)

**Propósito:** timestamp de creación de categoría.
**Definición:**

```sql
DROP TRIGGER IF EXISTS set_created_in_category;
DELIMITER $$
CREATE TRIGGER set_created_in_category
BEFORE INSERT ON Category
FOR EACH ROW
BEGIN
  SET NEW.created_in = NOW();
END$$
DELIMITER ;
```

---

### `prevent_past_expires_in` (Task, BEFORE INSERT/UPDATE)

**Propósito:** evitar que `expires_in` quede en el pasado.
**Definición:**

```sql
DROP TRIGGER IF EXISTS prevent_past_expires_in;
DELIMITER $$
CREATE TRIGGER prevent_past_expires_in
BEFORE INSERT ON Task
FOR EACH ROW
BEGIN
  IF NEW.expires_in IS NOT NULL AND NEW.expires_in < NOW() THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'expires_in no puede ser una fecha pasada';
  END IF;
END$$
DELIMITER ;

-- Variante para UPDATE
DROP TRIGGER IF EXISTS prevent_past_expires_in_upd;
DELIMITER $$
CREATE TRIGGER prevent_past_expires_in_upd
BEFORE UPDATE ON Task
FOR EACH ROW
BEGIN
  IF NEW.expires_in IS NOT NULL AND NEW.expires_in < NOW() THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'expires_in no puede ser una fecha pasada (update)';
  END IF;
END$$
DELIMITER ;
```

**Prueba:**

```sql
-- Debe fallar
INSERT INTO Task(list_id, task_title, task_description, task_status, expires_in)
VALUES (1,'Demo','Prueba','NEW', DATE_SUB(NOW(), INTERVAL 1 DAY));
```

---

### `validate_task_dates` (Task, BEFORE INSERT/UPDATE)

**Propósito:** coherencia temporal (`expires_in` debe ser >= `created_in`).
**Definición:**

```sql
DROP TRIGGER IF EXISTS validate_task_dates_ins;
DELIMITER $$
CREATE TRIGGER validate_task_dates_ins
BEFORE INSERT ON Task
FOR EACH ROW
BEGIN
  IF NEW.expires_in IS NOT NULL AND NEW.expires_in < NOW() THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'expires_in debe ser >= NOW()';
  END IF;
END$$
DELIMITER ;

DROP TRIGGER IF EXISTS validate_task_dates_upd;
DELIMITER $$
CREATE TRIGGER validate_task_dates_upd
BEFORE UPDATE ON Task
FOR EACH ROW
BEGIN
  IF NEW.expires_in IS NOT NULL AND NEW.expires_in < OLD.created_in THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'expires_in debe ser >= created_in';
  END IF;
END$$
DELIMITER ;
```

---

### `validate_task_status` (Task, BEFORE UPDATE)

**Propósito:** restringir transiciones inválidas (p. ej., no reabrir `DONE`).
**Reglas sugeridas:**
`NEW -> IN_PROGRESS -> DONE/CANCELLED`. No se permite `DONE -> IN_PROGRESS/NEW`.

```sql
DROP TRIGGER IF EXISTS validate_task_status;
DELIMITER $$
CREATE TRIGGER validate_task_status
BEFORE UPDATE ON Task
FOR EACH ROW
BEGIN
  IF NEW.task_status <> OLD.task_status THEN
    IF OLD.task_status = 'DONE' AND NEW.task_status IN ('IN_PROGRESS','NEW') THEN
      SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'No se puede reabrir una tarea DONE';
    END IF;
  END IF;
END$$
DELIMITER ;
```

**Prueba:**

```sql
UPDATE Task SET task_status='DONE' WHERE task_id=1;
-- Debe fallar
UPDATE Task SET task_status='IN_PROGRESS' WHERE task_id=1;
```

---

### `tracking_task_status` (Task, AFTER UPDATE)

**Propósito:** auditar cambios de estado en `Task_status` (historial).
**Definición:**

```sql
DROP TRIGGER IF EXISTS tracking_task_status;
DELIMITER $$
CREATE TRIGGER tracking_task_status
AFTER UPDATE ON Task
FOR EACH ROW
BEGIN
  IF NEW.task_status <> OLD.task_status THEN
    INSERT INTO Task_status(task_id, old_status, new_status, changed_in)
    VALUES(NEW.task_id, OLD.task_status, NEW.task_status, NOW());
  END IF;
END$$
DELIMITER ;
```

**Prueba:**

```sql
UPDATE Task SET task_status='IN_PROGRESS' WHERE task_id=2;
SELECT * FROM Task_status WHERE task_id=2 ORDER BY changed_in DESC;
```

---

### `after_user_insert` (AppUser, AFTER INSERT)

**Propósito:** provisionar datos iniciales para un usuario nuevo (evita pantallas vacías).
**Ejemplo típico (ajusta según tus constraints únicas):**

```sql
DROP TRIGGER IF EXISTS after_user_insert;
DELIMITER $$
CREATE TRIGGER after_user_insert
AFTER INSERT ON AppUser
FOR EACH ROW
BEGIN
  -- Crea categoría "General" para el usuario si no existe
  INSERT INTO Category(category_name, user_id, created_in)
  SELECT 'General', NEW.user_id, NOW()
  WHERE NOT EXISTS (
    SELECT 1 FROM Category WHERE user_id = NEW.user_id AND category_name = 'General'
  );

  -- Crea lista "Mis Tareas" bajo "General"
  INSERT INTO List(list_name, category_id, created_in)
  SELECT 'Mis Tareas', c.category_id, NOW()
  FROM Category c
  WHERE c.user_id = NEW.user_id AND c.category_name = 'General'
    AND NOT EXISTS (
      SELECT 1 FROM List l WHERE l.category_id = c.category_id AND l.list_name = 'Mis Tareas'
    );
END$$
DELIMITER ;
```

---

## Despliegue e idempotencia

Para evitar `Error 1359: Trigger already exists` al re-ejecutar scripts, usamos **DROP … IF EXISTS** antes de cada `CREATE TRIGGER`. Si migrás con Flyway, recomendamos una migración específica para “sanear”:

```sql
-- V10__drop_triggers_if_exists.sql
DROP TRIGGER IF EXISTS set_created_in_user;
DROP TRIGGER IF EXISTS set_created_in_task;
DROP TRIGGER IF EXISTS set_created_in_category;
DROP TRIGGER IF EXISTS prevent_past_expires_in;
DROP TRIGGER IF EXISTS prevent_past_expires_in_upd;
DROP TRIGGER IF EXISTS validate_task_dates_ins;
DROP TRIGGER IF EXISTS validate_task_dates_upd;
DROP TRIGGER IF EXISTS validate_task_status;
DROP TRIGGER IF EXISTS tracking_task_status;
DROP TRIGGER IF EXISTS after_user_insert;
```

> **Orden recomendado de ejecución** (si corrés manualmente):
> Tablas → Funciones → Procedimientos → **Triggers** → Vistas → Índices → Seeders.

---

## Verificación rápida

Listar triggers del esquema actual:

```sql
SELECT TRIGGER_NAME, EVENT_MANIPULATION, EVENT_OBJECT_TABLE, ACTION_TIMING
FROM information_schema.TRIGGERS
WHERE TRIGGER_SCHEMA = DATABASE()
ORDER BY EVENT_OBJECT_TABLE, TRIGGER_NAME;
```
``` FUNCIONES```
---
## fn_email_canonical
```
Propósito: normalizar direcciones de correo electrónico eliminando espacios y convirtiendo todo a minúsculas.
Ayuda a evitar duplicados y errores por diferencias de formato.

SELECT fn_email_canonical('  TEST@GMAIL.COM  ');
-- Resultado: 'test@gmail.com'
```
---
## fn_default_color
```
Propósito: asignar un color hexadecimal por defecto (#999999) cuando no se define uno explícitamente en categorías 
o listas.
```
---
## fn_priority_order
```
Propósito: transformar una prioridad textual (LOW, MIDDLE, HIGH) en un valor numérico que permita ordenarlas
de menor a mayor.
SELECT fn_priority_order('HIGH');
-- Resultado: 3
```
---
## fn_status_order
```
Propósito: establecer un orden lógico entre los estados de una tarea (NEW, IN_PROGRESS, DONE, CANCELLED).
SELECT fn_status_order('in_progress');
-- Resultado: 2
```
---
## fn_is_overdue_by_dates
```
Propósito: verificar si una tarea está vencida según su fecha de expiración (expires_in) y estado (task_status).
Una tarea se considera vencida si tiene fecha anterior a NOW() y no está marcada como DONE.
SELECT fn_is_overdue_by_dates('2025-01-01 00:00:00', 'NEW');
-- Resultado: 1 (si la fecha actual es posterior)

```
---
## fn_is_overdue_by_id
```
Propósito: determinar si una tarea (por su task_id) está vencida, consultando sus datos reales en la tabla Task.
SELECT fn_is_overdue_by_id(12);
-- Resultado: 1 si la tarea 12 está vencida
```
## fn_list_owner_user_id
```
Propósito: obtener el identificador del usuario propietario de una lista (List) determinada.
La relación se obtiene a través de la tabla Category.
SELECT fn_list_owner_user_id(3);
-- Resultado: 7 (id del usuario propietario)

```

## 📘 Documentación de Vistas SQL
---
## 1. Vista: UserLists

Descripción:
Esta vista muestra la relación entre los usuarios, sus categorías y las listas que pertenecen a cada categoría. Permite visualizar la jerarquía de información desde el usuario hasta las listas creadas.

## Propósito:
Facilitar la consulta de las listas creadas por cada usuario, organizadas por categoría.

Tablas involucradas:
AppUser, Category, List

## Campos devueltos:

Campo	Descripción
User_Name	Nombre del usuario
Category	Nombre de la categoría perteneciente al usuario
List	Nombre de la lista creada en esa categoría
Created_In	Fecha en que la lista fue creada

## Consulta SQL:

CREATE VIEW UserLists AS
SELECT U.user_name AS User_Name, C.category_name AS Category, L.list_name AS List, L.created_in AS Created_In
FROM AppUser U
JOIN Category C ON U.user_id = C.user_id
JOIN List L ON C.category_id = L.category_id;

---
---
## 2. Vista: UserCategoryCount

Descripción:
Muestra el número total de categorías creadas por cada usuario registrado en el sistema.

Propósito:
Permitir un conteo rápido de las categorías que posee cada usuario para análisis o reportes.

Tablas involucradas:
AppUser, Category

Campos devueltos:

Campo	Descripción
user_id	Identificador único del usuario
user_name	Nombre del usuario
total_categories	Cantidad total de categorías asociadas al usuario

Consulta SQL:

CREATE VIEW UserCategoryCount AS
SELECT U.user_id, U.user_name, COUNT(C.category_id) AS total_categories
FROM AppUser U
LEFT JOIN Category C ON U.user_id = C.user_id
GROUP BY U.user_id, U.user_name;
---
---

## 3. Vista: ListTaskCount
```
Descripción:
Muestra cada lista registrada en el sistema junto con el número total de tareas que contiene.

Propósito:
Brindar una visión general del nivel de actividad o cantidad de tareas por lista.

Tablas involucradas:
List, Task

Campos devueltos:

Campo	Descripción
list_id	Identificador de la lista
list_name	Nombre de la lista
total_tasks	Número total de tareas asociadas

Consulta SQL:

CREATE VIEW ListTaskCount AS
SELECT L.list_id, L.list_name, COUNT(T.task_id) AS total_tasks
FROM List L
LEFT JOIN Task T ON L.list_id = T.list_id
GROUP BY L.list_id, L.list_name;
```
---
---

## 4. Vista: PendingTasksByUser
```
Descripción:
Muestra las tareas que se encuentran pendientes o en progreso, organizadas por usuario.

Propósito:
Permitir identificar fácilmente las tareas activas que aún no se han completado por cada usuario.

Tablas involucradas:
AppUser, Category, List, Task

Campos devueltos:

Campo	Descripción
user_name	Nombre del usuario responsable
task_title	Título de la tarea
task_status	Estado actual de la tarea (NEW o IN_PROGRESS)
expires_in	Fecha límite o vencimiento de la tarea

Consulta SQL:

CREATE VIEW PendingTasksByUser AS
SELECT U.user_name, T.task_title, T.task_status, T.expires_in
FROM AppUser U
JOIN Category C ON U.user_id = C.user_id
JOIN List L ON C.category_id = L.category_id
JOIN Task T ON L.list_id = T.list_id
WHERE T.task_status IN ('NEW', 'IN_PROGRESS');
```
---
---

## 5. Vista: OverdueTasks
````
Descripción:
Muestra las tareas que están vencidas (fecha de expiración anterior a la actual) y que aún no se han marcado como completadas.

Propósito:
Ayudar a detectar y dar seguimiento a tareas atrasadas o no resueltas dentro del sistema.

Tablas involucradas:
AppUser, Category, List, Task

Campos devueltos:

Campo	Descripción
user_name	Nombre del usuario responsable
task_title	Título de la tarea
expires_in	Fecha de vencimiento de la tarea
task_status	Estado actual de la tarea

Consulta SQL:

CREATE VIEW OverdueTasks AS
SELECT U.user_name, T.task_title, T.expires_in, T.task_status
FROM AppUser U
JOIN Category C ON U.user_id = C.user_id
JOIN List L ON C.category_id = L.category_id
JOIN Task T ON L.list_id = T.list_id
WHERE T.expires_in < NOW() AND T.task_status <> 'DONE';
````
---
---
## ⚙️ Documentación de Procedimientos Almacenados
---
## 1. Procedimiento: sp_create_user

Descripción:
Crea un nuevo usuario en el sistema asegurando que el correo electrónico no esté duplicado.

Propósito:
Registrar nuevos usuarios con validación de email único y formato canónico.

Parámetros:

Tipo	Nombre	Descripción
IN	p_user_name	Nombre del usuario
IN	p_user_email	Correo electrónico del usuario
IN	p_user_pass	Contraseña del usuario
OUT	p_user_id	ID generado para el nuevo usuario

Validaciones:

Verifica que el correo no exista previamente.

Si el correo ya está registrado, lanza el error 'Email ya registrado'.

Tablas afectadas:
AppUser

Resultado:
Inserta un nuevo registro en AppUser y devuelve su user_id.
---
---

## 2. Procedimiento: sp_create_category

# Descripción:
Crea una nueva categoría asociada a un usuario existente.

# Propósito:
Permitir que los usuarios organicen sus listas mediante categorías personalizadas.

# Parámetros:

Tipo	Nombre	Descripción
IN	p_user_id	ID del usuario dueño de la categoría
IN	p_name	Nombre de la categoría
IN	p_color	Color asignado (hexadecimal)
OUT	p_category_id	ID de la categoría creada

# Validaciones:

Verifica que el usuario exista.

Si no se especifica color, se asigna uno por defecto (fn_default_color()).

Evita nombres de categoría duplicados para el mismo usuario.

Tablas afectadas:
Category

Resultado:
Inserta una nueva categoría y devuelve su ID.
---
---

## Procedimiento: sp_create_list

Descripción:
Crea una lista dentro de una categoría específica.

Propósito:
Agregar nuevas listas de tareas en una categoría existente.

Parámetros:

Tipo	Nombre	Descripción
IN	p_category_id	ID de la categoría
IN	p_name	Nombre de la lista
IN	p_desc	Descripción de la lista
OUT	p_list_id	ID generado para la lista

Validaciones:

Comprueba que la categoría exista.

Impide nombres de lista duplicados dentro de la misma categoría.

Tablas afectadas:
List

Resultado:
Inserta una nueva lista y retorna su ID.
---
---

## 4. Procedimiento: sp_create_task

Descripción:
Crea una nueva tarea dentro de una lista determinada.

Propósito:
Registrar tareas con prioridad, fecha de vencimiento y descripción.

Parámetros:

Tipo	Nombre	Descripción
IN	p_list_id	ID de la lista contenedora
IN	p_title	Título de la tarea
IN	p_desc	Descripción de la tarea
IN	p_expires	Fecha de vencimiento
IN	p_priority	Nivel de prioridad (LOW, MIDDLE, HIGH)
OUT	p_task_id	ID generado para la tarea

Validaciones:

Verifica que la lista exista.

La prioridad se normaliza a mayúsculas y por defecto es LOW.

La fecha de vencimiento no puede ser anterior al momento actual.

Tablas afectadas:
Task

Resultado:
Inserta una nueva tarea con estado inicial 'NEW'.
---
---

## 5. Procedimiento: sp_update_task

# Descripción:
Actualiza los datos de una tarea existente.

# Propósito:
Modificar título, descripción, prioridad o fecha de expiración de una tarea.

# Parámetros:

Tipo	Nombre	Descripción
IN	p_task_id	ID de la tarea a modificar
IN	p_title	Nuevo título
IN	p_desc	Nueva descripción
IN	p_expires	Nueva fecha de vencimiento
IN	p_priority	Nueva prioridad

Validaciones:

Verifica que la tarea exista.

La fecha no puede ser anterior al presente.

La prioridad debe ser válida (LOW, MIDDLE, HIGH).

Tablas afectadas:
Task

Resultado:
Actualiza los campos especificados de la tarea.
---
---

## 6. Procedimiento: sp_change_task_status

# Descripción:
Cambia el estado de una tarea existente y opcionalmente agrega un comentario.

Propósito:
Actualizar el flujo de trabajo de una tarea (nuevo, en progreso, finalizado, cancelado).

Parámetros:

Tipo	Nombre	Descripción
IN	p_task_id	ID de la tarea
IN	p_new_status	Nuevo estado (NEW, IN_PROGRESS, DONE, CANCELLED)
IN	p_comment	Comentario opcional

Validaciones:

La tarea debe existir.

El estado nuevo debe ser válido.

Tablas afectadas:
Task, Task_status

Resultado:
Actualiza el estado y, si aplica, el último comentario.
--- 
---

# 7. Procedimiento: sp_move_task

Descripción:
Mueve una tarea de una lista a otra dentro del mismo usuario.

Propósito:
Reorganizar tareas entre listas del mismo propietario.

Parámetros:

Tipo	Nombre	Descripción
IN	p_task_id	ID de la tarea a mover
IN	p_target_list_id	ID de la lista destino

Validaciones:

La tarea y la lista destino deben existir.

Ambas listas deben pertenecer al mismo usuario.

Tablas afectadas:
Task

Resultado:
La tarea es reasignada a la lista destino.
---
---

## 8. Procedimiento: sp_delete_task

Descripción:
Elimina una tarea junto con su historial de estados.

Propósito:
Realizar un borrado seguro de tareas.

Parámetros:

Tipo	Nombre	Descripción
IN	p_task_id	ID de la tarea a eliminar

Tablas afectadas:
Task_status, Task

Resultado:
Elimina la tarea y sus registros asociados mediante transacción.
---
---

## 9. Procedimiento: sp_delete_list

Descripción:
Elimina una lista y todas las tareas que contiene.

Propósito:
Borrar una lista completa junto con su información relacionada.

Parámetros:

Tipo	Nombre	Descripción
IN	p_list_id	ID de la lista a eliminar

Tablas afectadas:
Task_status, Task, List

Resultado:
Elimina todos los datos asociados en una transacción segura.
---
---
 
## 10. Procedimiento: sp_delete_category

Descripción:
Elimina una categoría junto con sus listas y tareas asociadas.

Propósito:
Realizar un borrado seguro y completo de una categoría del usuario.

Parámetros:

Tipo	Nombre	Descripción
IN	p_category_id	ID de la categoría a eliminar

Tablas afectadas:
Task_status, Task, List, Category

Resultado:
Elimina la categoría y todas las entidades relacionadas.
---
---

## 11. Procedimiento: sp_list_tasks

Descripción:
Devuelve un listado filtrado de tareas según distintos criterios.

Propósito:
Obtener tareas de un usuario con filtros por categoría, lista, estado, prioridad o texto.

Parámetros:

Tipo	Nombre	Descripción
IN	p_user_id	ID del usuario
IN	p_category_id	ID de la categoría (opcional)
IN	p_list_id	ID de la lista (opcional)
IN	p_status	Estado de la tarea
IN	p_priority	Prioridad
IN	p_due_from	Fecha de vencimiento inicial
IN	p_due_to	Fecha de vencimiento final
IN	p_query	Texto a buscar

Tablas consultadas:
Task, List, Category

Resultado:
Devuelve las tareas filtradas, ordenadas por vencimiento, prioridad y estado.
---
---

## 12. Procedimiento: sp_get_dashboard_counts

Descripción:
Obtiene métricas y conteos de tareas para mostrar en el panel principal (dashboard).

Propósito:
Proveer estadísticas sobre el estado actual de las tareas del usuario.

Parámetros:

Tipo	Nombre	Descripción
IN	p_user_id	ID del usuario

Tablas consultadas:
Task, List, Category

Resultado:
Devuelve totales de tareas, clasificadas por estado, vencidas y del día actual.
---
---
## 13.Procedimiento: sp_tasks_next_due

Descripción:
Muestra las próximas tareas que vencerán para un usuario.

Propósito:
Proporcionar una vista rápida de las tareas más urgentes pendientes.

Parámetros:

Tipo	Nombre	Descripción
IN	p_user_id	ID del usuario
IN	p_limit	Límite de tareas a mostrar (por defecto 10)

Tablas consultadas:
Task, List, Category

Resultado:
Devuelve las tareas no completadas, ordenadas por fecha de vencimiento ascendente.
---