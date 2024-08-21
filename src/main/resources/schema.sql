CREATE TABLE IF NOT EXISTS MPA_RATINGS (
	MPA_RATING_ID_PK INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
	MPA_RATING_NAME VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS DIRECTORS (
    DIRECTOR_ID_PK INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    DIRECTOR_NAME VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS FILMS (
	FILM_ID_PK INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
	FILM_NAME VARCHAR(255) NOT NULL,
	FILM_RELEASE_DATE DATE NOT NULL,
	FILM_DURATION INTEGER NOT NULL,
	FILM_MPA_RATING_FK INTEGER NOT NULL REFERENCES MPA_RATINGS(MPA_RATING_ID_PK),
	FILM_DESCRIPTION VARCHAR(200) NOT NULL,
	FILM_RATING DEC(3, 1) NOT NULL
);

CREATE TABLE IF NOT EXISTS GENRES (
	GENRE_ID_PK INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
	GENRE_NAME VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS FILMS_GENRES (
	FG_FILM_ID INTEGER NOT NULL REFERENCES FILMS(FILM_ID_PK) ON DELETE CASCADE,
	FG_GENRE_ID INTEGER NOT NULL REFERENCES GENRES(GENRE_ID_PK) ON DELETE CASCADE,
	CONSTRAINT FILM_GENRE_PK PRIMARY KEY (
		FG_FILM_ID,
		FG_GENRE_ID
	)
);

CREATE TABLE IF NOT EXISTS FILMS_DIRECTORS (
	FD_FILM_ID INTEGER NOT NULL REFERENCES FILMS(FILM_ID_PK) ON DELETE CASCADE,
	FD_DIRECTOR_ID INTEGER NOT NULL REFERENCES DIRECTORS(DIRECTOR_ID_PK) ON DELETE CASCADE,
	CONSTRAINT FILM_DIRECTOR_PK PRIMARY KEY (
		FD_FILM_ID,
		FD_DIRECTOR_ID
	)
);

CREATE TABLE IF NOT EXISTS USERS (
	USER_ID_PK INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
	USER_LOGIN VARCHAR(255) NOT NULL UNIQUE,
	USER_NAME VARCHAR(255) NOT NULL,
	USER_EMAIL VARCHAR(255) NOT NULL UNIQUE,
	USER_BIRTHDAY DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS_RATINGS (
	FR_FILM_ID_PK INTEGER NOT NULL REFERENCES FILMS(FILM_ID_PK) ON DELETE CASCADE,
	FR_USER_ID_PK INTEGER NOT NULL REFERENCES USERS(USER_ID_PK) ON DELETE CASCADE,
	FR_RATING BOOLEAN NOT NULL,
	CONSTRAINT FILMS_RATING_PK PRIMARY KEY(
		FR_USER_ID_PK,
		FR_FILM_ID_PK
	)
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP_STATUSES (
	FS_USER_ID INTEGER NOT NULL REFERENCES USERS(USER_ID_PK) ON DELETE CASCADE,
	FS_FRIEND_ID INTEGER NOT NULL REFERENCES USERS(USER_ID_PK) ON DELETE CASCADE,
	CONSTRAINT FRIENDSHIP_STATUS_PK PRIMARY KEY(
		FS_FRIEND_ID,
		FS_USER_ID
	)
);

CREATE TABLE IF NOT EXISTS REVIEW (
	REVIEW_ID INTEGER NOT NULL AUTO_INCREMENT,
	CONTENT VARCHAR(255) NOT NULL,
	POSITIVE BOOLEAN NOT NULL,
	USER_ID INTEGER NOT NULL,
	FILM_ID INTEGER NOT NULL,
	CONSTRAINT REVIEW_PK PRIMARY KEY (REVIEW_ID),
	CONSTRAINT REVIEW_USERS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID_PK) ON DELETE CASCADE,
	CONSTRAINT REVIEW_FILMS_FK FOREIGN KEY (FILM_ID) REFERENCES FILMS(FILM_ID_PK) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS REVIEW_LIKE (
	REVIEW_ID INTEGER NOT NULL,
	USER_ID INTEGER NOT NULL,
	LIKED BOOLEAN NOT NULL,
	CONSTRAINT REVIEW_LIKE_PK PRIMARY KEY (REVIEW_ID,USER_ID),
	CONSTRAINT REVIEW_LIKE_REVIEW_FK FOREIGN KEY (REVIEW_ID) REFERENCES REVIEW(REVIEW_ID) ON DELETE CASCADE,
	CONSTRAINT REVIEW_LIKE_USERS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID_PK) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS EVENTS (
    EVENT_ID INTEGER NOT NULL AUTO_INCREMENT,
    TIMESTAMP BIGINT NOT NULL,
    USER_ID INTEGER NOT NULL,
    EVENT_TYPE_NAME VARCHAR(50) NOT NULL,
    OPERATION_NAME VARCHAR(50) NOT NULL,
    ENTITY_ID INTEGER NOT NULL,
    CONSTRAINT EVENT_PK PRIMARY KEY (EVENT_ID),
    CONSTRAINT EVENT_USERS_FK FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID_PK) ON DELETE CASCADE
);

MERGE 
	INTO
	MPA_RATINGS (MPA_RATING_ID_PK, MPA_RATING_NAME)
VALUES(1, 'G');

MERGE 
	INTO
	MPA_RATINGS (MPA_RATING_ID_PK, MPA_RATING_NAME)
VALUES(2, 'PG');

MERGE
	INTO
	MPA_RATINGS (MPA_RATING_ID_PK, MPA_RATING_NAME)
VALUES(3, 'PG-13');

MERGE
	INTO
	MPA_RATINGS (MPA_RATING_ID_PK, MPA_RATING_NAME)
VALUES(4, 'R');

MERGE
	INTO
	MPA_RATINGS (MPA_RATING_ID_PK, MPA_RATING_NAME)
VALUES(5, 'NC-17');

MERGE
	INTO
	GENRES (GENRE_ID_PK, GENRE_NAME)
VALUES(1, 'Комедия');

MERGE
	INTO
	GENRES (GENRE_ID_PK, GENRE_NAME)
VALUES(2, 'Драма');

MERGE
	INTO
	GENRES (GENRE_ID_PK, GENRE_NAME)
VALUES(3, 'Мультфильм');

MERGE
	INTO
	GENRES (GENRE_ID_PK, GENRE_NAME)
VALUES(4, 'Триллер');

MERGE
	INTO
	GENRES (GENRE_ID_PK, GENRE_NAME)
VALUES(5, 'Документальный');

MERGE
	INTO
	GENRES (GENRE_ID_PK, GENRE_NAME)
VALUES(6, 'Боевик');