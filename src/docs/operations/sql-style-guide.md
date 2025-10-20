# Guía de estilo SQL
- snake_case para tablas/columnas.
- Timestamps: created_at / updated_at / completed_at.
- Catálogos > ENUM para portabilidad.
- Índices: FKs + columnas de filtro (estado, fecha).
- Nombrado objetos:
    - Vistas: `vw_*`
    - SPs: `sp_*` (verbo_sujeto)
    - Funciones: `fn_*`
- Migraciones: `V{n}__descripcion.sql` (Flyway-like). Prohibido editar versiones ya mergeadas.
