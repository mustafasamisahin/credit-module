# Credit Module API

A Spring Boot application that manages customer credits, loans, and installment payments.

## Features

### Customer Management

- Create new customers with credit limits
- Retrieve customer information
- Update customer details
- Delete customers
- Track used credit limits

### Loan Management

- Create loans with flexible installment plans
- View loans by customer
- Track loan payments and status
- Support for different interest rates
- Automatic installment generation

### Installment Handling

- Automatic installment scheduling
- Track payment status
- Support partial and full payments
- View installment details per loan

## API Endpoints

### Customer Endpoints

```http
POST /api/customers # Create a new customer
GET /api/customers/{id} # Get customer by ID
GET /api/customers # List all customers
PUT /api/customers/{id} # Update customer details
DELETE /api/customers/{id} # Delete a customer
```

### Loan Endpoints

```http
POST /api/loans # Create a new loan
GET /api/loans # List loans by customer
GET /api/loans/{id}/installments # Get loan installments
POST /api/loans/{id}/pay # Make a payment on a loan
```

## Usage Examples

### 1. Creating a Customer

```http
POST /api/customers
Content-Type: application/json
{
"name": "Sami",
"surname": "Sahin",
"creditLimit": 10000.00,
"usedCreditLimit": 0.00
}
```

### 2. Creating a Loan

```http
POST /api/loans?customerId=1&amount=5000&interestRate=0.15&numberOfInstallments=12
```

- `customerId`: Customer's ID
- `amount`: Loan amount
- `interestRate`: Between 0.1 and 0.5 (10% to 50%)
- `numberOfInstallments`: Must be 6, 9, 12, or 24

### 3. Making a Payment

```http
POST /api/loans/1/pay?amount=500.00
```

## Business Rules

### Credit Limits

- Customers have a maximum credit limit
- Used credit cannot exceed the total credit limit
- Credit limit is automatically adjusted when loans are created or paid

### Loan Rules

- Interest rates must be between 10% and 50%
- Available installment periods: 6, 9, 12, or 24 months
- Installments are automatically scheduled for the 1st of each month
- Payments are applied to the earliest due installments first

### Payment Processing

- Payments are processed in chronological order
- Partial payments are not allowed for individual installments
- System tracks paid and unpaid installments
- Only installments due within the next 3 months can be paid

## Technical Details

Built with:

- Spring Boot
- Java 17+
- Lombok
- ModelMapper
- Spring Data JPA

## Error Handling

The API includes validation for:

- Invalid credit limits
- Insufficient credit availability
- Invalid interest rates
- Invalid installment periods
- Customer not found scenarios
- Loan not found scenarios

## Getting Started

1. Clone the repository
2. Run the Spring Boot application
3. Use the API endpoints to manage customers and loans