# Plan de Capacitación: Automatización de Pruebas de API con REST Assured

Este repositorio contiene el proyecto base de capacitación y entrenamiento en automatización de pruebas para APIs REST, diseñado para ingenieros de software y QA Automation. El proyecto implementa un framework moderno, robusto y altamente escalable utilizando **Java 17**, **REST Assured**, **JUnit 5**, **AssertJ** y **Allure Report**.

---

## 🎯 Objetivos de la Capacitación

* **Comprender la Arquitectura de Pruebas de API:** Aprender a diseñar frameworks de pruebas desacoplados utilizando patrones de diseño modernos.
* **Dominar REST Assured:** Conocer el manejo de especificaciones de petición (Request) y respuesta (Response), serialización/deserialización, autenticación y aserciones.
* **Aplicar Buenas Prácticas de Ingeniería:** Utilizar patrones como *Data Transfer Objects (DTO)*, *Service Object Model (SOM)*, y generación dinámica de datos fakes para evitar la fragilidad de las pruebas.
* **Reporting Avanzado:** Generar reportes interactivos, registrar trazas de peticiones HTTP en los reportes y configurar variables de entorno para análisis de fallos rápidos.

---

## 🛠️ Tecnologías y Librerías Utilizadas

El framework integra las siguientes herramientas del ecosistema Java:

* **Java 17 (LTS):** Lenguaje base de programación estructurada y tipada.
* **Apache Maven:** Gestor de dependencias y ciclo de vida de compilación/pruebas.
* **JUnit 5 (Jupiter):** Framework de pruebas unitarias y de integración para orquestar la ejecución.
* **REST Assured (v5.4.0):** Librería líder para simplificar la automatización y validación de APIs REST.
* **Allure Report (v2.27.0):** Framework de reportes flexible y enriquecido con captura automática de peticiones y respuestas HTTP (`AllureRestAssured` filter).
* **AssertJ (v3.25.3):** Biblioteca de aserciones fluidas que proporciona mensajes detallados y comparación recursiva de objetos.
* **Lombok (v1.18.30):** Biblioteca para reducir código repetitivo (*boilerplate*) mediante anotaciones como `@Data`, `@Builder` y `@Allure`.
* **DataFaker (v2.5.2):** Generación realista de datos aleatorios en tiempo de ejecución.
* **Jackson (v2.15.2):** Motor para la serialización y deserialización de JSON a DTOs.
* **Java-Dotenv (v5.2.2):** Gestión segura de variables de entorno (por ejemplo, `BASE_URL`, credenciales) a través de un archivo `.env`.

---

## 🏗️ Patrones de Diseño y Arquitectura

El proyecto está estructurado siguiendo principios de código limpio (**Clean Code**) y desacoplamiento absoluto de responsabilidades:

```
src/test/java/com/proyecto/
├── models/                  # Data Transfer Objects (DTOs) - Modelado de datos JSON
│   ├── Pet/                 # DTOs específicos del dominio Pet (Mascotas)
│   │   ├── CategoryDto.java
│   │   ├── PetDto.java
│   │   └── TagDto.java
│   ├── LoginRequest.java
│   ├── UserLogin.java
│   ├── UserRequest.java
│   └── UserResponse.java
├── services/                # Service Object Model (SOM) - Abstracción de clientes API
│   ├── ApiSecurity.java     # Gestión de Tokens y Autenticación
│   ├── ApiServices.java     # Cliente genérico HTTP (GET, POST, PUT, DELETE, PATCH, Multipart)
│   └── utils/               # Utilidades generales
│       └── ApiUtils.java    # Generación de payloads aleatorios con DataFaker
└── tests/                   # Orquestación de pruebas y aserciones
    ├── BaseApiTest.java     # Configuración global de entorno y Specs de REST Assured
    └── ApiTest.java         # Casos de prueba concretos
```

### 1. Service Object Model (SOM)
En lugar de escribir llamadas HTTP directamente en los métodos de prueba, el framework encapsula el comportamiento del cliente API dentro de la clase `ApiServices`. Esto centraliza el manejo del cliente HTTP, facilitando el mantenimiento ante cambios en los endpoints.

### 2. Data Transfer Objects (DTOs)
Cada recurso JSON está fuertemente tipado mediante DTOs ubicados en `com.proyecto.models`. Usamos **Lombok** para generar getters, setters, constructores y constructores fluidos (`@Builder`). Esto permite estructurar payloads complejos de manera legible:
```java
PetDto requestPayload = PetDto.builder()
        .id(12345L)
        .name("Fido")
        .status("available")
        .build();
```

