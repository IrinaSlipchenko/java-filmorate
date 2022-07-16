INSERT INTO genres (genre_id, genre_name)
VALUES (1, 'Комедия');
INSERT INTO genres (genre_id, genre_name)
VALUES (2, 'Драма');
INSERT INTO genres (genre_id, genre_name)
VALUES (3, 'Мультфильм');
INSERT INTO genres (genre_id, genre_name)
VALUES (4, 'Триллер');
INSERT INTO genres (genre_id, genre_name)
VALUES (5, 'Документальный');
INSERT INTO genres (genre_id, genre_name)
VALUES (6, 'Боевик');

INSERT INTO rating_mpa(mpa_id, mpa_name, mpa_description)
VALUES (1, 'G', 'у фильма нет возрастных ограничений');
INSERT INTO rating_mpa(mpa_id, mpa_name, mpa_description)
VALUES (2, 'PG', 'детям рекомендуется смотреть фильм с родителями');
INSERT INTO rating_mpa(mpa_id, mpa_name, mpa_description)
VALUES (3, 'PG-13', 'детям до 13 лет просмотр не желателен');
INSERT INTO rating_mpa(mpa_id, mpa_name, mpa_description)
VALUES (4, 'R', 'лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
INSERT INTO rating_mpa(mpa_id, mpa_name, mpa_description)
VALUES (5, 'NC-17', 'лицам до 18 лет просмотр запрещён');