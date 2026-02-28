# java-filmorate

## ER-диаграмма:

<img src="./src/main/resources/er_diagram.png" width="100%" alt="ER‑диаграмма">

# Добавленный функционал

## 1. Общие фильмы друзей

**Описание задачи:** реализован вывод общих с другом фильмов с сортировкой по их популярности.  
**API**: ``` GET /films/common?userId={userId}&friendId={friendId}```  

**Описание**: возвращает список фильмов, отсортированных по популярности, которые являются общими для двух пользователей.  

**Параметры:**
* **userId (int)** - идентификатор пользователя, запрашивающего информацию.
* **friendId (int)** - идентификатор пользователя, с которым необходимо сравнить список фильмов.

## 2. Топ-N популярных фильмов

**Описание задачи:** реализован вывод топ-N фильмов по количеству лайков с возможностью фильтрации по жанру и году выпуска.  
**API**: ``` GET /films/popular?count={limit}&genreId={genreId}&year={year} ```  

**Описание**: возвращает список самых популярных фильмов (отсортированных по количеству лайков) указанного жанра за указанный год.  

Параметры:
* **count (int, необязательный)** - максимальное количество возвращаемых фильмов (по умолчанию 10).
* **genreId (int, необязательный)** - идентификатор жанра для фильтрации.
* **year (int, необязательный)** - год выпуска для фильтрации.


## 3. Поиск фильмов

**Описание задачи:** реализован поиск по названию фильмов и по режиссёру (при наличии соответствующей информации). Поиск осуществляется по подстроке.    
**API**: ``` GET /films/search```  

**Параметры строки запроса:**  
* ``` query (string)``` - текст для поиска. 
* ``` by  (string)``` - область поиска. Может принимать значения:
- **director** - поиск по режиссёру.
- **title** - поиск по названию.
- **director,title (или title,director)** - одновременный поиск по режиссёру и названию.  

**Пример запроса:**  
``` GET /films/search?query=крад&by=director,title ```

## 4. Удаление контента

**Описание задачи:** добавлена функциональность для удаления фильма и пользователя по идентификатору.  

### Удаление пользователя ###
**API**:
``` DELETE /users/{userId}```  
**Описание**: удаляет пользователя по его идентификатору.

**Параметры:**  
* **userId (int)** - идентификатор пользователя для удаления.

### Удаление фильма ###
**API**:
``` DELETE /films/{filmId}```  
**Описание**: удаляет фильм по его идентификатору..

**Параметры:**
* **filmId (int)** - идентификатор фильма для удаления.

## 5. Рекомендательная система

**Описание задачи:** реализована простая рекомендательная система для фильмов на основе коллаборативной фильтрации.  

**Алгоритм:**  
1. Найти пользователей с максимальным количеством пересечения по лайкам.
2. Определить фильмы, которые лайкнул "похожий" пользователь, но не лайкнул текущий пользователь.
3. Рекомендовать эти фильмы.  

**API**:
``` GET /users/{id}/recommendations```  

**Описание**: возвращает список рекомендованных фильмов для просмотра пользователю.

**Параметры:**
* **id (int)** - идентификатор пользователя, для которого генерируются рекомендации.
## 6. Лента событий

**Описание задачи:** добавлена возможность просмотра последних событий на платформе: добавление/удаление друзей, добавление/удаление лайков к фильмам, добавление/удаление/обновление отзывов.  
**API**: ``` GET /users/{id}/feed ```  
**Описание**: возвращает ленту событий пользователя.  

**Параметры:**  
* **id (int)** - идентификатор пользователя, для которого запрашивается лента событий.

## 7. Отзывы к фильмам

**Описание задачи:** реализованы отзывы на фильмы с системой оценки полезности (лайк/дизлайк) и рейтингом.  
**Характеристики отзыва:**   
* **Оценка**: полезно/бесполезно.
* **Тип отзыва**: негативный/положительный (определяется автоматически или пользователем).
* **Рейтинг**: Изначально равен 0. Увеличивается на 1 за "лайк" (полезно) и уменьшается на 1 за "дизлайк" (бесполезно). Отзывы сортируются по рейтингу полезности.  

