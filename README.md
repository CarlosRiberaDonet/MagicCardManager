# Magic Investor

Aplicacion web full stack para la busqueda, analisis y seguimiento de inversiones en cartas de Magic: The Gathering. Integra datos de Scryfall y Cardmarket para ofrecer precios actualizados, gestion de coleccion personal y watchlist de seguimiento.

---

## Indice

- [Descripcion](#descripcion)
- [Tecnologias](#tecnologias)
- [Arquitectura](#arquitectura)
- [Base de datos](#base-de-datos)
- [API REST](#api-rest)
- [Seguridad JWT](#seguridad-jwt)
- [Frontend](#frontend)
- [Instalacion y ejecucion](#instalacion-y-ejecucion)
- [Primera carga de datos](#primera-carga-de-datos)
- [Actualizacion automatica](#actualizacion-automatica)
- [Funcionalidades](#funcionalidades)
- [Roadmap](#roadmap)

---

## Descripcion

Magic Investor es una herramienta orientada a inversores y coleccionistas de Magic: The Gathering. Integra datos de **Scryfall** (catalogo completo de cartas) y **Cardmarket** (precios de mercado) para ofrecer una experiencia completa de busqueda, filtrado y seguimiento del valor de cartas.

El sistema permite:
- Buscar cartas con hasta 8 filtros combinables simultaneamente
- Consultar precios actualizados y tendencias de mercado (low, trend, avg1/7/30)
- Gestionar una coleccion personal con seguimiento de inversion (precio de compra vs valor actual)
- Mantener una watchlist de cartas de interes
- Actualizacion automatica diaria de precios desde Cardmarket
- Autenticacion segura con JWT y gestion de roles (USER / ADMIN)

---

## Tecnologias

### Backend

| Tecnologia | Version | Uso |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 3.x | Framework REST |
| Spring Security | 3.x | Autenticacion y autorizacion |
| JJWT | 0.12.6 | Generacion y validacion de tokens JWT |
| MySQL | 8.x | Base de datos relacional |
| JDBC | -- | Acceso a datos con patron DAO manual |
| Jackson | 2.x | Parseo de JSON en streaming |
| Lombok | -- | Reduccion de boilerplate |
| Maven | 3.x | Gestion de dependencias y build |

### Frontend

| Tecnologia | Uso |
|---|---|
| HTML5 / CSS3 | Estructura y estilos |
| JavaScript ES6+ | Logica e interactividad |
| Modulos ES6 | Organizacion y separacion de responsabilidades |
| noUiSlider | Slider de rango de precio |
| Google Fonts (Cinzel + Crimson Pro) | Tipografia premium |

### APIs externas

| API | Uso |
|---|---|
| Scryfall Bulk Data | Catalogo completo de cartas (~500MB) |
| Scryfall Sets API | Informacion de ediciones |
| Cardmarket Price Guide | Precios diarios de mercado |

---

## Arquitectura

El proyecto sigue una arquitectura en capas clasica de Spring Boot con separacion clara de responsabilidades:

```
Frontend (JS Modules)
        |
        | HTTP / REST
        v
Controller Layer          <- Recibe peticiones, extrae parametros, devuelve respuestas
        |
Service Layer             <- Logica de negocio, calculos, coordinacion
        |
DAO Layer                 <- Acceso a datos con JDBC y PreparedStatement
        |
MySQL 8
```

### Capas del backend

```
com.magic.investor_api
├── API/                  <- Clientes de APIs externas (Scryfall, Cardmarket, CardTrader)
├── Auth/                 <- Filtro JWT, servicio JWT, controlador de autenticacion
├── config/               <- Spring Security, CORS, configuracion async
├── controller/           <- Endpoints REST
├── dao/                  <- Acceso a BD con JDBC
├── dto/                  <- Objetos de transferencia de datos
├── mapper/               <- Mapeo de JSON a modelos
├── model/                <- Entidades JPA
├── repository/           <- Repositorios JPA (usados puntualmente)
├── Scheduler/            <- Tareas programadas
└── service/              <- Logica de negocio
```

### Decisiones de diseno

**DAO manual con JDBC** en lugar de JPA para todas las queries de busqueda. Permite construccion dinamica de queries con `StringBuilder` y `PreparedStatement`, garantizando seguridad contra SQL injection y control total sobre el rendimiento.

**Parseo en streaming con Jackson** para procesar los JSONs de Scryfall (500MB+) sin cargar el archivo completo en memoria, evitando desbordamientos de heap.

**JWT stateless** para autenticacion sin estado en servidor, ideal para APIs REST. El token contiene `userId`, `email` y `role`, eliminando consultas a BD en cada peticion autenticada.

**Queries dinamicas con `WHERE 1=1`** para combinar hasta 8 filtros opcionales de forma limpia, segura y extensible.

**Indices en columnas de filtrado** (`name`, `rarity`, `lang`, `set_code`, `price`, etc.) que reducen tiempos de consulta de segundos a milisegundos.

---

## Base de datos

### Esquema principal

```
scryfall_card
─────────────
id (PK)
scryfall_id
cardmarket_id (FK -> card_price)
name / printed_name
lang
image_url
rarity
set_name / set_code (FK -> scryfall_set)
collector_number
cardmarket_url
price / price_foil
type_line
border_color / frame
is_foil / is_reprint
released_at

card_price                    scryfall_set
──────────                    ────────────
id (PK)                       id (PK)
cardmarket_id                 code (UNIQUE)
avg / low / trend             name
avg1 / avg7 / avg30           released_at
avg_foil / low_foil           icon_svg_uri
trend_foil
avg1/7/30_foil
updated_at

user                          user_collection
────                          ───────────────
id (PK)                       id (PK)
email (UNIQUE)                user_id (FK -> user)
password (BCrypt)             card_id (FK -> scryfall_card)
role (USER/ADMIN)             purchase_price
                              quantity
                              added_at

user_watchlist
──────────────
id (PK)
user_id (FK -> user)
card_id (FK -> scryfall_card)
added_at
```

### Indices en scryfall_card

```sql
INDEX idx_name (name)
INDEX idx_printed_name (printed_name)
INDEX idx_rarity (rarity)
INDEX idx_lang (lang)
INDEX idx_set_name (set_name)
INDEX idx_set_code (set_code)
INDEX idx_type_line (type_line)
INDEX idx_price (price)
INDEX idx_cardmarket (cardmarket_id)
```

---

## API REST

### Autenticacion -- `/auth` (publica)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/auth/register` | Registrar nuevo usuario |
| POST | `/auth/login` | Login -- devuelve token JWT |

**Ejemplo login:**
```json
POST /auth/login
{
  "email": "usuario@email.com",
  "password": "password"
}
```
**Respuesta:** token JWT en texto plano.

---

### Cartas -- `/cards` (publica)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/cards/search` | Busqueda con filtros opcionales |
| GET | `/cards/id?cardId=` | Detalle completo de carta por ID |

**Parametros de busqueda (`/cards/search`):**

| Parametro | Tipo | Descripcion |
|---|---|---|
| `name` | String | Nombre parcial (opcional) |
| `setCode` | String | Codigo de edicion |
| `rarity` | String | common / uncommon / rare / mythic |
| `lang` | String | Codigo de idioma (en, es, fr...) |
| `typeLine` | String | Tipo de carta |
| `minPrice` | Double | Precio minimo |
| `maxPrice` | Double | Precio maximo |
| `orderBy` | String | price_asc / price_desc / name_asc / name_desc |
| `hideNA` | Boolean | Ocultar cartas sin precio |
| `page` | int | Numero de pagina (requerido) |
| `size` | int | Resultados por pagina (requerido) |

**Respuesta (`CardPageDTO`):**
```json
{
  "totalCards": 245,
  "page": 1,
  "cardDTOList": [ ... ]
}
```

---

### Ediciones -- `/sets` (publica)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/sets/scryfall` | Lista de ediciones con code e iconSvgUri |

---

### Usuario -- `/user` (requiere JWT)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/user/mycollection` | Coleccion completa con datos de carta y precios |
| GET | `/user/collection` | Lista de IDs de cartas en coleccion |
| GET | `/user/collection/contains?cardId=` | Cantidad de una carta en coleccion |
| POST | `/user/collection/add` | Anadir carta a coleccion |
| DELETE | `/user/collection/del` | Eliminar carta de coleccion |
| GET | `/user/watchlist/contains?cardId=` | Comprobar si carta esta en watchlist |
| POST | `/user/watchlist/add` | Anadir carta a watchlist |
| DELETE | `/user/watchlist/del` | Eliminar carta de watchlist |

**Body para add/del coleccion y watchlist (`UserCollectionDTO`):**
```json
{
  "cardId": 12345,
  "purchasePrice": 5.50
}
```

> El `userId` se extrae automaticamente del token JWT -- nunca se envia desde el cliente.

---

### Administracion -- `/admin` (requiere rol ADMIN)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/admin/update` | Forzar actualizacion completa de BD |

### Precios -- `/prices` (requiere rol ADMIN)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/prices/import` | Descargar price guide de Cardmarket |
| POST | `/prices/update` | Importar precios a tabla card_price |

### Scryfall -- `/scryfall` (requiere rol ADMIN)

| Metodo | Endpoint | Descripcion |
|---|---|---|
| POST | `/scryfall/editions` | Descargar e importar ediciones |
| POST | `/scryfall/cards` | Descargar e importar cartas |
| POST | `/scryfall/update-prices` | Actualizar precios en scryfall_card |

---

## Seguridad JWT

### Flujo de autenticacion

```
1. POST /auth/login  { email, password }
2. Servidor valida password con BCrypt
3. Genera JWT firmado con HMAC-SHA384
   Payload: { userId, email, role, iat, exp (24h) }
4. Cliente almacena token en localStorage
5. Cada peticion protegida incluye:
   Authorization: Bearer <token>
6. JwtAuthFilter intercepta, valida y carga SecurityContext
```

### Roles y permisos

| Rol | Acceso |
|---|---|
| Sin autenticar | Busqueda de cartas, ediciones, login, registro |
| USER | Todo lo anterior + coleccion y watchlist propios |
| ADMIN | Todo lo anterior + actualizacion de BD |

### Seguridad de contrasenas

Las contrasenas se almacenan hasheadas con **BCrypt** (factor de coste 10). Nunca se almacenan ni transmiten en texto plano. El administrador del sistema se crea directamente en BD con hash BCrypt generado por codigo.

---

## Frontend

### Estructura de archivos

```
/
├── index.html              <- Pagina principal -- busqueda y grid de cartas
├── cardDetail.html         <- Detalle de carta con precios y acciones
├── collection.html         <- Coleccion personal del usuario
├── navbar.html             <- Navbar compartido (carga dinamica)
├── favicon.ico
├── css/
│   ├── style.css           <- Estilos globales y grid de cartas
│   ├── navbar.css          <- Navbar, modal de login, menu usuario
│   ├── cardDetail.css      <- Detalle de carta y botones de accion
│   └── collection.css      <- Pagina de coleccion
└── js/
    ├── app.js              <- Logica principal: busqueda, filtros, paginacion
    ├── api.js              <- Llamadas al backend (cartas y ediciones)
    ├── apiUser.js          <- Llamadas al backend (usuario, coleccion, watchlist)
    ├── apiLogin.js         <- Llamadas al backend (autenticacion)
    ├── auth.js             <- Modal login, menu usuario, gestion de token
    ├── navbar.js           <- Carga dinamica del navbar
    ├── cardsRenderer.js    <- Renderizado de cartas en grid
    ├── pagination.js       <- Logica de paginacion
    ├── userActions.js      <- Acciones de coleccion y watchlist
    ├── cardDetail.js       <- Logica de la pagina de detalle
    └── collection.js       <- Logica de la pagina de coleccion
```

### Arquitectura del frontend

```
navbar.js  ->  carga navbar.html dinamicamente
           ->  dispara evento 'navbarLoaded'
           ->  mueve modal al body (evita stacking context)

app.js     ->  escucha 'navbarLoaded'
           ->  enlaza listeners de busqueda y filtros
           ->  llama a api.js para buscar cartas
           ->  llama a cardsRenderer.js para renderizar

auth.js    ->  gestiona modal de login/registro
           ->  gestiona menu desplegable de usuario
           ->  almacena/elimina token JWT en localStorage

userActions.js  ->  llama a apiUser.js para coleccion/watchlist
                ->  abre modal si no hay token
```

### Caracteristicas del frontend

- **Modulos ES6** con separacion clara de responsabilidades
- **Navbar dinamico** cargado con `fetch()` y compartido en todas las paginas
- **Autenticacion JWT** en `localStorage` con menu de usuario desplegable
- **8 filtros combinables**: set, rareza, idioma, tipo, rango de precio con noUiSlider, ordenacion, ocultar sin precio
- **Slider de precio** sincronizado con inputs numericos editables
- **Paginacion** con total de paginas calculado desde el backend
- **Vista lista y cuadricula** en la pagina de coleccion con estadisticas de inversion
- **Responsive** para movil y tablet con media queries en 3 breakpoints
- **Diseno premium** con paleta oscura, acentos dorados y tipografia Cinzel

---

## Instalacion y ejecucion

### Requisitos

- Java 21+
- Maven 3.8+
- MySQL 8+
- Servidor HTTP local para el frontend (VS Code Live Server o similar)

### Backend

```bash
# 1. Clonar el repositorio
git clone https://github.com/CarlosRiberaDonet/MagicManager.git
cd MagicManager

# 2. Configurar la BD en src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/magic_investor
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password

# 3. Crear las tablas en MySQL
# Ejecutar el schema SQL incluido en /docs/schema.sql

# 4. Arrancar el servidor
mvn spring-boot:run
```

El servidor arranca en `http://localhost:8081`.

### Frontend

Abre `index.html` con Live Server de VS Code o cualquier servidor HTTP local.

---

## Primera carga de datos

Con el servidor arrancado, autenticate como ADMIN y ejecuta en orden:

```bash
# 1. Importar ediciones de Scryfall
POST http://localhost:8081/scryfall/editions

# 2. Importar cartas de Scryfall (~500MB, proceso largo ~10-15 min)
POST http://localhost:8081/scryfall/cards

# 3. Descargar price guide de Cardmarket
POST http://localhost:8081/prices/import

# 4. Importar precios a la tabla card_price
POST http://localhost:8081/prices/update

# 5. Actualizar precios en scryfall_card
POST http://localhost:8081/scryfall/update-prices
```

> Todos los endpoints de administracion requieren el header:
> `Authorization: Bearer <token_admin>`

---

## Actualizacion automatica

El sistema incluye un `SchedulerTask` que ejecuta el proceso completo de actualizacion automaticamente cada dia a las **6:00 AM**:

```java
@Scheduled(cron = "0 0 6 * * *")
public void updateBBDD()
```

El proceso incluye: descarga de price guide de Cardmarket, importacion a `card_price`, descarga de cartas de Scryfall, importacion a `scryfall_card` y actualizacion de precios.

Para forzar una actualizacion manual sin esperar al scheduler:

```bash
POST http://localhost:8081/admin/update
Authorization: Bearer <token_admin>
```

---

## Funcionalidades

### Implementadas

- Busqueda de cartas con 8 filtros combinables y paginacion
- Detalle completo de carta con precios historicos (low, trend, avg1/7/30)
- Enlace directo a Cardmarket con idioma correcto
- Registro e inicio de sesion con JWT y BCrypt
- Coleccion personal: anadir, eliminar, controlar cantidad por carta
- Watchlist: seguimiento de cartas de interes
- Pagina de coleccion con vista lista y cuadricula
- Estadisticas de coleccion: total cartas, valor actual, invertido, ganancia/perdida
- Iconos SVG de edicion en cartas y detalle
- Actualizacion automatica diaria de precios
- Panel de administracion para actualizaciones manuales
- Diseno responsive para movil y tablet
- Navbar compartido con carga dinamica

### En desarrollo

- Grafica de evolucion historica de precios por carta
- Historial de precios en base de datos
- Integracion con CardTrader API (blueprints y precios alternativos)
- Scraper de precios para cartas sin datos de Cardmarket
- Sistema de alertas de variacion de precio
- Perfil de usuario con estadisticas globales de inversion
- Funcionalidades premium con sistema de suscripcion

---

## Autor

**Carlos Ribera Donet**
- GitHub: [CarlosRiberaDonet](https://github.com/CarlosRiberaDonet)
- LinkedIn: [carlos-r-335390276](https://www.linkedin.com/in/carlos-r-335390276/)
- Portfolio: [carlosriberadonet.github.io](https://carlosriberadonet.github.io/Carlos-Ribera/)

---

*Proyecto desarrollado como aplicacion full stack con Java Spring Boot y JavaScript vanilla. Disenado con vision comercial como herramienta SaaS para inversores de Magic: The Gathering.*
