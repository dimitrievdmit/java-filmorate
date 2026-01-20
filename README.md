# java-filmorate

## ER-диаграмма:

![ER-диаграмма](/src/main/resources/er_diagram.png)

# Пояснение к диаграмме.

## Таблица `films` (Фильмы)

- **`id`** — уникальный идентификатор фильма.  
  Тип: `integer` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`name`** — название фильма.  
  Тип: `varchar` (строковый).

- **`description`** — описание фильма.  
  Тип: `varchar` (строковый).

- **`release_date`** — дата выхода фильма.  
  Тип: `date` (дата).

- **`duration`** — длительность фильма в минутах.  
  Тип: `integer` (целочисленный).

- **`genre_id`** — ссылка на жанр фильма.  
  Тип: `integer` (целочисленный).  
  Связь: внешний ключ к таблице `film_genre` (`id`).

- **`rating_id`** — ссылка на рейтинг фильма.  
  Тип: `integer` (целочисленный).  
  Связь: внешний ключ к таблице `film_rating` (`id`).

## Таблица `film_likes` (Лайки фильмов)

- **`film_id`** — идентификатор фильма, которому поставлен лайк.  
  Тип: `integer` (целочисленный).  
  Связь: внешний ключ к таблице `films` (`id`).

- **`user_id`** — идентификатор пользователя, поставившего лайк.  
  Тип: `integer` (целочисленный).  
  Связь: внешний ключ к таблице `users` (`id`).

## Таблица `film_genre` (Жанры фильмов)

- **`id`** — уникальный идентификатор жанра.  
  Тип: `integer` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`name`** — название жанра.  
  Тип: `genre` (перечисляемый).  
  Возможные значения:
    - `COMEDY` (комедия)
    - `DRAMA` (драма)
    - `CARTOON` (мультфильм)
    - `THRILLER` (триллер)
    - `DOCUMENTARY` (документальный)
    - `ACTION` (боевик)

## Таблица `film_rating` (Рейтинги фильмов)

- **`id`** — уникальный идентификатор рейтинга.  
  Тип: `integer` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`name`** — значение рейтинга.  
  Тип: `rating` (перечисляемый).  
  Возможные значения:
    - `G` (для всех возрастов)
    - `PG` (рекомендуется присутствие родителей)
    - `PG_13` (детям до 13 лет просмотр нежелателен)
    - `R` (лицам до 17 лет обязательно присутствие взрослого)
    - `NC_17` (запрещено для лиц младше 17)

## Таблица `users` (Пользователи)

- **`id`** — уникальный идентификатор пользователя.  
  Тип: `integer` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`email`** — электронная почта пользователя.  
  Тип: `varchar` (строковый).

- **`login`** — логин пользователя для авторизации.  
  Тип: `varchar` (строковый).

- **`name`** — полное имя пользователя.  
  Тип: `varchar` (строковый).

- **`birthday`** — дата рождения пользователя.  
  Тип: `date` (дата).

## Таблица `friends` (Друзья)

- **`user_id`** — идентификатор инициатора дружбы.  
  Тип: `integer` (целочисленный).  
  Ключевое свойство: составной первичный ключ.  
  Связь: внешний ключ к таблице `users` (`id`).

- **`friend_id`** — идентификатор получателя запроса в друзья.  
  Тип: `integer` (целочисленный).  
  Ключевое свойство: составной первичный ключ.  
  Связь: внешний ключ к таблице `users` (`id`).

- **`status_id`** — ид статуса дружеского запроса.

## Таблица `friend_status` (Статус дружбы с пользователем)

- **`id`** — уникальный идентификатор.  
  Тип: `integer` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`name`** — значение статуса.  
  Тип: `rating` (перечисляемый).  
  Возможные значения:
    - `CONFIRMED` — подтверждёна
    - `UNCONFIRMED` — не подтверждёна

## Перечисления (`enum`)

### `genre` (Жанры)

- `COMEDY` — комедия
- `DRAMA` — драма
- `CARTOON` — мультфильм
- `THRILLER` — триллер
- `DOCUMENTARY` — документальный
- `ACTION` — боевик

### `rating` (Рейтинги)

- `G` — для всех возрастов
- `PG` — рекомендуется присутствие родителей
- `PG_13` — детям до 13 лет просмотр нежелателен
- `R` — лицам до 17 лет обязательно присутствие взрослого
- `NC_17` — запрещено для лиц младше 17

### `friendship_status` (Статусы дружбы)

- `CONFIRMED` — подтверждён
- `UNCONFIRMED` — не подтверждён

#### Запросы для основных операций приложения.

##### Запросы для операций с фильмами

- Получение всех фильмов. Вместе с данными о лайках.

```sql
SELECT f.id, 
       f.name, 
       f.description, 
       f.releaseDate, 
       f.duration, 
       fg.name AS genre, 
       fr.name AS rating, 
       array_agg(fl.user_id) AS likes
FROM films f
JOIN film_likes fl ON f.id=fl.film_id
JOIN film_genre fg ON f.genre_id=fg.id
JOIN film_rating fr ON f.rating_id=fr.id
GROUP BY f.id;
```