### 3. Especificaciones Globales (Specs)
En `BaseApiTest.java`, se configuran especificaciones compartidas para ahorrar redundancia:
* **RequestSpecification:** Define la `baseUri` obtenida del archivo `.env`, cabeceras comunes (`accept: application/json`) y registra automáticamente las trazas HTTP en Allure.
* **ResponseSpecification:** Valida que todas las respuestas de la API utilicen formato JSON (`ContentType.JSON`) y que el tiempo de respuesta sea óptimo (menos de 5 segundos).

---

## 💡 Ejemplos de Implementación Clave

### A. Pruebas Fuertemente Tipadas y Comparación Recursiva
En `ApiTest.java`, se genera un payload dinámico y se compara directamente el cuerpo de la respuesta con el DTO enviado originalmente mediante AssertJ:

```java
@Test
@DisplayName("Debe crear una mascota exitosamente con datos aleatorios")
public void PostPetReturn200OK() {
    // 1. Generación de datos fakes estructurados
    PetDto requestPayload = ApiUtils.generateRandomPet();
    String endpoint = "/pet";

    // 2. Ejecución de la petición POST a través del SOM
    Response response = ApiServices.post(requestPayload, endpoint);
    
    // 3. Extracción de respuesta y mapeo directo a DTO
    PetDto responseBody = response.then()
            .statusCode(HttpStatus.SC_OK)
            .extract().as(PetDto.class);

    // 4. Aserción profunda/recursiva de objetos completos
    assertThat(responseBody)
            .as("El response debe coincidir exactamente con el payload enviado")
            .usingRecursiveComparison()
            .isEqualTo(requestPayload);
}
```

### B. Generación de Datos con DataFaker
En `ApiUtils.java`, se desacopla la generación de datos de prueba para asegurar que cada ejecución sea independiente:
```java
public static PetDto generateRandomPet() {
    return PetDto.builder()
            .id(faker.number().randomNumber())
            .name(faker.dog().name())
            .status(faker.options().option("available", "pending", "sold"))
            .category(CategoryDto.builder()
                    .id(faker.number().randomNumber())
                    .name(faker.dog().breed()).build())
            .photoUrls(Collections.singletonList("http://example.com/photo.jpg"))
            .tags(Collections.singletonList(TagDto.builder().id(1L).name("cute").build()))
            .build();
}
```

---

## 🚀 Guía de Configuración y Ejecución

### Prerrequisitos
* Java JDK 17 o superior instalado.
* Apache Maven instalado.
* Archivo `.env` configurado en la raíz del proyecto.

### 1. Configuración de Variables de Entorno
Crea o edita el archivo `.env` en la raíz del proyecto con la URL base del entorno a probar:
```env
BASE_URL=https://petstore.swagger.io/v2
```

### 2. Descarga de Dependencias y Compilación
Para inicializar el proyecto y descargar todas las librerías necesarias:
```bash
mvn clean install -DskipTests
```

### 3. Ejecución de Pruebas
Para ejecutar toda la suite de pruebas unitarias e integradas:
```bash
mvn test
```

### 4. Generación de Reportes con Allure

El framework está configurado para almacenar los resultados de las pruebas en la carpeta `target/allure-results`. Para generar y abrir el reporte gráfico interactivo:

* **Generar el Reporte:**
  ```bash
  mvn allure:report
  ```
* **Levantar Servidor para Visualizar el Reporte:**
  ```bash
  mvn allure:serve
  ```
  *(Esto abrirá automáticamente tu navegador predeterminado mostrando un panel interactivo con las métricas, tiempos de respuesta, trazas de peticiones HTTP, cabeceras y payloads de respuesta).*

---

## 📈 Buenas Prácticas Promovidas en este Proyecto

1. **Evitar Hardcoding:** Todas las URLs bases, tokens y configuraciones sensibles se manejan mediante variables de entorno en `.env`.
2. **Abstracción del Cliente HTTP:** Las pruebas no conocen cómo funciona REST Assured internamente; solo interactúan con `ApiServices`, garantizando alta mantenibilidad.
3. **Validación de Rendimiento:** En `BaseApiTest`, se fuerza un límite de tiempo de respuesta (SLA de 5000ms) a nivel global.
4. **Comparación Recursiva:** En lugar de validar campo por campo con múltiples `assertEquals`, se valida el objeto JSON completo deserializado con `usingRecursiveComparison()` de AssertJ.
5. **Autenticación Centralizada:** Soporte para inicios de sesión dinámicos y obtención automatizada de Tokens a través de `ApiSecurity`.

---

## 🔒 Calidad de Código, Git Hooks y CI/CD Automático

Para asegurar la entrega de un código limpio, legible y libre de problemas de seguridad o calidad, se ha implementado un flujo robusto de calidad de código e integración continua.

