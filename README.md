# PostApp

For any external user it's only "list of posts and ability to see full information by click".
But for any developer this project is demonstration of usage of different technologies. We use 
modern development practices and a robust architecture to create clean and understandable code.

## Tech Stack

BASE COMPONENTS:
- **Kotlin**: The primary programming language, offering concise syntax and powerful features.
- **Jetpack Compose**: For building the entire UI declaratively, enabling a more efficient and 
- intuitive development process.

TECHNOLOGIES FOR ROBUST CODE STRUCTURE AND MAINTAINING:
- **Navigation Compose**: 
To handle all in-app navigation, providing a seamless and type-safe way to move between screens.
In this project provides navigation between main screen (list of posts) and detail screen (post details).

- **Koin**: 
For dependency injection, simplifying the management of object dependencies and improving modularity.
In this project plays leading role of orchestral conductor for separate areas of responsibility

- **Ktor**: 
As the networking client to fetch data from the remote API. It's a modern, coroutine-based, 
and multiplatform library. In this project helps to get all data from fake API

- **Kotlinx.Serialization**: 
For parsing JSON data from the network into Kotlin objects. Without this block is imposable to convert 
data to objects for code execution

- **Room**: 
For local database persistence, allowing the app to work offline and providing a single source of 
truth for data.

TECHNOLOGIES FOR TESTING
- **JUnit**:
Unit is a testing framework for Java (and Kotlin) used to write and run unit tests.
Using for organize tests and verify that methods / classes return the expected result

- **MockK**:
MockK is a mocking library for Kotlin that works well with JUnit. 
It’s used to simulate (mock) parts of code — like APIs, databases, or other classes.
Gives opportunity to test program logic without depending on real external components.

## Architecture

The application follows the **MVVM (Model-View-ViewModel)** pattern, aligned with a lightweight version 
of **Clean Architecture**. This separates concerns into distinct layers, making the codebase easier to 
understand, test, and maintain.

The main layers are:
- **UI Layer**: Composable screens that observe state from the ViewModels. This layer is responsible 
only for displaying data and capturing user input.
- **Domain/ViewModel Layer**: Contains the business logic. The ViewModels expose state to the UI 
using **UDF (Unidirectional Data Flow)** with **StateFlow**, ensuring a predictable and consistent 
state management.
- **Data Layer**: The `Repository` acts as a **Single Source of Truth** for all application data. It 
implements a **Remote + Local** strategy, fetching data from the network and caching it in the local 
Room database. This ensures the app remains functional even when offline.

### Data Flow Diagram

The diagram below illustrates the flow of data and dependencies between the different architectural layers.

```mermaid
graph TB;
    subgraph UI Layer
        direction TB
        Screen[Compose Screen] -- "User Action" --> ViewModel;
        ViewModel -- "State (StateFlow)" --> Screen;
    end

    subgraph "ViewModel / Domain Layer"
        direction TB
        ViewModel -- "Requests data" --> Repository;
    end

    subgraph Data Layer
        direction TB
        Repository -- "Fetches/Saves" --> ApiService["Remote API (Ktor)"];
        Repository -- "Fetches/Saves" --> Database["Local DB (Room)"];
        graph TB

%% ==== UI LAYER ====
subgraph "🎨 UI Layer (Jetpack Compose)"
    direction TB
    Screen["🖥️ Compose Screen\n(PostList / PostDetail)"]
    Screen -->|User Actions\n(e.g. onClick, refresh)| ViewModel
    ViewModel -->|UI State\n(StateFlow<PostUiState>)| Screen
end

%% ==== DOMAIN / VIEWMODEL LAYER ====
subgraph "🧠 ViewModel / Domain Layer"
    direction TB
    ViewModel["📦 ViewModel (MVVM)\nHolds state + business logic"]
    ViewModel -->|Requests Data / Triggers Refresh| Repository
    Repository -->|Returns Domain Models\n(via Flow / suspend)| ViewModel
end

%% ==== DATA LAYER ====
subgraph "💾 Data Layer"
    direction TB
    Repository["📚 Repository\n(Single source of truth)"]

    ApiService["🌐 ApiService (Ktor)\nRemote source: /posts, /posts/{id}"]
    Database["🗄️ Room Database\nLocal cache (PostDao)"]

    Repository -->|Fetch / Save| ApiService
    Repository -->|Read / Write| Database

    ApiService -->|Network Response\n(PostDto)| Repository
    Database -->|Local Data Flow\n(PostEntity)| Repository
end

%% ==== RELATION BETWEEN REMOTE AND LOCAL ====
ApiService -->|Sync / Cache Updates| Database

%% ==== OPTIONAL FLOW NOTES ====
classDef layer fill:#f8f9fa,stroke:#d0d0d0,stroke-width:1px,color:#222,font-weight:bold;
classDef node fill:#ffffff,stroke:#bcbcbc,stroke-width:1px;
class Screen,ViewModel,Repository,ApiService,Database node;
class UI,ViewModel,Repository,ApiService,Database layer;
ApiService -- "Updates" --> Database;
```
