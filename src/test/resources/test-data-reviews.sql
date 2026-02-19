MERGE INTO users AS tbl
USING (VALUES (1, 'email1@mail.ru', 'login1', 'Name1', DATE '1990-01-01'),
              (2, 'email2@mail.ru', 'login2', 'Name2', DATE '1995-05-15')
) AS tmp(id, email, login, name, birthday)
ON tbl.id = tmp.id
WHEN MATCHED THEN UPDATE SET tbl.email = tmp.email, tbl.login = tmp.login, tbl.name = tmp.name, tbl.birthday = tmp.birthday
WHEN NOT MATCHED THEN INSERT (email, login, name, birthday) VALUES (tmp.email, tmp.login, tmp.name, tmp.birthday);

MERGE INTO films AS tbl
USING (VALUES (1, 'Film1', 'Description1', TIMESTAMP '2000-01-01 00:00:00+00', 120, 1),
              (2, 'Film2', 'Description2', TIMESTAMP '2010-06-15 00:00:00+00', 90, 2)
) AS tmp(id, name, description, release_date, duration, rating_id)
ON tbl.id = tmp.id
WHEN MATCHED THEN UPDATE SET tbl.name = tmp.name, tbl.description = tmp.description, tbl.release_date = tmp.release_date, tbl.duration = tmp.duration, tbl.rating_id = tmp.rating_id
WHEN NOT MATCHED THEN INSERT (name, description, release_date, duration, rating_id) VALUES (tmp.name, tmp.description, tmp.release_date, tmp.duration, tmp.rating_id);

MERGE INTO reviews AS tbl
USING (VALUES (1, 1, 1, 'Content1', TRUE),
              (2, 2, 1, 'Content2', FALSE),
              (3, 1, 2, 'Content3', TRUE),
              (4, 2, 2, 'Content4', TRUE),
              (5, 1, 1, 'Content5', FALSE),
              (6, 2, 2, 'Content6', TRUE)
) AS tmp(id, user_id, film_id, content, positive)
ON tbl.id = tmp.id
WHEN MATCHED THEN UPDATE SET tbl.content = tmp.content, tbl.positive = tmp.positive
WHEN NOT MATCHED THEN INSERT (id, user_id, film_id, content, positive) VALUES (tmp.id, tmp.user_id, tmp.film_id, tmp.content, tmp.positive);

MERGE INTO review_rating AS tbl
USING (VALUES (2, 1, TRUE),
              (1, 6, TRUE)
) AS tmp(user_id, review_id, positive)
ON tbl.user_id = tmp.user_id AND tbl.review_id = tmp.review_id
WHEN MATCHED THEN UPDATE SET tbl.positive = tmp.positive
WHEN NOT MATCHED THEN INSERT (user_id, review_id, positive) VALUES (tmp.user_id, tmp.review_id, tmp.positive);