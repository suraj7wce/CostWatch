# CostWatch
Java-MySQL application for maintaining and monitoring personal expenses

Requirements: Java SDK, MySQL Shell/Workbench, JDBC MySQL driver, IDE/Text Editor.

Schema:

CREATE DATABASE expenses;
USE expenses;

CREATE TABLE expense_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE,
    description VARCHAR(255),
    amount DECIMAL(10, 2)
);

