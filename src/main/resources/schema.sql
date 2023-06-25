DROP TABLE IF EXISTS bookings, items, comments, users CASCADE;

CREATE TABLE IF NOT EXISTS users(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar(100) NOT NULL UNIQUE,
    name varchar(100));


CREATE TABLE IF NOT EXISTS items(
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(100),
    description varchar(100) NOT NULL,
    available boolean,
    user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS bookings(
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    item_id BIGINT REFERENCES items(item_id) ON DELETE CASCADE,
    start_date timestamp NOT NULL,
    end_date timestamp NOT NULL,
    booker_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
    status varchar(10));

CREATE TABLE IF NOT EXISTS comments(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text varchar(100) NOT NULL,
    item_id BIGINT REFERENCES items(item_id) ON DELETE CASCADE,
    author_name varchar NOT NULL,
    created timestamp);