**CRUD операции для отзывов:**  
``` POST /reviews ``` - добавление нового отзыва.  
``` PUT /reviews ``` - редактирование существующего отзыва.  
``` DELETE /reviews/{id} ```  - удаление отзыва по ID.  
``` GET /reviews/{id} ``` - получение отзыва по ID.  

**Получение списка отзывов**:  
``` GET /reviews?filmId={filmId}&count={count} ```  
**Описание**: Получает список отзывов.
* Если **filmId** указан, возвращаются отзывы для конкретного фильма.
* Если **filmId** не указан, возвращаются все отзывы.
* Если **count** не указан, по умолчанию возвращается 10 отзывов.  

**Оценка отзывов**  
``` PUT /reviews/{id}/like/{userId} ``` - пользователь ставит лайк отзыву.  
``` PUT /reviews/{id}/dislike/{userId} ``` - пользователь ставит дизлайк отзыву.  
``` DELETE /reviews/{id}/like/{userId}``` - пользователь удаляет лайк отзыву.  
``` DELETE /reviews/{id}/dislike/{userId}``` - пользователь удаляет дизлайк отзыву.  

## 8. Управление фильмами и режиссёрами

**Описание задачи:** добавлено имя режиссёра в информацию о фильмах, что позволяет реализовать расширенную функциональность для работы с режиссёрами и их фильмографией.  

**Получение списка фильмов режиссёра**  
**API:**  ``` GET /films/director/{directorId}?sortBy=[year,likes] ```  
**Описание**: возвращает список фильмов указанного режиссёра, отсортированных по количеству лайков (likes) или году выпуска (year).  
**Параметры запроса:**  
* **directorId (int)** - идентификатор режиссёра.
* **sortBy (string, необязательный)** - критерий сортировки (year или likes).

**CRUD операции для режиссёров**  
``` GET /directors ``` - получение списка всех режиссёров.  
``` GET /directors/{id} ``` Получение режиссёра по идентификатору.  
``` POST /directors ``` - создание нового режиссёра.  
``` PUT /directors ``` - изменение существующего режиссёра.  
``` DELETE /directors/{id}``` - удаление режиссёра по идентификатору.  

# Пояснение к диаграмме.

## Таблица `films` (Фильмы)

- **`id`** — уникальный идентификатор фильма.  ё
  Тип: `bigint` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`name`** — название фильма.  
  Тип: `varchar` (строковый).

- **`description`** — описание фильма.  
  Тип: `varchar` (строковый).

- **`release_date`** — дата выхода фильма.  
  Тип: `date` (дата).

- **`duration`** — длительность фильма в минутах.  
  Тип: `bigint` (целочисленный).

- **`rating_id`** — ссылка на рейтинг фильма.  
  Тип: `smallint` (целочисленный).  
  Связь: внешний ключ к таблице `film_rating` (`id`).

## Таблица `film_likes` (Лайки фильмов)

- **`film_id`** — идентификатор фильма, которому поставлен лайк.  
  Тип: `bigint` (целочисленный).  
  Связь: внешний ключ к таблице `films` (`id`).

- **`user_id`** — идентификатор пользователя, поставившего лайк.  
  Тип: `bigint` (целочисленный).  
  Связь: внешний ключ к таблице `users` (`id`).

## Таблица `film_genres` (Жанры фильмов)

- **`film_id`** — идентификатор фильма.  
  Тип: `bigint` (целочисленный).
  Связь: внешний ключ к таблице `films` (`id`).

- **`genre_id`** — ссылка на жанр фильма.
  Тип: `smallint` (целочисленный).
  Связь: внешний ключ к таблице `genres` (`id`).

## Таблица `genres` (Жанры)

- **`id`** — уникальный идентификатор жанра.  
  Тип: `smallint` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`name`** — название жанра.  
  Тип: `varchar` (строковый).  
  Возможные значения (`enum FilmGenre`):
    - `COMEDY` (комедия)
    - `DRAMA` (драма)
    - `CARTOON` (мультфильм)
    - `THRILLER` (триллер)
    - `DOCUMENTARY` (документальный)
    - `ACTION` (боевик)

## Таблица `film_rating` (Рейтинги фильмов)

