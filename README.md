# Temporis ⏳ v2.1.1
> **Optimización y control del tiempo con accesibilidad universal nativa.**

Temporis es una aplicación Android diseñada para gestionar la productividad mediante temporizadores personalizados. Este proyecto ha sido desarrollado como el **Proyecto Final del Ciclo Formativo de Grado Superior en DAM (Desarrollo de Aplicaciones Multiplataforma)**.

## 🚀 Características Principales (Versiones anteriores)
- **Gestión de Tiempos (CRUD):** Control total sobre tus tareas con persistencia en tiempo real.
- **Analítica Visual:** Gráficos de contribución anual para monitorizar el progreso y rachas de productividad.
- **Accesibilidad Total:** Interfaz dinámica con escalado de fuente, alto contraste y diseño reactivo que se adapta incluso con el teclado desplegado.
- **Seguridad Biométrica:** Autenticación de última generación mediante la Biometric API de Android.
- **Sincronización Cloud:** Respaldado por el ecosistema de Google (**Firebase Auth, Firestore y Storage**).
- **Multilingüe:** Traducido íntegramente a Castellano, Catalán e Inglés.

---

## 🚀 Novedades de la Release v2.1.1
- **Estabilización del Motor Asíncrono:** Optimización en el manejo de hilos y corrutinas de Kotlin para evitar pérdidas de memoria en temporizadores en segundo plano.
- **Refactorización de Capas:** Ajustes finos en las entidades heredadas de Java integradas en los ViewModels de Kotlin (Arquitectura MVVM).
- **Aseguramiento de Calidad:** Actualización del pipeline automatizado y solución de bugs menores detectados por el análisis estático.

---

## 🛠️ Tecnologías y Arquitectura
- **Lenguajes:** Kotlin & Java (Interoperabilidad nativa).
- **Arquitectura:** Patrón **MVVM** (Model-View-ViewModel) + **Clean Architecture** estructurada con una separación estricta de responsabilidades (**Presentación** (UI (Vistas / _Activities_ / _Components_) y **Gestores de Estado** (_ViewModels_ / _Presenters_)), Dominio (Entidades (_Entities_) y Interfaces de Repositorio (_Repositories_)) y **Datos** (Repositorios (_Repositories_) y Fuentes de datos (_Data Sources_))).
- **Persistencia y Backend:** Arquitectura en la nube NoSQL basada en **Firebase Firestore** (estructuras colecciones/subcolecciones de acceso eficiente), **Firebase Auth** y **Firebase Storage**.
- **Seguridad Nivel Sistema:** Autenticación biométrica nativa mediante la `Biometric API` de Android con fallback seguro a PIN/Patrón.
- **Concurrencia:** `Kotlin Coroutines` para operaciones asíncronas fluidas de lectura/escritura sin bloquear el hilo de interfaz (UI Thread).
- **Componentes Visuales:** Material Design 3 con implementación estricta de *Design Tokens* dinámicos.

- **Librerías Clave:** - `MPAndroidChart` para la visualización de datos.
  - `Biometric API` para la seguridad.
  - `Coroutines` para procesos asíncronos.
 
---

## ♿ Accesibilidad Universal (WCAG AAA)
El núcleo de Temporis está diseñado para romper barreras digitales, implementando de manera nativa:
- **Modo Oscuro Puro:** Fondo `#000000` y fuentes `#FFFFFF` que garantizan un contraste superior a `7:1` (Cumplimiento WCAG AAA).
- **Escalado Dinámico:** Soporte nativo para fuentes grandes de hasta `30sp` sin romper la distribución espacial de la interfaz.
- **Navegación Asistida:** Integración estructural completa con el lector de pantalla **TalkBack** mediante descripciones de contenido explícitas.

---

## 🧪 Calidad, Integración Continua y Testing
- **Análisis Estático (Clean Code):** Integración con **SonarCloud** para garantizar un *Quality Gate* del 100% libre de deuda técnica y código duplicado.
- **Pruebas de Software:**
  - **Unit Tests:** Cobertura de la lógica de negocio y validación de repositorios con JUnit 4.
  - **Métricas de Cobertura:** Automatización del reporte de cobertura estimada (~85%) mediante el plugin de **JaCoCo**.
- **Pipeline CI/CD:** Automatización de compilación y ejecución de tests en cada commit a través de **GitHub Actions**.
- **Clean Code:** Estructura de paquetes organizada por funcionalidades (ui, model, viewmodel, repository).

---

## 📦 Instalación y Despliegue
1. Clona el repositorio:
```bash
   git clone [https://github.com/kevinzamoraa/Temporis-Final_Project-DAM.git](https://github.com/kevinzamoraa/Temporis-Final_Project-DAM.git)
```
   
2. Abre el proyecto en Android Studio.

3. Sincroniza Gradle y ejecuta en un emulador o dispositivo físico.

---

👤 Autor
**Kevin Zamora Amela**
Desarrollador Multiplataforma, _Técnico Superior en Automatización y Robótica Industrial_ y _Técnico Superior en Desarrollo de Aplicaciones Multiplataforma_.

- **LinkedIn**: [kevin-zamora-webdev](https://www.linkedin.com/in/kevin-zamora-webdev/)
- **GitHub Profile**: [@kevinzamoraa](https://github.com/kevinzamoraa)
