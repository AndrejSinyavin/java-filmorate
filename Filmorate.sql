CREATE TABLE "films" (
  "film_id" integer PRIMARY KEY,
  "name" varchar(255) NOT NULL,
  "description" varchar(200) NOT NULL,
  "release_date" date NOT NULL,
  "duration" integer,
  "mpa_rating" integer
);

CREATE TABLE "users" (
  "user_id" integer PRIMARY KEY,
  "login" varchar(255) UNIQUE NOT NULL,
  "name" varchar(255),
  "email" varchar(255) UNIQUE NOT NULL,
  "birthday" date NOT NULL
);

CREATE TABLE "film_genres" (
  "film_id" integer,
  "genre_id" integer,
  PRIMARY KEY ("film_id", "genre_id")
);

CREATE TABLE "genres" (
  "genre_id" integer PRIMARY KEY,
  "name" varchar(50) UNIQUE NOT NULL
);

CREATE TABLE "mpa_ratings" (
  "mpa_rating_id" integer PRIMARY KEY,
  "name" varchar(50) UNIQUE NOT NULL
);

CREATE TABLE "film_ratings" (
  "film_id" integer,
  "user_id" integer,
  PRIMARY KEY ("film_id", "user_id")
);

CREATE TABLE "friendship_statuses" (
  "user_id" integer,
  "friend_id" integer,
  "request_confirmed" boolean,
  "request_blocked" boolean,
  "request_cancel_by_friend" boolean,
  PRIMARY KEY ("user_id", "friend_id")
);

ALTER TABLE "films" ADD FOREIGN KEY ("mpa_rating") REFERENCES "mpa_ratings" ("mpa_rating_id");

ALTER TABLE "film_genres" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "film_genres" ADD FOREIGN KEY ("genre_id") REFERENCES "genres" ("genre_id");

ALTER TABLE "film_ratings" ADD FOREIGN KEY ("film_id") REFERENCES "films" ("film_id");

ALTER TABLE "film_ratings" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "friendship_statuses" ADD FOREIGN KEY ("user_id") REFERENCES "users" ("user_id");

ALTER TABLE "friendship_statuses" ADD FOREIGN KEY ("friend_id") REFERENCES "users" ("user_id");
