DROP TABLE users;

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(64) NOT NULL,
  firstname VARCHAR(64) NULL,
  lastname VARCHAR(64) NULL,
  email VARCHAR(64) NOT NULL,
  description VARCHAR(1024) NULL,
  active BOOLEAN NULL,
  password_hash VARCHAR(128) NOT NULL,
  salt VARCHAR(128) NULL,
  account_created TIMESTAMP NULL,
  last_login TIMESTAMP NULL,
  login_count BIGINT NULL,
  eula_version VARCHAR(16) NULL
  );
  CREATE UNIQUE INDEX email_idx ON users using btree (email);
  CREATE UNIQUE INDEX username_idx ON users using btree (username);
  CREATE UNIQUE INDEX id_idx ON users using btree (id);
  
DROP TABLE user_roles;
CREATE TABLE IF NOT EXISTS user_roles (
  id BIGSERIAL PRIMARY KEY,
  roleName VARCHAR(128) NOT NULL,
  user_id BIGINT NULL
  );
CREATE UNIQUE INDEX roleName_idx ON user_roles using btree (roleName,user_id);

DROP TABLE role_permissions;
CREATE TABLE IF NOT EXISTS role_permissions (
  id BIGSERIAL PRIMARY KEY,
  permission_name VARCHAR(128) NULL UNIQUE,
  role_id BIGINT NOT NULL
);
  
 CREATE UNIQUE INDEX permission_name_idx ON role_permissions using btree (permission_name);
 
