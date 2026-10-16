# 🛡️ polizas-api

> API REST para la gestión de pólizas de arrendamiento (individuales y colectivas).

![CI](https://github.com/jdvalmart/polizas-api/actions/workflows/ci.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.8-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)
![H2](https://img.shields.io/badge/DB-H2%20in--memory-1341a3)

Solución al **Módulo 2 – Prueba Técnica Práctica** del proceso de selección de
Desarrollador TI en Seguros Bolívar. 🚀

---

## 🧰 Stack

| Componente | Tecnología |
|:---|:---|
| ☕ Lenguaje | Java 21 |
| 🌱 Framework | Spring Boot 4.0.8 (Spring Web MVC) |
| 🗄️ Persistencia | Spring Data JPA + Hibernate |
| 💾 Base de datos | H2 en memoria |
| 📦 Build | Maven (wrapper incluido) |
| 🧰 Utilidades | Lombok, Bean Validation |

## ✅ Requisitos

- **JDK 17 o superior** (probado con JDK 25). Verificá con `java -version`.
- No hace falta instalar Maven: se usa el wrapper `./mvnw`.

## 🚀 Cómo ejecutar

```bash
./mvnw spring-boot:run
```

La aplicación queda en **`http://localhost:8080`**.
Al arrancar carga datos de ejemplo (una póliza individual y una colectiva) si
la base de datos está vacía.

## ⚙️ Configuración

Propiedades en `src/main/resources/application.properties`:

```properties
app.ipc=0.132                                        # IPC aplicado en la renovación (13.2%)
app.core.url=http://localhost:8080/core-mock/evento  # Servicio del CORE legado (mock)
app.api-key=123456                                   # Cabecera obligatoria x-api-key
```

## 🔐 Seguridad

Todas las peticiones deben incluir:

```http
x-api-key: 123456
```

Si falta o es inválida, la API responde **`401`** con el formato de error estándar.

---

## 🧭 Arquitectura

Arquitectura en capas: **controller → service → repository**, con el dominio
aislado y la integración con el CORE encapsulada.

```mermaid
flowchart TB
    Cliente([🧑‍💻 Cliente / Front])

    subgraph API["polizas-api · Spring Boot"]
        direction TB
        INT["🔐 ApiKeyInterceptor<br/><i>valida x-api-key</i>"]

        subgraph CTRL["🎛️ Controller · capa HTTP"]
            PC[PolizaController]
            RC[RiesgoController]
            MC[CoreMockController]
        end

        GEH["🚨 GlobalExceptionHandler<br/><i>@RestControllerAdvice</i>"]

        subgraph SVC["🧠 Service · reglas de negocio"]
            PS[PolizaService]
            RS[RiesgoService]
            CIS[CoreIntegrationService]
        end

        subgraph REPO["📚 Repository · acceso a datos"]
            PR[PolizaRepository]
            RR[RiesgoRepository]
        end

        subgraph DOM["🏛️ Domain · modelo"]
            P[Poliza]
            R[Riesgo]
        end
    end

    DB[(💾 H2 en memoria)]
    CORE[["🏦 CORE legado<br/>/core-mock/evento"]]

    Cliente --> INT --> CTRL
    PC --> PS
    RC --> RS
    PC --> RS
    PS --> PR
    RS --> PR
    RS --> RR
    PR --> P
    RR --> R
    PR --> DB
    RR --> DB
    PS -. notificar() .-> CIS
    RS -. notificar() .-> CIS
    CIS -- "POST HTTP + x-api-key" --> MC
    MC -. solo loguea .-> CORE
    CTRL -. excepciones .-> GEH

    style API fill:#f0f7ff,stroke:#4a90d9
    style SVC fill:#fff8e1,stroke:#e0a800
    style DB fill:#e8f5e9,stroke:#43a047
    style CORE fill:#fce4ec,stroke:#d81b60
```

### 🔄 Flujo de una renovación

```mermaid
sequenceDiagram
    autonumber
    participant C as 🧑‍💻 Cliente
    participant I as 🔐 ApiKeyInterceptor
    participant PC as PolizaController
    participant PS as PolizaService
    participant DB as 💾 H2
    participant CIS as CoreIntegrationService
    participant MC as CoreMockController

    C->>I: POST /polizas/1/renovar (x-api-key)
    alt header inválido
        I-->>C: 401 Unauthorized
    else header válido
        I->>PC: pasa la petición
        PC->>PS: renovar(1)
        PS->>DB: findById(1)
        alt póliza CANCELADA
            PS-->>PC: BusinessException
            PC-->>C: 409 Conflict
        else póliza activa
            PS->>PS: canon ×= (1 + IPC)<br/>prima = canon × meses<br/>estado = RENOVADA
            PS->>DB: save(poliza)
            PS->>CIS: notificar("ACTUALIZACION", 1)
            CIS->>MC: POST /core-mock/evento
            MC->>MC: 📝 log del intento
            PS-->>PC: póliza renovada
            PC-->>C: 200 OK + JSON
        end
    end
```

### 🗃️ Modelo de datos

```mermaid
erDiagram
    POLIZA ||--o{ RIESGO : "tiene (1:N)"
    POLIZA {
        Long id PK
        TipoPoliza tipo "INDIVIDUAL | COLECTIVA"
        EstadoPoliza estado "ACTIVA | CANCELADA | RENOVADA"
        LocalDate fechaInicio
        LocalDate fechaFin
        Integer mesesVigencia
        BigDecimal canonMensual
        BigDecimal prima
    }
    RIESGO {
        Long id PK
        Long poliza_id FK
        String descripcion
        EstadoRiesgo estado "ACTIVO | CANCELADO"
    }
```

---

## 🔌 Endpoints

| Método | Ruta | Descripción |
|:---:|:---|:---|
| 🟢 `GET` | `/polizas?tipo=&estado=` | Lista pólizas filtrando (opcional) por tipo y estado |
| 🟢 `GET` | `/polizas/{id}/riesgos` | Lista los riesgos de una póliza |
| 🟡 `POST` | `/polizas/{id}/riesgos` | Agrega un riesgo (solo pólizas **COLECTIVA**) |
| 🟡 `POST` | `/polizas/{id}/renovar` | Renueva la póliza aplicando el IPC |
| 🟡 `POST` | `/polizas/{id}/cancelar` | Cancela la póliza y todos sus riesgos |
| 🟡 `POST` | `/riesgos/{id}/cancelar` | Cancela un riesgo |
| 🔵 `POST` | `/core-mock/evento` | Mock del CORE legado: registra en logs el evento |

Valores válidos: `tipo` = `INDIVIDUAL` | `COLECTIVA`;
`estado` = `ACTIVA` | `CANCELADA` | `RENOVADA`.

### 📬 Ejemplos

```bash
# Listar pólizas colectivas activas
curl -H 'x-api-key: 123456' 'http://localhost:8080/polizas?tipo=COLECTIVA&estado=ACTIVA'

# Riesgos de la póliza 2
curl -H 'x-api-key: 123456' http://localhost:8080/polizas/2/riesgos

# Agregar un riesgo a una póliza colectiva
curl -H 'x-api-key: 123456' -H 'Content-Type: application/json' \
     -d '{"descripcion":"Bodega 7 - Bogotá"}' \
     http://localhost:8080/polizas/2/riesgos

# Renovar la póliza 1 (incrementa canon y prima por IPC)
curl -X POST -H 'x-api-key: 123456' http://localhost:8080/polizas/1/renovar

# Cancelar la póliza 1 (cascada a sus riesgos)
curl -X POST -H 'x-api-key: 123456' http://localhost:8080/polizas/1/cancelar

# Cancelar un riesgo
curl -X POST -H 'x-api-key: 123456' http://localhost:8080/riesgos/1/cancelar
```

## 📜 Reglas de negocio

- 🎯 Una póliza **individual** solo puede tener **1 riesgo** (por eso solo las
  colectivas admiten agregar riesgos).
- 🚫 **No se puede renovar** una póliza cancelada.
- 🧨 La **cancelación de una póliza cancela todos sus riesgos** en cascada.
- 🚫 **Agregar un riesgo exige** que la póliza sea `COLECTIVA`.
- 📡 Toda acción que modifique estado **notifica al CORE** legado.

## 📁 Estructura del proyecto

```
src/main/java/com/segurosbolivar/polizasapi/
├── ⚙️ config/       ApiKeyInterceptor, WebConfig, DataSeeder
├── 🎛️ controller/   PolizaController, RiesgoController, CoreMockController
├── 🏛️ domain/       Poliza, Riesgo y enums de estado/tipo
├── 📦 dto/          PolizaResponse, RiesgoRequest/Response, CoreEventoRequest
├── 🚨 exception/    BusinessException, ApiError, GlobalExceptionHandler
├── 📚 repository/   PolizaRepository, RiesgoRepository
└── 🧠 service/      PolizaService, RiesgoService, CoreIntegrationService
```

## 🧪 Pruebas

```bash
./mvnw test
```

Tests de integración con `MockMvc` + H2 que cubren las reglas de negocio y la
seguridad (401 sin api-key, renovación con IPC, cascada, solo colectivas, etc.).

## 🔁 Integración continua

GitHub Actions ejecuta `./mvnw test` en cada push y pull request a `main`
(ver `.github/workflows/ci.yml`).
