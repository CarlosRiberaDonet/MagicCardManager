# TCG Data Engine & Analyzer 🃏

Sistema de alto rendimiento para la gestión, persistencia y consulta de datos de mercados de cartas coleccionables (Magic: The Gathering).

## 🛠️ Stack Tecnológico
* **Backend:** Java 21, Spring Boot 3.2+, Spring Data JPA.
* **Base de Datos:** MySQL 8.0 (Indexación optimizada para búsquedas semánticas).
* **Frontend:** Vanilla Architecture (HTML5, CSS3, JavaScript Moderno).
* **Integraciones:** Cardmarket JSON Datasets & TraderCard API.

## 🚀 Desafíos de Ingeniería Resueltos

### 1. Ingesta Masiva de Datos (OOM Prevention)
Implementación de **Jackson Streaming API** (`JsonParser`) para el procesamiento de datasets de gran tamaño. 
* **Logro:** Lectura secuencial que mantiene el consumo de memoria RAM constante (O(1)), evitando errores de `OutOfMemory`.
* **Persistencia:** Sistema de ráfagas (Batch processing) cada 1000 registros para optimizar el throughput de la base de datos.

### 2. Integración de APIs Heterogéneas
El sistema unifica dos modelos de obtención de datos:
* **Batch Loading:** Procesamiento offline de archivos JSON estáticos.
* **Real-time Sync:** Consumo de la API de TraderCard mediante procesamiento de respuestas dinámicas y mapeo a entidades relacionales en caliente.

### 3. Rendimiento en Consultas
Optimización de la capa de persistencia mediante **índices de base de datos** en las columnas de búsqueda, permitiendo respuestas en milisegundos desde el Frontend (JS) al Backend (REST).

## 📋 Roadmap (Próximos Sprints)
- [ ] **Data Consistency:** Implementación de lógica "Upsert" (Update or Insert) para evitar duplicidad de registros en re-importaciones.
- [ ] **Market Analysis:** Integración de gráficas de evolución de precios.
- [ ] **Security:** Implementación de capas de seguridad para la protección de endpoints sensibles.
