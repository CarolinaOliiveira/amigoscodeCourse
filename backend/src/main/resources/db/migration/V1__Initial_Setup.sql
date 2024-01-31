CREATE TABLE customer(
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    age INT NOT NULL,
    gender TEXT NOT NULL
);

alter table customer
    add constraint customer_email_unique unique (email);
