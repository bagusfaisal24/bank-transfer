CREATE TABLE portfolio_det
(
    id   INT AUTO_INCREMENT  PRIMARY KEY,
    account_id INT NOT NULL,
    porto_id INT NOT NULL,
    created_at timestamp,
    deleted_at timestamp,
    transaction_date timestamp,
    dk VARCHAR(2),
    amount DECIMAL(10, 2) NOT NULL,
    remarks VARCHAR(255),
    CONSTRAINT fk_account_det FOREIGN KEY (account_id) REFERENCES account_bank(id),
        CONSTRAINT fk_port_par FOREIGN KEY (porto_id) REFERENCES portfolio(id)
);
