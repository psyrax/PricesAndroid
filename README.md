# 🃏 PokéPrices Android

**Consulta y organiza precios de cartas Pokémon TCG desde tu Android.**

Port completo de la app iOS [PokéPrices](https://github.com/psyrax/Prices) a Android nativo con Kotlin y Jetpack Compose.

---

## ✨ Características

| Función | Descripción |
|---------|-------------|
| 🔍 **Búsqueda de precios** | Busca cartas por nombre y expansión usando la API de JustTCG |
| 💰 **Conversión USD → MXN** | Precios convertidos automáticamente con tipo de cambio en tiempo real |
| 📋 **Lista de venta** | Organiza las cartas que tienes en venta |
| 🛒 **Lista de deseos** | Lleva el control de las cartas que quieres comprar |
| ✏️ **Editor de cartas** | Edita nombre, expansión, precio, imagen y más |
| 📡 **NFC Deep Links** | Escanea tags NFC con esquema `ogl://card?id=X` para abrir cartas |
| 🖼️ **Imágenes HD** | Carga asíncrona de imágenes de cartas con Coil |

## 📱 Screenshots

<p align="center">
  <em>¡Próximamente!</em>
</p>

## 🏗️ Arquitectura

```
app/src/main/java/com/psyrax/pokeprices/
├── data/
│   ├── local/          # Room DB, DAOs, DataStore
│   ├── model/          # Entidades (Carta, CartaVariant, GameSet)
│   ├── remote/         # Retrofit APIs, DTOs
│   └── repository/     # Repositorios (CartaRepository, CurrencyRepository)
├── ui/
│   ├── navigation/     # NavHost, Screen sealed class
│   ├── screens/        # Composables (Search, Lists, Detail, Edit, Settings)
│   ├── theme/          # Material3 Theme
│   ├── util/           # PriceFormatter
│   └── viewmodel/      # ViewModels (MVVM)
├── MainActivity.kt     # Entry point + deep link handling
└── PokePricesApp.kt    # Application class + singletons
```

**Patrón:** MVVM con Repository pattern

## 🛠️ Tech Stack

| Tecnología | Uso |
|-----------|-----|
| **Kotlin 1.9** | Lenguaje principal |
| **Jetpack Compose** | UI declarativa (Material3) |
| **Room 2.6** | Base de datos local (SQLite) |
| **Retrofit 2.9** | Cliente HTTP para APIs |
| **OkHttp 4.12** | HTTP client + logging |
| **Coil 2.5** | Carga de imágenes asíncrona |
| **DataStore** | Preferencias (API key, tipo de cambio) |
| **Navigation Compose** | Navegación entre pantallas |
| **Coroutines + Flow** | Programación asíncrona y reactiva |
| **KSP** | Procesamiento de anotaciones (Room) |

## 📋 Requisitos

- **Android SDK 34** (Android 14)
- **Min SDK 26** (Android 8.0+)
- **JDK 17**
- **Gradle 8.5**

## 🚀 Setup & Build

### 1. Clonar el repo

```bash
git clone <repo-url>
cd PricesAndroid
```

### 2. Configurar entorno

```bash
# Asegúrate de tener JDK 17 y Android SDK
export JAVA_HOME="/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home"
export ANDROID_HOME="$HOME/Library/Android/sdk"
```

### 3. Build debug

```bash
./gradlew assembleDebug
# APK en: app/build/outputs/apk/debug/app-debug.apk
```

### 4. Build release (firmado)

```bash
./gradlew assembleRelease
# APK en: app/build/outputs/apk/release/app-release.apk
```

### 5. Instalar en dispositivo/emulador

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

## 🔑 Configuración de la App

1. Abre la app y ve a la pestaña **⚙️ Configuración**
2. Ingresa tu **API Key** de [JustTCG](https://justtcg.com)
3. Toca **Refrescar Expansiones** para cargar los sets disponibles
4. Toca **Actualizar Tipo de Cambio** para obtener USD → MXN actual
5. ¡Listo! Ve a **🔍 Buscar** y empieza a consultar precios

## 🔗 APIs utilizadas

| API | Endpoint | Uso |
|-----|----------|-----|
| **JustTCG** | `api.justtcg.com/v1` | Búsqueda de cartas, sets, precios |
| **Open Exchange Rates** | `open.er-api.com/v6/latest/USD` | Tipo de cambio USD → MXN |

## 📄 Equivalencias iOS → Android

| iOS (Swift) | Android (Kotlin) |
|------------|-------------------|
| SwiftUI | Jetpack Compose |
| SwiftData | Room Database |
| URLSession | Retrofit + OkHttp |
| AsyncImage | Coil AsyncImage |
| @AppStorage | DataStore Preferences |
| NavigationStack | Navigation Compose |
| @Observable | ViewModel + StateFlow |
| .searchable | OutlinedTextField |

## 📝 Licencia

Proyecto personal de [Psyrax](https://github.com/psyrax).
