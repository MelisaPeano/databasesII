# Diccionario de datos – Estado AS-IS (V1)

## AppUser
| Campo         | Tipo            | Nulo | Clave        | Reglas / Notas                                     |
|---------------|-----------------|------|--------------|----------------------------------------------------|
| user_id       | INT             | NO   | PK, UQ, AI   | Identificador.                                     |
| user_name     | VARCHAR(100)    | NO   | —            | Nombre visible.                                    |
| user_email    | VARCHAR(100)    | NO   | UQ           | Email único.                                       |
| user_password | VARCHAR(100)    | NO   | —            | Hash/contraseña.                                   |
| created_in    | DATETIME        | SÍ   | —            | **Se setea por trigger** `set_created_in_user`.    |

## Category
| Campo         | Tipo         | Nulo | Clave      | Reglas / Notas                                               |
|---------------|--------------|------|------------|--------------------------------------------------------------|
| category_id   | INT          | NO   | PK, UQ, AI | —                                                            |
| user_id       | INT          | NO   | FK         | → `AppUser(user_id)`.                                       |
| category_name | VARCHAR(60)  | NO   | —          | Nombre de la categoría.                                     |
| category_color| CHAR(7)      | SÍ   | —          | Color HEX opcional (p.ej. `#A1B2C3`).                        |
| created_in    | DATETIME     | SÍ   | —          | **Se setea por trigger** `set_created_in_category`.         |

## List
| Campo           | Tipo           | Nulo | Clave      | Reglas / Notas                                           |
|-----------------|----------------|------|------------|----------------------------------------------------------|
| list_id         | INT            | NO   | PK, UQ, AI | —                                                        |
| category_id     | INT            | NO   | FK         | → `Category(category_id)`.                               |
| list_name       | VARCHAR(60)    | NO   | —          | —                                                        |
| list_description| VARCHAR(255)   | SÍ   | —          | —                                                        |
| created_in      | DATETIME       | SÍ   | —          | **Se setea por trigger** `set_created_in_list`.          |

## Task
| Campo           | Tipo                                   | Nulo | Clave      | Reglas / Notas                                                                 |
|-----------------|----------------------------------------|------|------------|---------------------------------------------------------------------------------|
| task_id         | INT                                    | NO   | PK, UQ, AI | —                                                                               |
| list_id         | INT                                    | NO   | FK         | → `List(list_id)`.                                                              |
| task_title      | VARCHAR(100)                           | NO   | —          | —                                                                               |
| task_description| TEXT                                   | SÍ   | —          | —                                                                               |
| expires_in      | DATETIME                               | SÍ   | —          | **Validado por trigger** `validate_task_expiration` (no se permite pasado).    |
| priority        | ENUM('LOW','HIGH','MIDDLE')            | SÍ   | —          | Enumerado de prioridad (orden lógico sugerido: LOW < MIDDLE < HIGH).           |
| task_status     | ENUM('NEW','IN_PROGRESS','DONE','CANCELLED') | SÍ | —  | Estado actual. **Ver triggers 5 y 6**.                                         |
| created_in      | DATETIME                               | SÍ   | —          | **Se setea por trigger** `set_created_in_task`.                                |
| completed_in    | DATETIME                               | SÍ   | —          | **Se setea automáticamente** al pasar a `DONE` (trigger `set_completed_in_task`). |

## Task_status (historial de estados)
| Campo          | Tipo                                           | Nulo | Clave      | Reglas / Notas                                                                   |
|----------------|------------------------------------------------|------|------------|-----------------------------------------------------------------------------------|
| task_status_id | INT                                            | NO   | PK, UQ, AI | —                                                                                |
| task_id        | INT                                            | NO   | FK         | → `Task(task_id)`.                                                                |
| status         | ENUM('NEW','IN_PROGRESS','DONE','CANCELLED')   | SÍ   | —          | Valor del estado en el momento del cambio.                                        |
| changed_in     | DATETIME                                       | SÍ   | —          | Fecha/hora del cambio.                                                            |
| comment        | VARCHAR(100)                                   | SÍ   | —          | Comentario opcional (los cambios automáticos escriben 'Cambio automático por trigger'). |