- **`id`** — уникальный идентификатор рейтинга.  
  Тип: `smallint` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`name`** — значение рейтинга.  
  Тип: `varchar` (строковый).  
  Возможные значения (`enum FilmRating`):
    - `G` (для всех возрастов)
    - `PG` (рекомендуется присутствие родителей)
    - `PG_13` (детям до 13 лет просмотр нежелателен)
    - `R` (лицам до 17 лет обязательно присутствие взрослого)
    - `NC_17` (запрещено для лиц младше 17)

## Таблица `directors` (Режиссеры)

- **`id`** — уникальный идентификатор режиссера.  
  Тип: `bigint` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`name`** — значение рейтинга.  
  Тип: `varchar` (строковый).

## Таблица `film_director` (Режиссеры фильмов)

- **`director_id`** — идентификатор режиссера, у которого есть фильм.  
  Тип: `bigint` (целочисленный).  
  Связь: внешний ключ к таблице `director` (`id`).

 - **`film_id`** — идентификатор фильма, у которого есть режиссер.  
    Тип: `bigint` (целочисленный).
    Связь: внешний ключ к таблице `films` (`id`).

## Таблица `users` (Пользователи)

- **`id`** — уникальный идентификатор пользователя.  
  Тип: `bigint` (целочисленный).  
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
  Тип: `bigint` (целочисленный).  
  Ключевое свойство: составной первичный ключ.  
  Связь: внешний ключ к таблице `users` (`id`).

- **`friend_id`** — идентификатор получателя запроса в друзья.  
  Тип: `bigint` (целочисленный).  
  Ключевое свойство: составной первичный ключ.  
  Связь: внешний ключ к таблице `users` (`id`).

- **`status_id`** — ид статуса дружеского запроса.
  Тип: `smallint` (целочисленный).

## Таблица `friend_status` (Статус дружбы с пользователем)

- **`id`** — уникальный идентификатор.  
  Тип: `smallint` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`name`** — значение статуса.  
  Тип: `varchar` (строковый).  
  Возможные значения (`enum FriendshipStatus`):
      - `CONFIRMED` — подтверждёна
      - `UNCONFIRMED` — не подтверждёна

## Таблица `user_feed` (Событие случившееся с пользователем)

- **`id`** — уникальный идентификатор события.  
  Тип: `bigint` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`user_id`** — идентификатор пользователя совершающего событие.  
  Тип: `bigint` (целочисленный).  
  Связь: внешний ключ к таблице `users` (`id`).

- **`event_type`** — вид события 
  Тип: `varchar` (строковый).
  Возможные значения (`enum EventType`):
    - `LIKE` — лайк
    - `FRIEND` — друг
    - `REVIEW` - просмотр

- **`operation`** — тип операции.  
  Тип: `varchar` (строковый).
  Возможные значения (`enum EventOperation`):
    - `ADD` — добавить
    - `UPDATE` — обновить
    - `REMOVE` - удалить

- **`entity_id`** — идентификатор сущности с которой происходит событие.  
  Тип: `bigint` (целочисленный).

- **`timestamp`** — дата происшествия.  
  Тип: `bigint` (целочисленный).

## Таблица `reviews` (Отзывы на фильмы)

- **`id`** — уникальный идентификатор события.  
  Тип: `bigint` (целочисленный).  
  Ключевое свойство: первичный ключ.

- **`user_id`** — идентификатор пользователя оставившего отзыв.  
  Тип: `bigint` (целочисленный).  
  Связь: внешний ключ к таблице `users` (`id`).

- **`film_id`** — идентификатор фильма, у которого есть режиссер.  
  Тип: `bigint` (целочисленный).
  Связь: внешний ключ к таблице `films` (`id`).

- **`content`** — тектс отзыва.  
  Тип: `text` (строковый).  

- **`positive`** — тип отзыва.  
  Тип: `boolean` (логический). 
  Допустимые значения: `TRUE` (истина), `FALSE` (ложь).

## Таблица `review_rating` (Рейтинг отзыва)

- **`user_id`** — идентификатор пользователя оставившего отзыв.  
  Тип: `bigint` (целочисленный).  
  Связь: внешний ключ к таблице `users` (`id`).

- **`review_id`** — идентификатор события.  
  Тип: `bigint` (целочисленный).
  Связь: внешний ключ к таблице `reviews` (`id`).
    
