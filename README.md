# PocketSpend - Expense Tracker and Budgeting App

PocketSpend is a web application designed to help users track their expenses, set budgets, and visualize their spending patterns over time. Built with Spring Boot and MySQL, this application provides a user-friendly interface for managing personal finances.

## Features

- Track expenses with detailed descriptions
- Set and manage budgets
- View spending patterns over time
- User-friendly interface

## Technologies Used

- Spring Boot
- MySQL
- HTML/CSS
- Java

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven
- MySQL Server

### Installation

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/PocketSpend.git
   ```

2. Navigate to the project directory:
   ```
   cd PocketSpend
   ```

3. Configure the database:
   - Create a new MySQL database for the application.
   - Update the `src/main/resources/application.properties` file with your database connection details.

4. Build the project:
   ```
   mvn clean install
   ```

5. Run the application:
   ```
   mvn spring-boot:run
   ```

### Accessing the Application

Once the application is running, you can access it at `http://localhost:8080`.

## Usage

- Use the interface to add, view, and delete expenses.
- Set budgets to manage your spending effectively.
- Analyze your spending patterns through the provided visualizations.

## Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue for any enhancements or bug fixes.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Docker

**Docker Hub Repository:** [bytesync/pocketspend](https://hub.docker.com/r/bytesync/pocketspend)

### How to Run with Docker Hub Image

1. **Pull the image from Docker Hub:**
   ```sh
   docker pull bytesync/pocketspend:latest
   ```
2. **Run the container (default port 8080):**
   ```sh
   docker run -p 8080:8080 bytesync/pocketspend:latest
   ```
3. **Access the app:**
   - Open your browser and go to [http://localhost:8080](http://localhost:8080)

---

You can also run the application by building the image locally or using Docker Compose.

### Build and Run Locally

```sh
# Build the Docker image
docker build -t pocketspend .

# Run the Docker container (default port 8080)
docker run -p 8080:8080 pocketspend
```

### Use with Docker Compose

Update your `docker-compose.yml` to use the image:

```yaml
app:
  image: bytesync/pocketspend:latest
  ports:
    - "8080:8080"
  environment:
    SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/pocketspend
    SPRING_DATASOURCE_USERNAME: root
    SPRING_DATASOURCE_PASSWORD: root
    SPRING_JPA_HIBERNATE_DDL_AUTO: update
  depends_on:
    db:
      condition: service_healthy
```