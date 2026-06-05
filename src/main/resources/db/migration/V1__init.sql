CREATE TABLE users (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255),
email VARCHAR(255) UNIQUE,
deleted_at DATETIME(6)
);

CREATE TABLE roles (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
role_name VARCHAR(255)
);

CREATE TABLE user_role (
user_id BIGINT NOT NULL,
role_id BIGINT NOT NULL,
FOREIGN KEY (user_id) REFERENCES users(id),
FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE recipe (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
title VARCHAR(255),
description VARCHAR(255),
image VARCHAR(255),
category VARCHAR(255),
time INT,
user_id BIGINT,
deleted_at DATETIME(6),
FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE ingredient (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255),
amount VARCHAR(255),
recipe_id BIGINT,
deleted_at DATETIME(6),
FOREIGN KEY (recipe_id) REFERENCES recipe(id)
);

CREATE TABLE step (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
step_number INT,
title VARCHAR(255),
description TEXT,
time INT,
recipe_id BIGINT NOT NULL,
deleted_at DATETIME(6),
FOREIGN KEY (recipe_id) REFERENCES recipe(id)
);

CREATE TABLE tag (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE recipe_tag (
recipe_id BIGINT NOT NULL,
tag_id BIGINT NOT NULL,
PRIMARY KEY (recipe_id, tag_id),
FOREIGN KEY (recipe_id) REFERENCES recipe(id),
FOREIGN KEY (tag_id) REFERENCES tag(id)
);

CREATE TABLE comment (
id BIGINT AUTO_INCREMENT PRIMARY KEY,
text TEXT NOT NULL,
rating INT NOT NULL,
created_at DATETIME(6),
deleted_at DATETIME(6),
recipe_id BIGINT NOT NULL,
user_id BIGINT NOT NULL,
FOREIGN KEY (recipe_id) REFERENCES recipe(id),
FOREIGN KEY (user_id) REFERENCES users(id)
);
