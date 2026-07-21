# Manual Técnico — Multi-Timer Food v1.0

---

## 1. Descripción del sistema

**Multi-Timer Food** (también referenciada como *MultiKitchen Timers*) es una aplicación Android que permite a un usuario crear y gestionar **múltiples temporizadores de cocina simultáneos**, con notificaciones personalizadas por sonido, autenticación local, categorías, presets de comida y un historial de temporizadores completados.

- **Problema que resuelve:** en una cocina real es común preparar varios platillos a la vez (ej. arroz + carne + salsa), cada uno con su propio tiempo de cocción. Un temporizador único de celular no permite monitorear varios procesos en paralelo, obligando al usuario a recordar tiempos de memoria o usar múltiples apps.
- **Usuario objetivo:** personas que cocinan en casa y necesitan controlar tiempos de varias preparaciones al mismo tiempo, sin conocimientos técnicos previos.
- **Alcance del MVP:** creación y gestión de temporizadores múltiples con notificación individual, categorías predefinidas, presets de alimentos comunes, historial de temporizadores ejecutados, y autenticación local (registro/login) para persistencia de datos por usuario. Quedan fuera del MVP: sincronización en la nube, temporizadores compartidos entre dispositivos, y recomendaciones basadas en IA.

---

## 2. Arquitectura de la aplicación

La aplicación sigue el patrón **MVVM (Model-View-ViewModel)**, combinando **Jetpack Compose** para las pantallas principales de la app y **XML Views** para las pantallas de Login/Registro.

### 2.1 Diagrama de capas

```
┌──────────────────────────────────────────────────────────┐
│                        UI LAYER                            │
│  ┌────────────────────┐   ┌───────────────────────────┐   │
│  │  Jetpack Compose    │   │   XML Views (Activities)  │   │
│  │  (CreateTimerScreen,│   │   Login / Register         │   │
│  │  TimerListScreen,   │   │                             │   │
│  │  SoundSelectorSheet)│   │                             │   │
│  └─────────┬───────────┘   └──────────────┬──────────────┘   │
│            │  observa StateFlow/LiveData   │                │
└────────────┼────────────────────────────────┼────────────────┘
             │                                │
┌────────────▼────────────────────────────────▼────────────────┐
│                      LÓGICA (ViewModel)                        │
│   TimerViewModel · CategoryViewModel · FoodPresetViewModel      │
│   HistoryViewModel · UserViewModel (login/registro)             │
│   → expone estado a la UI y delega en los Repositories          │
└────────────┬─────────────────────────────────────────────────┘
             │
┌────────────▼─────────────────────────────────────────────────┐
│                    REPOSITORIOS (dominio)                       │
│  TimerRepository · CategoryRepository · FoodPresetRepository    │
│  HistoryRepository · UserRepository                             │
│  → usan safeDbCall/DbResult<T> para manejo uniforme de errores  │
└────────────┬─────────────────────────────────────────────────┘
             │
┌────────────▼─────────────────────────────────────────────────┐
│                     DATOS (Room Database)                       │
│  Entidades: timers · categories · food_presets · timer_history  │
│  DAO por entidad + Room como fuente única de verdad              │
└──────────────────────────────────────────────────────────────┘

              (transversal, no es una capa más)
┌──────────────────────────────────────────────────────────────┐
│  WorkManager: TimerNotificationScheduler → TimerFinishedWorker  │
│  Canales de notificación dinámicos por sonido (Android 8+)      │
│  InAppAlarmPlayer: feedback de alarma en primer plano            │
│  PasswordHasher (PBKDF2WithHmacSHA256): seguridad de credenciales│
└──────────────────────────────────────────────────────────────┘
```

### 2.2 Patrón de diseño: MVVM

- **Model:** entidades Room (`Timer`, `Category`, `FoodPreset`, `TimerHistory`) y las clases de dominio derivadas (ej. `ActiveTimer`, respaldado por la tabla `timers` con reconciliación de tiempo transcurrido).
- **View:** pantallas Compose (declarativas, observan estado) + Activities XML para Login/Registro.
- **ViewModel:** expone estado observable a la UI y coordina llamadas a los repositorios; no contiene lógica de acceso a datos directa.
- **Repository:** capa intermedia entre ViewModel y Room, responsable de exponer operaciones CRUD envueltas en `DbResult<T>` mediante el wrapper `safeDbCall`, garantizando manejo de errores consistente en toda la app.

