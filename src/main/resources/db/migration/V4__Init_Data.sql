insert into account_bank(account_holder, account_number)
values ('Si Jonny', '1');
insert into account_bank(account_holder, account_number)
values ('Si Papa', '2');
insert into account_bank(account_holder, account_number)
values ('Si Ocil', '3');
insert into portfolio(account_id, created_at, amount)
values (1, current_timestamp, 10000000);
insert into portfolio_det(account_id, porto_id, created_at, transaction_date, dk, amount, remarks)
values (1, 1, current_timestamp, current_timestamp, 'D',10000000,'Nabung Awal');