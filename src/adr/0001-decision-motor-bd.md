# ADR 0001 — Motor de Base de Datos
**Estado:** Aprobado

## Contexto
- El DDL de V1 y triggers de V4 usan sintaxis de **MySQL 8 / InnoDB** (ENUM, SIGNAL, NOW(), backticks).
- El curso requiere SPs, triggers y compatibilidad con MySQL.

## Decisión
Adoptar **MySQL 8.0 (InnoDB)** como motor para desarrollo y entrega.

## Consecuencias
- Mantener DDL y triggers compatibles con MySQL 8.
- **Deuda técnica:** evaluar migrar `ENUM` a catálogos + FKs para mayor portabilidad.
- Documentar estrategia de cascadas (ver ADR-0002 propuesto).
