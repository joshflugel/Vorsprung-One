![Mars Rover Banner](app/src/main/res/drawable/vorsprung_one_banner_jagh.png)

# 🚀 **Vorsprung-One**

[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![OpenGL ES](https://img.shields.io/badge/OpenGL%20ES-5586A4?style=flat&logo=opengl&logoColor=white)](https://www.khronos.org/opengles/)
[![Hilt](https://img.shields.io/badge/Hilt-FF6F00?style=flat&logo=dagger&logoColor=white)](https://dagger.dev/hilt/)
[![Coroutines](https://img.shields.io/badge/Coroutines-0095D5?style=flat&logo=kotlin&logoColor=white)](https://kotlinlang.org/docs/coroutines-overview.html)
[![Architecture](https://img.shields.io/badge/CLEAN%20Architecture-4CAF50?style=flat&logo=android&logoColor=white)](#)

---

## 🌌 Overview

**Vorsprung-One** is a **3D mission control interface** where you visualize a Martian Rover navigating a terrain grid in real time. Built for mobile devices, it offers:

- 🧭 **Interactive controls**: Rotate and zoom the 3D terrain using touch gestures.
- 🚗 **Dynamic path tracking**: The trajectory shifts **from gray to red** to show progress.
- 🖥️ **Split-screen layout**: 3D graphics + live textual rover data.

---

## 🧠 Features

- 🎮 **Real-time 3D OpenGL visualization**
- 🔁 **Smooth screen orientation changes**
- ☑️ **Reactive and stateful UI**
- 🧰 **Mission loading and control buttons**

![Demo](app/src/main/res/drawable/vorsprung_hd.gif)
---

## 🧱 Tech Stack

| Layer        | Technology |
|--------------|------------|
| **Language** | Kotlin |
| **UI**       | Jetpack Compose (Material 3) |
| **Graphics** | Native OpenGL ES 2.0 |
| **DI**       | Hilt |
| **Async**    | Coroutines + Flows |
| **Data**     | Retrofit (mock server), DataStore |
| **Architecture** | CLEAN (3 layers) |
| **Testing**  | JUnit, Mockk, Instrumentation |

---

## 🧩 CLEAN Architecture Breakdown

### 🖼️ **Presentation Layer**
- Single Jetpack Compose screen
- Split UI: **Text Info Area** + **3D Rover Visualization**
- ViewModel handles:
  - Vehicle state
  - Trajectory + camera POV
  - UI reactivity

### 🔧 **Domain Layer**
- Use Cases:
  - `GetInitialMissionUseCase`
  - `EmitMissionSequenceUseCase`
- Functional Programming: Monad-based steering logic
- Core Models + Repository Interface

### 🌐 **Data Layer**
- Repository fetching mission data from a **mock Retrofit server**
- **Predefined missions** with hand-crafted trajectories
- DataStore for persistent map state

---

## 🎨 Design Philosophy

### 📊 **Visualization**
- Simple schematic-style graphics: **Terrain**, **Rover**, and **Trajectory**
- OpenGL selected for:
  - Smooth native performance
  - Touch gesture control
  - Low-level control (at the cost of complexity)

### 🧪 **Test Strategy**
- Unit Tests for use cases and logic
- JUnit4 and mockk
- Manual test checklist to validate UX and flow

### 🤖 **Controls & UX**
- Start Mission 🔁
- Load New Mission 🗺️
- Maintain smooth transitions and performance under screen rotation

---

## 🗺️ Mission Planning

- **Initial mission** is hardcoded with a functional approach using Monads.
- **Improvements**:
  - Data delivery in JSON via mock server
  - Multiple predefined maps
  - No procedural/random generation: curated routes give better behavior and UX

---

## 📜 Full Technical Description

This is a martian rover mission control visualization for Android, called "Vorsprung One", which means advantage or lead, in German. 

The 3D visualization displays the terrain grid, the rover, and its trajectory; and allows the user to zoom or rotate the graphics with touch gestures at any moment, while the rover is idle or moving. The Rover's trajectory changes color, red for the path behind, and gray for the path ahead.

UI controls straightforward: one button starts the mission, and the other button loads a new mission with different map size and vehicle data, in terms of trajectory, bearing, and starting position.

The UI is designed rearrange itself seamlessly when screen orientation changes, while the 3D visualization of the Rover's mission continues to run smoothly, and the text information of the Rover's position and trajectory are preserved.

**Tech stack:** Kotlin, 3D graphics UI made with native OpenGL2, Jetpack Compose Material3, Dependency injection with Hilt, Flows, Coroutines, a mock Retrofit server to receive mission data, and Data Store to persist the mission map.

**Design features:** Layered CLEAN architecture, Functional Programming Monads to execute trajectory maneuvering, stateful UI with MVVM, Unit Tests and Dependency Injection.

---

### CLEAN Architecture implemented in 3 Layers

- **Presentation Layer:** The UI is implemented using a MVVM presentation pattern, and consists of a single screen made with Jetpack Compose that is divided into a Text information Area on one half, and a real time 3D visualization of the rover on the other half.
- **Domain Layer:** One Use Case to obtain initial mission data, and another Use Case to emit a sequence of states in a flow as rover as it executes its maneuvers across the terrain. Also object models, the Monad implementation of Functional Programming to govern steering, and the Repository interface.

- **Data Layer:** A repository connected to a mock Network DataSource modeled with Retrofit to deliver Mission data from a data source that provides a set of predefined Missions that are designed to have appealing map layouts, and trajectories that challenge the Rover's maneuverability as it executes its orders.

---

### Design decisions:

#### Visualization:
Text to display instant rover position, waypoint history, and vehicle orientation.  
OpenGL was chosen for the visualizations to deliver an engaging aesthetic visual experience with its smooth native performance, support for 3D model manipulation combined with touch gestures.  
The tradeoff has in no lesser part dealing with the complexities of low level graphics and largely driven by linear algebra.

A simple visualization based on schematics, consisting of 3 parts: Terrain, Rover, and Trajectory communicates all the information we need to know on this mission in an aesthetic and concise way. Videogame style graphics and models come with complications in terms of proprietary frameworks that are computationally cumbersome, ownership, and added complications of dealing with graphics generated by others, with less return in learning experience as a developer.

#### Responsiveness:
The UI should be able to run the mission while displaying text and 3D graphics smoothly and statefully for a good user experience.

#### UI controls:
Having 3D graphics compels the possibility of adding touch gestures to zoom in/out, to rotate the visualization, all while the mission is taking place on the screen.  
The controls need to cover three basic needs: the need to pause, start a mission, or generate a new mission map.

#### Mission creation:
The starting point is a hardcoded mission that uses monads to calculate a trajectory.  
The improvements chosen were to implement the mission data delivery in JSON format from a mock Retrofit server that could be swapped out for a real server.  
The other improvement was to allow the user to play around with one mission in one map, and have the possibility to load more missions with different maps.  
Preset maps were chosen over randomly generated maps because randomly generated trajectories can be odd or boring, such as a rover that gets caught in corners or bumping against the edge of the plateau.

#### Code Formatting:
Comments in the OpenGL package are especially verbose due to the novelty of this library to most developers, myself included.

---

## 📆 2025  
**Josh Flugel**

