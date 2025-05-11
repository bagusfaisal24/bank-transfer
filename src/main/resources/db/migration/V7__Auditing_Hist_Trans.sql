CREATE TABLE LOG_HIST_PAYMENT
(
    id   INT AUTO_INCREMENT  PRIMARY KEY,
    created_at timestamp,
    deleted_at timestamp,
    transaction_id varchar(50),
    status_authorization varchar(20),
    status_transaction varchar(50)
)