- Получение фильма по ИД. Вместе с данными о лайках.

```sql
SELECT f.id, 
       f.name, 
       f.description, 
       f.releaseDate, 
       f.duration, 
       fg.name AS genre, 
       fr.name AS rating, 
       array_agg(fl.user_id) AS likes
FROM films f
JOIN film_likes fl ON f.id=fl.film_id
JOIN film_genre fg ON f.genre_id=fg.id
JOIN film_rating fr ON f.rating_id=fr.id
WHERE f.id = <filmId>
GROUP BY f.id;
```

- Создать фильм.

```sql
INSERT INTO films (id, name, description, releaseDate, duration, genre_id, rating_id)
VALUES (
        <idValue>, 
        <nameValue>, 
        <descriptionValue>, 
        <releaseDateValue>, 
        <durationValue>, 
        <genreIdValue>, 
        <ratingIdValue>
);
```

- Обновить фильм.

```sql
UPDATE films
SET name = <nameValue>
    description = <descriptionValue>
    release_date = <releaseDateValue>
    duration = <durationValue> 
    genre_id = <genreIdValue> 
    rating_id = <ratingIdValue>
WHERE id = <idValue>;
```

- Удалить фильм.

```sql
DELETE FROM films
WHERE id = <idValue>;
```

- Удалить все лайки фильма. Используется при удалении фильма.

```sql
DELETE FROM film_likes
WHERE film_id = <idValue>;
```

- Добавить лайк к фильму.

```sql
INSERT INTO film_likes (film_id, user_id)
VALUES (
        <filmIdValue>, 
        <userIdValue>
);
```

- Удалить лайк фильма.

```sql
DELETE FROM film_likes
WHERE film_id = <filmIdValue>
      AND user_id = <userIdValue>;
```

- Получить топ n популярных фильмов. Вместе с информацией по лайкам.

```sql
SELECT f.*, array_agg(fl.user_id) AS likes
FROM films f
JOIN film_likes fl ON f.id=fl.film_id
WHERE f.id IN (
   SELECT film_id
   FROM film_likes
   ORDER BY COUNT(user_id) DESC
   LIMIT <n>
) top
GROUP BY f.id;
```

##### Запросы для операций с пользователями

- Получить всех пользователей. Вместе с данными о друзьях. Превращение данных колонок friend_ids и friend_statuses в хэш
  таблицу будет на стороне UserStorage или UserService.

```sql
SELECT u.*, 
       array_agg(f.friend_id) AS friend_ids, 
       array_agg(fs.name) AS friend_statuses
FROM users u
JOIN friends f ON u.id=f.user_id
JOIN friend_status fs ON f.status_id=fs.id
GROUP BY f.id;
```

- Получение пользователя по ИД. Вместе с данными о друзьях. Превращение данных колонок friend_ids и friend_statuses в
  хэш таблицу будет на стороне UserStorage или UserService.

```sql
SELECT u.*, 
       array_agg(f.friend_id) AS friend_ids, 
       array_agg(fs.name) AS friend_statuses
FROM users u
JOIN friends f ON u.id=f.user_id
JOIN friend_status fs ON f.status_id=fs.id
WHERE u.id = <userId>
GROUP BY f.id;
```

- Создать пользователя.

```sql
INSERT INTO users (id, email, login, name, birthday)
VALUES (
        <idValue>, 
        <emailValue>, 
        <loginValue>, 
        <nameValue>, 
        <birthdayValue>
);
```

- Обновить пользователя.

```sql
UPDATE users
SET email = <emailValue>
    login = <loginValue>
    name = <nameValue>
    birthday = <birthdayValue> 
WHERE id = <idValue>;
```

- Удалить пользователя.

```sql
DELETE FROM users
WHERE id = <idValue>;
```

- Удалить все записи о дружбе пользователя. Используется при удалении пользователя.

```sql
DELETE FROM friends
WHERE user_id = <idValue>
      OR friend_id = <idValue>;
```

- Добавить запись о дружбе пользователя. При добавлении друга используется дважды - для каждого из пользователей.

```sql
INSERT INTO friends (user_id, friend_id, status_id)
VALUES (
        <userIdValue>, 
        <friendIdValue>, 
        <statusIdValue>
);
```

- Удалить запись о дружбе пользователя. При удалении дружбы используется дважды - для каждого из пользователей.

```sql
DELETE FROM friends
WHERE user_id = <idValue>
      AND friend_id = <friendIdValue>;
```

- Получить ИД друзей пользователя.

```sql
SELECT f.friend_id
FROM friends f
WHERE f.user_id = <userId>;
```

- Получить ИД общих друзей с пользователем.

```sql
SELECT f1.friend_id
FROM friends f1
JOIN friends f2 ON f1.friend_id=f2.friend_id
WHERE f1.user_id = <userId>
      AND f2.user_id = <otherUserId>;
```
