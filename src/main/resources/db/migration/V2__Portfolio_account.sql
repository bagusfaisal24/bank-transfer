CREATE TABLE portfolio
(
    id   INT AUTO_INCREMENT  PRIMARY KEY,
    account_id INT NOT NULL,
    created_at timestamp,
    deleted_at timestamp,
    amount DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_account_par FOREIGN KEY (account_id) REFERENCES account_bank(id)
);
