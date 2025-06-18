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