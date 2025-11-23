# SKRT Social Desktop Client

A JavaFX desktop application for SKRT Social that connects to the Symfony API backend.

## Features

- User authentication (login/register)
- News feed with pagination
- Create posts with text and images
- Like and comment on posts
- User profiles
- Edit profile
- Admin dashboard (for admin users)
  - User management
  - Post management

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- SKRT Social API running (default: http://localhost:8000)

## Configuration

The API base URL is configured in `src/main/java/com/example/skrtdesk/util/Constants.java`:

```java
public static final String API_BASE_URL = "http://localhost:8000";
```

Change this if your API is running on a different host/port.

## Building and Running

### Using Maven

1. Build the project:
```bash
mvn clean compile
```

2. Run the application:
```bash
mvn javafx:run
```

### Creating a JAR

To create a standalone JAR:
```bash
mvn clean package
```

## Usage

1. Make sure the Symfony API is running
2. Launch the application
3. Register a new account or login with existing credentials
4. Explore the features:
   - View the news feed
   - Create posts with or without images
   - Like and comment on posts
   - View user profiles
   - Edit your profile
   - Access admin dashboard (if you have admin role)

## Project Structure

```
src/main/java/com/example/skrtdesk/
├── SkrtApplication.java          # Main entry point
├── model/                         # Data models
├── service/                       # API communication
├── controller/                    # JavaFX controllers
├── view/                          # Navigation management
├── util/                          # Utilities
└── exception/                     # Custom exceptions

src/main/resources/
├── fxml/                          # UI layouts
└── css/                           # Stylesheets
```

## Troubleshooting

### Connection Issues

If you can't connect to the API:
1. Verify the API is running: `curl http://localhost:8000/api`
2. Check the API_BASE_URL in Constants.java
3. Ensure no firewall is blocking the connection

### Build Issues

If Maven build fails:
1. Verify Java version: `java -version` (should be 17+)
2. Clean Maven cache: `mvn clean`
3. Update dependencies: `mvn clean install -U`

## Technologies

- JavaFX 17.0.6 - UI framework
- Gson 2.10.1 - JSON parsing
- Apache HttpClient 5.2.1 - HTTP requests
- BootstrapFX 0.4.0 - CSS styling
- Maven - Build tool

