DROP TABLE IF EXISTS feed, review_like, reviews, friends, film_likes, film_genre, genres, films, rating_mpa, users;

CREATE TABLE  IF NOT EXISTS users
(
    user_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_name VARCHAR(100),
    login     VARCHAR(50),
    email     VARCHAR(50),
    birthday  DATE
);

CREATE TABLE IF NOT EXISTS rating_mpa
(
    mpa_id          INT PRIMARY KEY,
    mpa_name        VARCHAR(10),
    mpa_description VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS films
(
    film_id       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_name     VARCHAR(100),
    description   VARCHAR(200),
    release_date  DATE,
    duration      INT,
    rating_mpa_id INT,
    CONSTRAINT rating_mpa FOREIGN KEY (rating_mpa_id) REFERENCES rating_mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS genres
(
    genre_id   INT PRIMARY KEY,
    genre_name VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id BIGINT REFERENCES films (film_id),
    genre_id INT REFERENCES genres (genre_id),
    primary key (film_id, genre_id)

);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id BIGINT REFERENCES films (film_id),
    user_id BIGINT REFERENCES users (user_id),
    primary key (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id BIGINT REFERENCES users (user_id),
    friend_id BIGINT REFERENCES users (user_id),
    primary key (user_id, friend_id)

);

CREATE TABLE IF NOT EXISTS reviews
(
    review_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content VARCHAR(200) NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    film_id BIGINT REFERENCES films (film_id) ON DELETE CASCADE,
    useful INT
);

CREATE TABLE IF NOT EXISTS review_like
(
    review_id BIGINT REFERENCES reviews (review_id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    is_useful BOOLEAN,
    primary key (review_id, user_id)
);

CREATE TABLE IF NOT EXISTS feed
(
    event_time TIMESTAMP NOT NULL,
    user_id BIGINT REFERENCES users (user_id) ON DELETE CASCADE,
    event_type VARCHAR(10) NOT NULL,
    operation VARCHAR(10) NOT NULL,
    event_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    CONSTRAINT constr_type CHECK (event_type IN ('LIKE','REVIEW','FRIEND')),
    CONSTRAINT constr_operation CHECK (operation IN ('REMOVE','ADD','UPDATE'))
)








