DELETE FROM user_role;
DELETE FROM users;

INSERT INTO users(id, active, password, username, email)
VALUES(1, true, 'aaaaaa', 'aa', 'aaaa@some.com');
INSERT INTO users(id, active, password, username, email)
VALUES(2, true, 'bbbbbb', 'bb', 'bbbb@some.com');

INSERT INTO user_role(user_id, roles)
VALUES(1, 'USER'), (1, 'ADMIN');
INSERT INTO user_role(user_id, roles)
VALUES(2, 'USER');