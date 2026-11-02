# Banca Digital – Cajero Automático Inteligente

Parcial Final – Análisis y Diseño de Sistemas (Spring Boot 3.5 · Java 25 · Arquitectura Limpia).

## Requisitos
- JDK 25
- Maven 3.9+ (o usa tu IDE: IntelliJ / VS Code importan el `pom.xml`)

## Ejecutar
```bash
mvn spring-boot:run          # API en http://localhost:8080
mvn test                     # pruebas unitarias + integración
```
Al primer arranque se crea un **usuario demo**:

| Correo | Clave | PIN | Saldo |
|---|---|---|---|
| `demo@banca.com` | `Demo12345` | `1234` | $2.000.000 |

La base H2 se guarda en `./data/` (ignorada por git). Para reiniciarla, borra esa carpeta.

## Endpoints
| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| POST | `/api/auth/registro` | No | Crea usuario + cuenta (clave ≥ 8, PIN de 4 dígitos) |
| POST | `/api/auth/login` | No | Devuelve el token JWT |
| GET | `/api/cuentas/saldo` | Sí | Consulta de saldo |
| POST | `/api/cuentas/depositos` | Sí | `{"monto": 50000}` |
| GET | `/api/usuarios/perfil` | Sí | Datos del titular |
| PUT | `/api/usuarios/perfil` | Sí | Actualiza correo, teléfono y/o dirección |
| PUT | `/api/usuarios/clave` | Sí | `{"claveActual","claveNueva"}` |
| POST | `/api/cajero/retiros` | Sí | `{"monto": 180000, "pin": "1234"}` |

Las peticiones autenticadas llevan `Authorization: Bearer <token>`.

### Ejemplo rápido
```bash
TOKEN=$(curl -s -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"correo":"demo@banca.com","clave":"Demo12345"}' | sed -E 's/.*"token":"([^"]+)".*/\1/')

curl -s -X POST localhost:8080/api/cajero/retiros -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' -d '{"monto":180000,"pin":"1234"}'
```
Respuesta:
```json
{"montoRetirado":180000,
 "billetes":[{"denominacion":100000,"cantidad":1},{"denominacion":50000,"cantidad":1},
             {"denominacion":20000,"cantidad":1},{"denominacion":10000,"cantidad":1}],
 "totalBilletes":4,"nuevoSaldo":1820000,"acumuladoHoy":180000,"disponibleHoy":820000}
```

## Reglas del retiro y códigos de error
Se informa el **primer** incumplimiento, en este orden:

| Regla | HTTP | `codigo` |
|---|---|---|
| Monto ≤ 0 o nulo | 400 | `MONTO_INVALIDO` |
| Monto < $10.000 | 422 | `MONTO_MINIMO` |
| Monto no múltiplo de $10.000 (no fraccionable) | 422 | `MONTO_NO_FRACCIONABLE` |
| PIN no es de exactamente 4 dígitos | 400 | `PIN_FORMATO_INVALIDO` |
| PIN incorrecto | 422 | `PIN_INCORRECTO` |
| Saldo insuficiente | 422 | `SALDO_INSUFICIENTE` |
| Acumulado del día + monto > $1.000.000 | 422 | `TOPE_DIARIO_EXCEDIDO` |

Error uniforme: `{"codigo","mensaje","status","timestamp"}`.
La jornada del tope diario se calcula en zona `America/Bogota`.

## Arquitectura (dependencias hacia adentro)
```
domain          Java puro: modelo, ReglasRetiro, Dispensador, excepciones
application     casos de uso + puertos (interfaces) + commands/resultados
infrastructure  web (REST), security (JWT/BCrypt), persistence (JPA), config (composición)
```
Los casos de uso no usan anotaciones de Spring; se conectan en `BeanConfig`.
La atomicidad del retiro la da `UnidadDeTrabajo` (implementada con `TransactionTemplate`)
y la cuenta se bloquea con `PESSIMISTIC_WRITE` para evitar carreras en el tope diario.

## Flujo de ramas
`feature` → `develop` → `main` (solo `main` se revisa). Repositorio compartido con `jjbrival93`.