### 1. Formateo y Estilo de Código (Spotless)
Se utiliza **Spotless** con el estándar oficial de **Google Java Format** para asegurar que todo el código del proyecto tenga una estética y formato uniformes.
* **Verificar formato:**
  ```bash
  mvn spotless:check
  ```
* **Aplicar formato automáticamente:**
  ```bash
  mvn spotless:apply
  ```

### 2. Análisis Estático de Calidad (Qlty)
Se integra **Qlty** como suite de análisis de código para identificar malas prácticas, bugs potenciales y vulnerabilidades:
* Se ha configurado el linter para evitar falsos positivos con **Lombok** (excluyendo la regla de campos privados no utilizados `java:S1068` mediante un bloque `[[triage]]` en `.qlty/qlty.toml`).
* **Ejecutar análisis completo local:**
  ```bash
  qlty check
  ```

### 3. Automatización de Git Hooks Locales
Para asegurar que todo desarrollador cumpla con las políticas de código limpio, el proyecto automatiza la instalación de Git Hooks. **No se requiere ninguna configuración manual**; al ejecutar cualquier compilación de Maven (como `mvn initialize` o `mvn clean install`), el plugin `git-build-hook-maven-plugin` instala los siguientes hooks desde la carpeta `.githooks/` a tu directorio local `.git/hooks/`:

#### 🛡️ `pre-commit` (Formateo y Calidad Local)
* Detecta los archivos `.java` que vas a confirmar (*staged*).
* Ejecuta `mvn spotless:apply` para darles formato de forma automática.
* Agrega los cambios de formato de vuelta al commit actual de manera transparente.
* Ejecuta `qlty check --fail-level medium` **únicamente sobre tus archivos modificados**, garantizando que no introduzcas problemas de calidad de nivel medio o superior, sin bloquearte por problemas preexistentes de código legado.

#### 📝 `commit-msg` (Validación de Conventional Commits)
* Valida que tus mensajes de confirmación sigan el estándar internacional de **Conventional Commits** (ej: `feat(login): agregar inicio de sesión con Google` o `fix: corregir conexión a la base de datos`).
* Si el mensaje no sigue el estándar, cancela el commit de manera segura y te muestra una guía interactiva y colorida en la consola detallando cómo escribir el mensaje correctamente y con ejemplos prácticos listos para copiar.

#### 🚀 `pre-push` (Filtro Crítico de Subida)
* Actúa como una red de seguridad final ejecutando un análisis general de Qlty antes de subir tus ramas al servidor remoto.
* Utiliza `--fail-level high`, por lo que solo bloqueará la subida ante vulnerabilidades graves (ej. fugas de secretos detectadas por `trufflehog` o dependencias vulnerables detectadas por `osv-scanner`). Es un filtro mucho más laxo que el de commit para no entorpecer el flujo diario.

---

### 4. Integración Continua con GitHub Actions (`.github/workflows/ci.yml`)
El repositorio incluye soporte nativo y automático para **GitHub Actions**.
* **Estrategia por Ramas:** El pipeline se ejecuta de forma automática para cada `push` o `pull_request` en las ramas principales (`main`, `master`) y ramas de trabajo (`feature/*`, `bugfix/*`, `hotfix/*`).
* **Caché Inteligente:** Almacena de manera persistente las dependencias de Maven (`~/.m2/repository`) entre ejecuciones para optimizar los tiempos de compilación.
* **Reportes Allure como Artefactos:** Ejecuta la suite de pruebas bajo un runner de Ubuntu con Java 17 (Temurin). Al finalizar, **sin importar si las pruebas pasaron o fallaron**, genera el reporte interactivo HTML de Allure y lo sube como artefacto descargable al flujo del run de GitHub Actions con una retención de 14 días.

### 5. Validación de Contratos OpenAPI (Spectral)
Se utiliza **Spectral** (por Stoplight) para realizar análisis estático (*linting*) de los contratos OpenAPI/Swagger del proyecto (como el contrato `contratoPetStore.yaml`), asegurando el cumplimiento de especificaciones y mejores prácticas de diseño API.
* **Integración en GitHub Actions:** Se ha definido el job `lint` en el pipeline de CI/CD que corre antes de ejecutar los tests.
* **Generación de Reporte de Errores:** Al subir cambios a tu contrato, Spectral analizará el archivo y generará un reporte de texto plano llamado `reports/spectral-errors.txt`.
* **Artefacto Asegurado:** Este archivo se subirá **siempre** como un artefacto descargable en GitHub Actions con el nombre `openapi-lint-report-<rama>`, permitiéndote examinar detalladamente los errores de diseño de la API directamente desde la pestaña "Summary" del flujo de GitHub Actions sin importar si la validación aprobó o falló.