- **`positive`** — тип отзыва.  
  Тип: `boolean` (логический). 
  Допустимые значения: `TRUE` (истина), `FALSE` (ложь).

#### Дополнительные enum 
  
## FilmSearchType (для типов поиска фильма)

  - `TITLE` — по названию
  - `DIRECTOR` — по режиссеру
  - `TITLE_AND_DIRECTOR` - по названию и режиссеру

### Запросы для основных операций приложения.

#### Принципы структурирования

1. **Фильмы** — основной домен, поэтому запросы идут первыми.
2. **Пользователи** — второй по важности домен (взаимодействуют с фильмами).
3. **Жанры, рейтинги, режиссёры** — справочные данные для фильмов.
4. **Лайки, отзывы, оценки отзывов** — пользовательские взаимодействия с контентом.
5. **Лента событий** — агрегирует все действия пользователей.
6. **Специализированные запросы** — сложные бизнес‑логики (рекомендации, общие фильмы).

#### Запросы для операций с фильмами
- Получение всех фильмов. Данные о лайках, жанрах и режиссёрах получаются отдельными запросами.
```sql
SELECT
    f.id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    f.rating_id
FROM films f;
```

- Получение фильма по ИД. Данные о лайках, жанрах и режиссёрах получаются отдельными запросами.
```sql
SELECT
    f.id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    f.rating_id
FROM films f
WHERE f.id = :id;
```

- Получить топ‑N популярных фильмов с фильтрами по жанру и году выпуска.
```sql
SELECT
    f.id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    f.rating_id
FROM films f
LEFT JOIN film_likes fl ON f.id = fl.film_id
LEFT JOIN film_genres fg ON f.id = fg.film_id
WHERE (:genreId IS NULL OR fg.genre_id = :genreId)
  AND (:year IS NULL OR EXTRACT(YEAR FROM f.release_date) = :year)
GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id
ORDER BY COUNT(fl.user_id) DESC
LIMIT :count;
```

- Поиск фильмов по названию или имени режиссёра с сортировкой по популярности.
```sql
SELECT
    f.id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    f.rating_id
FROM films f
LEFT JOIN film_likes fl ON f.id = fl.film_id
LEFT JOIN (
    SELECT fd.film_id, d.name AS director_name
    FROM film_director fd
    JOIN directors d ON fd.director_id = d.id
) AS director_data ON f.id = director_data.film_id
WHERE (:filmName IS NOT NULL AND LOWER(f.name) LIKE :filmName)
   OR (:directorName IS NOT NULL AND LOWER(director_data.director_name) LIKE :directorName)
GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id
ORDER BY COUNT(fl.user_id) DESC;
```

- Создание фильма.
```sql
INSERT INTO films (name, description, release_date, duration, rating_id)
VALUES (:name, :description, :releaseDate, :duration, :ratingId);
```

- Обновление фильма.
```sql
UPDATE films
SET name = :name,
    description = :description,
    release_date = :releaseDate,
    duration = :duration,
    rating_id = :ratingId
WHERE id = :id;
```

- Удаление фильма (с каскадным удалением связанных данных).
```sql
DELETE FROM films WHERE id = :id;
```

- Добавление жанра к фильму.
```sql
INSERT INTO film_genres (film_id, genre_id)
VALUES (:filmId, :genreId);
```

- Удаление всех жанров фильма.
```sql
DELETE FROM film_genres WHERE film_id = :filmId;
```

- Удаление конкретного жанра фильма.
```sql
DELETE FROM film_genres
WHERE film_id = :filmId AND genre_id = :genreId;
```

- Добавление режиссёра к фильму.
```sql
INSERT INTO film_director (film_id, director_id)
VALUES (:filmId, :directorId);
```

- Удаление всех режиссёров фильма.
```sql
DELETE FROM film_director WHERE film_id = :filmId;
```

#### Запросы для операций с пользователями

- Получение всех пользователей.
```sql
SELECT
    u.id,
    u.email,
    u.login,
    u.name,
    u.birthday
FROM users u;
```

- Получение пользователя по ИД.
```sql
SELECT
    u.id,
    u.email,
    u.login,
    u.name,
    u.birthday
FROM users u
WHERE u.id = :id;
```

