# 🛒 CartMate – Aplicación de Lista de Mercado

CartMate es una aplicación móvil desarrollada en **Android (Kotlin + Jetpack Compose)** que permite a los usuarios gestionar listas de compras de forma sencilla, intuitiva y moderna.

---

## 📱 Características Principales

* 🔐 **Autenticación local**

  * Registro con validación de correo y contraseña
  * Inicio de sesión

* 📝 **Gestión de listas**

  * Crear listas personalizadas
  * Eliminar listas
  * Categorías con iconos

* 📦 **Gestión de productos**

  * Agregar, editar y eliminar productos
  * Marcar productos como comprados
  * Estilo visual con texto tachado
  * Eliminación con opción de deshacer (Snackbar)

* 🎉 **Experiencia de usuario**

  * Estado vacío amigable
  * Indicador de lista completada
  * Animaciones suaves
  * Feedback visual claro

* 🎨 **Diseño UI/UX**

  * Basado en Figma
  * Material Design 3
  * Modo claro y oscuro
  * Componentes modernos (cards, buttons, navigation)

---

## 🧱 Arquitectura

El proyecto sigue una arquitectura limpia basada en:

* **MVVM (Model - View - ViewModel)**
* **State Management con StateFlow**
* Separación por capas:

```
com.example.cartmate
│
├── data            # Base de datos (Room)
├── navigation      # Navegación entre pantallas
├── ui
│   ├── screens     # Pantallas principales
│   ├── components  # Componentes reutilizables
│   ├── theme       # Colores y estilos
│   └── viewmodel   # Lógica de negocio
```

---

## 🛠️ Tecnologías Utilizadas

* **Kotlin**
* **Jetpack Compose**
* **Material3**
* **Room Database**
* **Navigation Compose**
* **StateFlow / ViewModel**

---

## 🚀 Instalación

1. Clonar el repositorio:

```bash
git clone https://github.com/Cardanivelez/App-Nativa---Lista-De-Mercado.git
```

2. Abrir el proyecto en **Android Studio**

3. Conectar un dispositivo o emulador

4. Ejecutar la app ▶️


---

## 🔒 Validaciones Implementadas

* Correo con formato válido (ej: [usuario@dominio.com](mailto:usuario@dominio.com))
* Contraseña:

  * Mínimo 4 caracteres
  * Debe contener al menos:

    * 1 letra
    * 1 número

---

## 🎯 Funcionalidades Destacadas

* ✔ Persistencia de datos local con Room
* ✔ Gestión completa de listas y productos
* ✔ UI reactiva con Compose
* ✔ Navegación fluida
* ✔ Soporte Light/Dark Mode
* ✔ Feedback visual (Snackbar, animaciones)

---

## 📌 Estado del Proyecto

✅ Funcional
✅ Estable
🔧 Listo para mejoras futuras (Firebase, imágenes, sincronización)

---


## 📄 Licencia

Este proyecto es de uso académico y educativo.