### 2.3 Justificación de decisiones de arquitectura

- Se optó por una arquitectura **híbrida Compose + XML** porque el curso exige demostrar ambos enfoques; Login/Registro se mantienen en XML como ejercicio de Views tradicionales.
- Las configuraciones de alerta (sonido, vibración) se incorporaron directamente en la entidad `timers` en lugar de una tabla separada, priorizando simplicidad de MVP sobre normalización formal.
- Se usa `WorkManager` para las notificaciones porque era un requisito académico explícito, aunque `AlarmManager` habría ofrecido mayor precisión en temporizadores exactos (limitación conocida y documentada).

---

## 3. Modelo de datos

### 3.1 Diagrama entidad-relación

```
┌───────────────────┐        ┌───────────────────┐
│    categories      │        │   food_presets      │
├───────────────────┤        ├───────────────────┤
│ PK id               │        │ PK id                │
│    nombre           │◄───┐   │    nombre           │
│    color            │    │   │    tiempoDefault    │
│    icono            │    │   │ FK categoriaId ─────┼──┐
└───────────────────┘    │   └───────────────────┘  │
                            │                              │
                            │   ┌───────────────────┐  │
                            └──►│      timers         │◄─┘
                                ├───────────────────┤
                                │ PK id                │
                                │    nombre           │
                                │    duracionTotal    │
                                │    tiempoRestante   │
                                │    estado           │
                                │    sonidoId         │
                                │    vibracionActiva  │
                                │ FK categoriaId       │
                                │ FK presetId (nullable)│
                                │    fechaCreacion    │
                                └─────────┬─────────┘
                                          │ 1
                                          │
                                          │ N
                                ┌─────────▼─────────┐
                                │   timer_history     │
                                ├───────────────────┤
                                │ PK id                │
                                │ FK timerId           │
                                │    nombreTimer       │
                                │    duracionTotal    │
                                │    fechaFinalizacion │
                                │    completadoOk      │
                                └───────────────────┘
```

### 3.2 Descripción de relaciones y claves

| Entidad | Clave primaria | Claves foráneas | Relación |
|---|---|---|---|
| `categories` | `id` | — | 1 categoría → N timers |
| `food_presets` | `id` | `categoriaId` (opcional) | 1 preset → N timers |
| `timers` | `id` | `categoriaId`, `presetId` (nullable) | Entidad central; referencia a categoría y opcionalmente a un preset |
| `timer_history` | `id` | `timerId` | 1 timer → N registros de historial (se conserva histórico aunque el timer original se elimine, según diseño) |

### 3.3 Notas de diseño

- La tabla `timers` concentra también los datos de configuración de alerta (sonido, vibración) para mantener el modelo simple en el MVP, evitando una tabla adicional de configuración.
- `ActiveTimer` (clase de dominio en memoria) se sincroniza con la fila correspondiente en `timers`, reconciliando el tiempo transcurrido cuando la app se reabre o el proceso se recrea.

---

## 4. Tecnologías y librerías

| Categoría | Tecnología | Versión |
|---|---|---|
| Lenguaje | Kotlin | gestionado vía `libs.versions.toml` (Version Catalog); |
| Framework UI | Jetpack Compose (pantallas principales) + Android Views/XML con ViewBinding (Login, Registro) | BOM de Compose gestionado vía `libs.androidx.compose.bom` |
| Arquitectura | MVVM | — |
| Base de datos | Room (`room-runtime`, `room-ktx`, `room-compiler` vía KAPT) | 2.6.1 |
| Anotaciones | KAPT (`kotlin-kapt`) para el procesador de anotaciones de Room | — |
| Tareas en segundo plano | WorkManager (`work-runtime-ktx`) | 2.9.1 |
| ViewModel en Compose | `lifecycle-viewmodel-compose` | 2.6.1 |
| Diseño visual | Material Design 3 (`androidx.compose.material3`) + `material-icons-extended` | — |
| Compatibilidad Java | `JavaVersion.VERSION_11` (source/target compatibility y `jvmTarget`) | 11 |
| Seguridad | PBKDF2WithHmacSHA256 (`PasswordHasher.kt`, implementación propia, sin librería externa) | — |
| Testing | JUnit (unitarias), Room in-memory (`room-testing` 2.6.1), Espresso (`espresso-core`, `compose-ui-test-junit4`) | ver `androidTestImplementation` en Gradle |
| Ofuscación | ProGuard (`isMinifyEnabled = false` en `release`, reglas en `proguard-rules.pro`) | — |

