CREATE TABLE "film" (
  "film_id" integer PRIMARY KEY,
  "name" varchar NOT NULL,
  "description" varchar(200),
  "release_date" date,
  "duration" integer,
  "mpa_rating" integer
);

CREATE TABLE "user" (
  "user_id" integer PRIMARY KEY,
  "login" varchar UNIQUE NOT NULL,
  "name" varchar,
  "email" varchar UNIQUE NOT NULL,
  "birthday" date
);

CREATE TABLE "film_genre" (
  "film_genre_id" integer PRIMARY KEY,
  "film_id" integer,
  "genre_id" integer
);

CREATE TABLE "genre" (
  "genre_id" integer PRIMARY KEY,
  "name" varchar UNIQUE
);

CREATE TABLE "mpa_rating" (
  "mpa_rating_id" integer PRIMARY KEY,
  "name" varchar UNIQUE
);

CREATE TABLE "film_rating" (
  "film_rating_id" integer PRIMARY KEY,
  "film_id" integer,
  "user_id" integer
);

CREATE TABLE "friendship_status" (
  "request_id" integer PRIMARY KEY,
  "user_id" integer,
  "friend_id" integer,
  "request_confirmed" boolean,
  "request_blocked" boolean,
  "friendship_deleted" integer
);

ALTER TABLE "film" ADD FOREIGN KEY ("mpa_rating") REFERENCES "mpa_rating" ("mpa_rating_id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("film_id");

ALTER TABLE "film_genre" ADD FOREIGN KEY ("genre_id") REFERENCES "genre" ("genre_id");

ALTER TABLE "film_rating" ADD FOREIGN KEY ("film_id") REFERENCES "film" ("film_id");

ALTER TABLE "film_rating" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("user_id");

ALTER TABLE "friendship_status" ADD FOREIGN KEY ("user_id") REFERENCES "user" ("user_id");

ALTER TABLE "friendship_status" ADD FOREIGN KEY ("friend_id") REFERENCES "user" ("user_id");

ALTER TABLE "friendship_status" ADD FOREIGN KEY ("friendship_deleted") REFERENCES "user" ("user_id");
