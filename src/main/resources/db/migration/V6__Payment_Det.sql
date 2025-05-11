CREATE TABLE PAYMENT_DETAILS
(
    id   INT AUTO_INCREMENT  PRIMARY KEY,
    payment_id varchar(50),
    created_at timestamp,
    deleted_at timestamp,
    source_account varchar(50),
    destination_account varchar(50),
    amount DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_payment_id FOREIGN KEY (payment_id) REFERENCES PAYMENT(id)
)