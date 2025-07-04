## 👨‍💻 Prepared By: **Group 5** – Web Engineering Project

| 🔢 No | 👤 Name                              | 🆔 Matric No |
|------:|-------------------------------------|-------------:|
| 1️⃣   | **Khairunniza binti Khairol Anuar** | 291531       |
| 2️⃣   | **Nur Azzyati binti Abu Bakar**     | 291560       |
| 3️⃣   | **Nur Syazwani binti Mohd Muzakir** | 291724       |
| 4️⃣   | **Nur Damia binti Mohd Nazri**      | 291763       |
| 5️⃣   | **Ahmad Fazliyan bin Subli**        | 292631       |

> ✨ *This project is developed as part of our Web Engineering coursework.


# 💼📊 PocketSpend - Expense Tracker and Budgeting App

PocketSpend is a web application designed to help users track their expenses, set budgets, and visualize their spending patterns over time. Built with Spring Boot and MySQL, this application provides a user-friendly interface for managing personal finances.

## 🌟 Key Features

- Expense Tracking
Add and categorize daily expenses with detailed descriptions and amounts.
- Budget Management
Set monthly budgets for different categories (e.g., Food, Transport, Entertainment) and monitor your spending limits in real time.
- Spending Visualization
Interactive charts and graphs to help users analyze where their money goes over days, weeks, or months.
- User-friendly interface
PocketSpend features a clean interface that makes it easy for users to navigate, track expenses, and manage budgets effortlessly.

## 🛠️💻 Technologies Used

- Spring Boot
- MySQL
- HTML/CSS
- Java

## 🚀🔧 Getting Started

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

**Docker Hub Repository:** [bytesync/pocketspend](https://hub.docker.com/r/bytesync/pocketspend-springboot)

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
   - Open your browser and go to [http://localhost:8080/login.html](http://localhost:8080/login.html)

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