**Paleta de colores del proyecto (Material Design 3, tema personalizado):**

- Primario: `#E8842E` / `#E67E22` (naranja)
- Secundario: `#6B8E23` (verde oliva)
- Fondo: `#FFF8F0` / `#FCF1E7` (crema)

> Nota técnica: `dynamicColor` debe permanecer en `false` dentro de `Theme.kt` para evitar que Android 12+ sobreescriba la paleta personalizada con colores derivados del wallpaper del usuario.

---

## 5. Instrucciones para compilar

### 5.1 Requisitos previos

- **Android Studio:** versión compatible con `compileSdk = 37` 
- reciente de Android Studio, ej. Narwhal o superior)
- **JDK:** 11 (definido explícitamente en `compileOptions` y `kotlinOptions.jvmTarget` del proyecto)
- **SDK mínimo (minSdk):** 24 (Android 7.0 Nougat)
- **SDK objetivo (targetSdk):** 37
- **compileSdk:** 37
- **applicationId / namespace:** `com.yuquilema.multi_timerfood`
- **versionName / versionCode actuales:** 1.0 / 1
- **Gradle:** el proyecto usa el Gradle Wrapper incluido en el repositorio, no requiere instalación manual

### 5.2 Pasos para compilar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Kev5418/Multi-Timer-Food.git
   ```
2. Abrir el proyecto en Android Studio (`Open` → seleccionar la carpeta clonada).
3. Esperar la sincronización automática de Gradle, o forzarla manualmente:
   - `File` → `Sync Project with Gradle Files`
4. Verificar que el plugin `kotlin-kapt` esté aplicado (requerido por el procesador de anotaciones de Room).
5. Seleccionar un emulador o dispositivo físico con Android 8.0 (API 26) o superior — requisito mínimo por el uso de canales de notificación dinámicos.
6. Ejecutar la app: botón **Run** (▶) o `Shift + F10`.

### 5.3 Variables de entorno / configuración necesaria

- Esta aplicación **no depende de servicios externos** (no usa Firebase ni APIs REST en su MVP), por lo que **no requiere** `google-services.json` ni claves de API para compilar o ejecutar.
- Permisos declarados en `AndroidManifest.xml` que el usuario debe aceptar en tiempo de ejecución (Android 13+):
  - `POST_NOTIFICATIONS` (obligatorio para mostrar alertas de temporizador)
  - `VIBRATE` 

---

## 6. Estructura del repositorio

```
Multi-Timer-Food/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yuquilema/multi_timerfood/
│   │   │   │   ├── data/
│   │   │   │   │   ├── entity/          # Timer, Category, FoodPreset, TimerHistory
│   │   │   │   │   ├── dao/             # DAOs de Room
│   │   │   │   │   └── AppDatabase.kt   # Configuración de Room
│   │   │   │   ├── repository/          # TimerRepository, CategoryRepository, etc.
│   │   │   │   ├── viewmodel/           # TimerViewModel, CategoryViewModel, etc.
│   │   │   │   ├── ui/
│   │   │   │   │   ├── compose/         # Pantallas Compose (CreateTimerScreen, etc.)
│   │   │   │   │   └── xml/             # Activities de Login/Registro
│   │   │   │   ├── notifications/       # TimerFinishedWorker, TimerNotificationScheduler
│   │   │   │   ├── security/            # PasswordHasher.kt
│   │   │   │   └── util/                # DbResult, safeDbCall
│   │   │   ├── res/                     # Layouts XML, drawables, valores de tema
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                        # Pruebas unitarias (JUnit)
│   │   └── androidTest/                 # Pruebas de integración (Room in-memory) y UI (Espresso)
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
└── README.md
```


---

## 7. Historial de versiones

| Versión | Fecha | Contenido |
|---|---|---|
| v1.0 | 24/07/2026 | MVP completo: creación y gestión de múltiples temporizadores simultáneos, categorías, presets de comida, historial de temporizadores, autenticación local con hash seguro de contraseñas (PBKDF2), notificaciones con canales dinámicos por sonido, selector de sonido con vista previa en vivo, manejo uniforme de errores (`DbResult`/`safeDbCall`), persistencia completa vía Room. |

---

*Documento generado para el repositorio [Multi-Timer-Food](https://github.com/Kev5418/Multi-Timer-Food).*
