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
graph LR;
    subgraph UI Layer
        direction TB
        Screen[Compose Screen] -- User Action --> ViewModel;
        ViewModel -- State (StateFlow) --> Screen;
    end

    subgraph "ViewModel / Domain Layer"
        direction TB
        ViewModel -- Requests data --> Repository;
    end

    subgraph Data Layer
        direction TB
        Repository -- Fetches/Saves --> ApiService[Remote API (Ktor)];
        Repository -- Fetches/Saves --> Database[Local DB (Room)];
        ApiService -- Updates --> Database;
    end

    UI_Layer --> ViewModel_Domain_Layer;
    ViewModel_Domain_Layer --> Data_Layer;
```