- Создание пользователя.
```sql
INSERT INTO users (email, login, name, birthday)
VALUES (:email, :login, :name, :birthday);
```

- Обновление пользователя.
```sql
UPDATE users
SET email = :email,
    login = :login,
    name = :name,
    birthday = :birthday
WHERE id = :id;
```

- Удаление пользователя.
```sql
DELETE FROM users WHERE id = :id;
```

- Добавление неподтверждённой дружбы (запрос друга).
```sql
INSERT INTO friends (user_id, friend_id, status_id)
VALUES (:userId, :friendId, 2); -- 2 = статус «неподтверждённый»
```

- Подтверждение дружбы (для обеих сторон).
```sql
UPDATE friends
SET status_id = 1
WHERE user_id = :userId AND friend_id = :friendId
   OR user_id = :friendId AND friend_id = :userId; -- 1 = статус «подтверждённый»
```

- Удаление дружбы.
```sql
DELETE FROM friends
WHERE user_id = :userId AND friend_id = :friendId;
```

#### Запросы для операций с жанрами

- Получение всех жанров.
```sql
SELECT
    g.id,
    g.name
FROM genres g
ORDER BY g.id ASC;
```

- Получение жанра по ИД.
```sql
SELECT
    g.id,
    g.name
FROM genres g
WHERE g.id = :id;
```

#### Запросы для операций с рейтингами фильмов

- Получение всех рейтингов.
```sql
SELECT
    fr.id,
    fr.name
FROM film_rating fr;
```

- Получение рейтинга по ИД.
```sql
SELECT
    fr.id,
    fr.name
FROM film_rating fr
WHERE fr.id = :id;
```

#### Запросы для операций с лайками фильмов

- Получение лайков по спискам фильмов.
```sql
SELECT
    fl.film_id,
    fl.user_id
FROM film_likes fl
WHERE fl.film_id IN (:filmIds);
```

- Получение лайков по спискам пользователей.
```sql
SELECT
    fl.film_id,
    fl.user_id
FROM film_likes fl
WHERE fl.user_id IN (:userIds);
```

- Проверка наличия конкретного лайка.
```sql
SELECT
    fl.film_id,
    fl.user_id
FROM film_likes fl
WHERE fl.film_id = :filmId AND fl.user_id = :userId;
```

- Добавление лайка к фильму.
```sql
INSERT INTO film_likes (film_id, user_id)
VALUES (:filmId, :userId);
```

- Удаление лайка фильма.
```sql
DELETE FROM film_likes
WHERE film_id = :filmId AND user_id = :userId;
```

#### Запросы для операций с режиссёрами

- Получение всех режиссёров.
```sql
SELECT * FROM directors ORDER BY id ASC;
```

- Получение режиссёра по ИД.
```sql
SELECT * FROM directors WHERE id = :id;
```

- Создание режиссёра.
```sql
INSERT INTO directors (name) VALUES (:name);
```

- Обновление режиссёра.
```sql
UPDATE directors SET name = :name WHERE id = :id;
```

- Удаление режиссёра.
```sql
DELETE FROM directors WHERE id = :id;
```

- Получение режиссёров по ИД фильма.
```sql
SELECT
    fd.film_id,
    fd.director_id,
    d.name as director_name
FROM film_director fd
JOIN directors d ON fd.director_id = d.id
WHERE fd.film_id IN (:filmIds);
```

#### Запросы для операций с отзывами

- Создание отзыва.
```sql
INSERT INTO reviews (user_id, film_id, content, positive)
VALUES (:userId, :filmId, :content, :positive);
```

- Обновление отзыва.
```sql
UPDATE reviews SET content = :content, positive = :positive WHERE id = :id;
```

- Удаление отзыва.
```sql
DELETE FROM reviews WHERE id = :id;
```

- Получение отзыва по ИД.
```sql
SELECT * FROM reviews WHERE id = :id;
```

- Получение отзывов по ИД фильма (ограничение по количеству).
```sql
SELECT * FROM reviews WHERE film_id = :filmId LIMIT :count;
```

- Проверка существования отзыва по ИД.
```sql
SELECT EXISTS(SELECT 1 FROM reviews WHERE id = :id);
```

