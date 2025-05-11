CREATE TABLE PAYMENT
(
   id   INT AUTO_INCREMENT  PRIMARY KEY,
   transaction_id varchar(50),
   created_at timestamp,
   deleted_at timestamp,
   transaction_type varchar(20),
   total_amount DECIMAL(10, 2) NOT NULL,
   status_authorization varchar(20),
   status_transaction varchar(50)
)