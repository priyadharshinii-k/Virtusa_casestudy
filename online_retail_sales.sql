CREATE DATABASE online_retail;
USE online_retail;



CREATE TABLE Customers (
    customer_id INT PRIMARY KEY,
    name VARCHAR(100),
    city VARCHAR(100)
);

CREATE TABLE Products (
    product_id INT PRIMARY KEY,
    name VARCHAR(100),
    category VARCHAR(100),
    price DECIMAL(10,2)
);

CREATE TABLE Orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    order_date DATE,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);

CREATE TABLE Order_Items (
    order_id INT,
    product_id INT,
    quantity INT,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);



INSERT INTO Customers VALUES
(1, 'Amit', 'Delhi'),
(2, 'Priya', 'Mumbai'),
(3, 'Rahul', 'Chennai'),
(4, 'Sneha', 'Bangalore'),
(5, 'Karan', 'Hyderabad');

INSERT INTO Products VALUES
(101, 'Laptop', 'Electronics', 60000),
(102, 'Phone', 'Electronics', 30000),
(103, 'Shoes', 'Fashion', 2000),
(104, 'Watch', 'Accessories', 5000),
(105, 'Headphones', 'Electronics', 2500);

INSERT INTO Orders VALUES
(1001, 1, '2025-01-10'),
(1002, 2, '2025-01-15'),
(1003, 1, '2025-02-05'),
(1004, 3, '2025-02-20'),
(1005, 4, '2025-03-01');

INSERT INTO Order_Items VALUES
(1001, 101, 1),
(1001, 105, 2),
(1002, 102, 1),
(1003, 103, 3),
(1004, 104, 2),
(1005, 101, 1),
(1005, 102, 1);



1. Top-Selling Products
SELECT p.name, SUM(oi.quantity) AS total_sold
FROM Order_Items oi
JOIN Products p ON oi.product_id = p.product_id
GROUP BY p.name
ORDER BY total_sold DESC;

2. Most Valuable Customers
SELECT c.name, SUM(oi.quantity * p.price) AS total_spent
FROM Customers c
JOIN Orders o ON c.customer_id = o.customer_id
JOIN Order_Items oi ON o.order_id = oi.order_id
JOIN Products p ON oi.product_id = p.product_id
GROUP BY c.name
ORDER BY total_spent DESC;

3. Monthly Revenue
SELECT 
    DATE_FORMAT(o.order_date, '%Y-%m') AS month,
    SUM(oi.quantity * p.price) AS revenue
FROM Orders o
JOIN Order_Items oi ON o.order_id = oi.order_id
JOIN Products p ON oi.product_id = p.product_id
GROUP BY month
ORDER BY month;

4. Category-wise Sales
SELECT p.category, SUM(oi.quantity * p.price) AS total_sales
FROM Order_Items oi
JOIN Products p ON oi.product_id = p.product_id
GROUP BY p.category;

5. Inactive Customers (no orders)
SELECT c.*
FROM Customers c
LEFT JOIN Orders o ON c.customer_id = o.customer_id
WHERE o.order_id IS NULL;

6. Average Order Value
SELECT AVG(order_total) AS avg_order_value
FROM (
    SELECT o.order_id, SUM(oi.quantity * p.price) AS order_total
    FROM Orders o
    JOIN Order_Items oi ON o.order_id = oi.order_id
    JOIN Products p ON oi.product_id = p.product_id
    GROUP BY o.order_id
) t;

7. Best Selling Category
SELECT category, SUM(quantity) AS total_qty
FROM Order_Items oi
JOIN Products p ON oi.product_id = p.product_id
GROUP BY category
ORDER BY total_qty DESC
LIMIT 1;

8. Customer Order Count
SELECT c.name, COUNT(o.order_id) AS total_orders
FROM Customers c
LEFT JOIN Orders o ON c.customer_id = o.customer_id
GROUP BY c.name;

9. Highest Revenue Order
SELECT o.order_id, SUM(oi.quantity * p.price) AS total
FROM Orders o
JOIN Order_Items oi ON o.order_id = oi.order_id
JOIN Products p ON oi.product_id = p.product_id
GROUP BY o.order_id
ORDER BY total DESC
LIMIT 1;