#### Запросы для операций с оценками отзывов (like/dislike)

- Добавление лайка к отзыву.
```sql
INSERT INTO review_rating (user_id, review_id, positive)
VALUES (:userId, :reviewId, true);
```

- Добавление дизлайка к отзыву.
```sql
INSERT INTO review_rating (user_id, review_id, positive)
VALUES (:userId, :reviewId, false);
```

- Удаление оценки отзыва.
```sql
DELETE FROM review_rating
WHERE review_id = :reviewId AND user_id = :userId;
```

- Проверка, ставил ли пользователь оценку отзыву.
```sql
SELECT EXISTS(SELECT 1 FROM review_rating WHERE review_id = :reviewId AND user_id = :userId);
```

- Получение «полезности» отзыва (сумма лайков минус дизлайки).
```sql
SELECT COALESCE(SUM(CASE WHEN positive THEN 1 ELSE -1 END), 0)
FROM review_rating
WHERE review_id = :reviewId;
```

- Получение «полезности» для списка отзывов.
```sql
SELECT review_id, COALESCE(SUM(CASE WHEN positive THEN 1 ELSE -1 END), 0) AS useful
FROM review_rating
WHERE review_id IN (:reviewIds)
GROUP BY review_id;
```

#### Запросы для операций с лентой событий

- Добавление события в ленту пользователя.
```sql
INSERT INTO user_feed (user_id, event_type, operation, entity_id, timestamp)
VALUES (:userId, :eventType, :operation, :entityId, :timestamp);
```

- Получение ленты событий пользователя (с ограничением по количеству и сортировкой по времени).
```sql
SELECT id, user_id, event_type, operation, entity_id, timestamp
FROM user_feed
WHERE user_id = :userId
ORDER BY timestamp
LIMIT :limit;
```

#### Дополнительные специализированные запросы для фильмов

- Получение фильмов режиссёра, отсортированных по году выпуска.
```sql
SELECT
    f.id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    f.rating_id
FROM films f
JOIN film_director fd ON fd.film_id = f.id
WHERE fd.director_id = :directorId
ORDER BY f.release_date ASC;
```

- Получение фильмов режиссёра, отсортированных по количеству лайков.
```sql
SELECT
    f.id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    f.rating_id,
    COUNT(fl.film_id) AS likes_count
FROM films f
JOIN film_director fd ON fd.film_id = f.id
LEFT JOIN film_likes fl ON fl.film_id = f.id
WHERE fd.director_id = :directorId
GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id
ORDER BY likes_count DESC;
```

- Получение общих фильмов с другом, отсортированных по популярности.
```sql
SELECT
    f.id,
    f.name,
    f.description,
    f.release_date,
    f.duration,
    f.rating_id
FROM films f
JOIN film_likes fl1 ON f.id = fl1.film_id AND fl1.user_id = :userId
JOIN film_likes fl2 ON f.id = fl2.film_id AND fl2.user_id = :friendId
LEFT JOIN film_likes fl ON f.id = fl.film_id
GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.rating_id
ORDER BY COUNT(fl.user_id) DESC;
```

- Получение рекомендованных фильмов на основе схожести пользователей.
```sql
SELECT
    film.id,
    film.name,
    film.description,
    film.release_date,
    film.duration,
    film.rating_id
FROM (
    SELECT
        recommended_film.film_id,
        COUNT(DISTINCT similar_user.user_id) AS similarity_based_popularity
    FROM film_likes target_user_likes
    JOIN film_likes similar_user ON target_user_likes.film_id = similar_user.film_id
    JOIN film_likes recommended_film ON similar_user.user_id = recommended_film.user_id
    LEFT JOIN film_likes already_liked_by_target ON recommended_film.film_id = already_liked_by_target.film_id
        AND already_liked_by_target.user_id = :targetUser
    WHERE
        target_user_likes.user_id = :targetUser
        AND similar_user.user_id != :targetUser
        AND already_liked_by_target.film_id IS NULL
    GROUP BY recommended_film.film_id
) AS film_recommendation_metrics
JOIN films film ON film_recommendation_metrics.film_id = film.id
ORDER BY film_recommendation_metrics.similarity_based_popularity DESC, film.id
LIMIT :count;
```
