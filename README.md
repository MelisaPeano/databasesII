Manual de Usuario Técnico
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
```fn_email_canonical
Propósito: normalizar direcciones de correo electrónico eliminando espacios y convirtiendo todo a minúsculas.
Ayuda a evitar duplicados y errores por diferencias de formato.

SELECT fn_email_canonical('  TEST@GMAIL.COM  ');
-- Resultado: 'test@gmail.com'
```
---
```fn_default_color 
Propósito: asignar un color hexadecimal por defecto (#999999) cuando no se define uno explícitamente en categorías 
o listas.
```
---
```fn_priority_order
Propósito: transformar una prioridad textual (LOW, MIDDLE, HIGH) en un valor numérico que permita ordenarlas
de menor a mayor.
SELECT fn_priority_order('HIGH');
-- Resultado: 3
```
---
```fn_status_order
Propósito: establecer un orden lógico entre los estados de una tarea (NEW, IN_PROGRESS, DONE, CANCELLED).
SELECT fn_status_order('in_progress');
-- Resultado: 2
```
---
## fn_is_overdue_by_dates
```Propósito: verificar si una tarea está vencida según su fecha de expiración (expires_in) y estado (task_status).
Una tarea se considera vencida si tiene fecha anterior a NOW() y no está marcada como DONE.
SELECT fn_is_overdue_by_dates('2025-01-01 00:00:00', 'NEW');
-- Resultado: 1 (si la fecha actual es posterior)

```
---
## fn_is_overdue_by_id
```Propósito: determinar si una tarea (por su task_id) está vencida, consultando sus datos reales en la tabla Task.
SELECT fn_is_overdue_by_id(12);
-- Resultado: 1 si la tarea 12 está vencida
```
## fn_list_owner_user_id
```Propósito: obtener el identificador del usuario propietario de una lista (List) determinada.
La relación se obtiene a través de la tabla Category.
SELECT fn_list_owner_user_id(3);
-- Resultado: 7 (id del usuario propietario)

```

