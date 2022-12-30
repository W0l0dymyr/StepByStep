DELETE FROM user_role;
DELETE FROM users;

INSERT INTO users(id, active, password, username, email, best_result)
VALUES(1, true, 'aaaaaa', 'aa', 'aaaa@some.com', 0);
INSERT INTO users(id, active, password, username, email, best_result)
VALUES(2, true, 'bbbbbb', 'bb', 'bbbb@some.com', 0);

INSERT INTO user_role(user_id, roles)
VALUES(1, 'USER'), (1, 'ADMIN');
INSERT INTO user_role(user_id, roles)
VALUES(2, 'USER');