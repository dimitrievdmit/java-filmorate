-- Наполнить/обновить таблицы перечислений при запуске. Не работает, если надо удалить строку.
MERGE INTO film_rating AS tbl
USING (VALUES (1, 'G'),
              (2, 'PG'),
              (3, 'PG-13'),
              (4, 'R'),
              (5, 'NC-17')
) AS tmp(id, name)
ON tbl.id = tmp.id
WHEN MATCHED THEN UPDATE SET tbl.name = tmp.name
WHEN NOT MATCHED THEN INSERT (id, name) VALUES (tmp.id, tmp.name);

MERGE INTO genres AS tbl
USING (VALUES (1, 'COMEDY'),
              (2, 'DRAMA'),
              (3, 'CARTOON'),
              (4, 'THRILLER'),
              (5, 'DOCUMENTARY'),
              (6, 'ACTION')
) AS tmp(id, name)
ON tbl.id = tmp.id
WHEN MATCHED THEN UPDATE SET tbl.name = tmp.name
WHEN NOT MATCHED THEN INSERT (id, name) VALUES (tmp.id, tmp.name);

MERGE INTO friend_status AS tbl
USING (VALUES (1, 'CONFIRMED'),
              (2, 'UNCONFIRMED')
) AS tmp(id, name)
ON tbl.id = tmp.id
WHEN MATCHED THEN UPDATE SET tbl.name = tmp.name
WHEN NOT MATCHED THEN INSERT (id, name) VALUES (tmp.id, tmp.name);
