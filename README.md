# Temporis ⏳ v2.0.0
> **Optimización y control del tiempo con accesibilidad universal.**

Temporis es una aplicación Android diseñada para gestionar y optimizar la productividad mediante contadores y temporizadores personalizados. Este proyecto nace como el **Proyecto Final del Ciclo Formativo de Grado Superior en DAM (Desarrollo de Aplicaciones Multiplataforma)**.

## 🚀 Características Principales (MVP)
- **Gestión de Tiempos (CRUD):** Creación, lectura, actualización y borrado de temporizadores vinculados a tareas.
- **Gestión de Usuarios (CRUD):** Creación, lectura, actualización y borrado de alojados en y respaldados mediante los servicios, suministrados por Google, de **Firebase Auth(o Authentication)** y **Firestore Firebase**.
- **Seguridad Biométrica:** Acceso rápido y seguro mediante huella dactilar.
- **Persistencia en la Nube:** Sincronización en tiempo real mediante **Firebase Firestore**.
- **Autenticación Multimodal:** Inicio de sesión con Email/Password y Google Auth.
- **Accesibilidad:** Interfaz adaptada con controles de escala visual y sliders optimizados.
- **Cambio de idioma:** Interfaz disponible en tres idiomas (al menos de momento): Castellano/Español,Catalán/Balear/Valenciano y Inglés/English 

## 🛠️ Tecnologías Utilizadas
- **Lenguajes:** Kotlin & Java.
- **Backend:** Firebase (Auth, Firestore, Storage).
- **Arquitectura:** Patrón MVVM (Model-View-ViewModel).
- **Librerías:** Biometric API, Dexter (Permisos), Material Components, MPAndroidChart.

## 🧪 Testing y Calidad
El proyecto cuenta con una suite de pruebas para garantizar la estabilidad:
- **Unit Tests:** JUnit 4 para la lógica de temporizadores y validaciones.
- **Integration Tests:** Espresso para flujos de navegación y UI.
- **Cobertura:** Enfocada en el ciclo de vida del CRUD principal.

## 📦 Instalación
1. Clona el repositorio:
   ```bash
   git clone [https://github.com/kevinzamoraa/Temporis-Final_Project-DAM.git](https://github.com/kevinzamoraa/Temporis-Final_Project-DAM.git)
   
2. Abre el proyecto en Android Studio.

3. Sincroniza Gradle y ejecuta en un emulador o dispositivo físico.

👤 Autor
**Kevin Zamora Amela** Estudiante de Desarrollo de Aplicaciones Multiplataforma.
