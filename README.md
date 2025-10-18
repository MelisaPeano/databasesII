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