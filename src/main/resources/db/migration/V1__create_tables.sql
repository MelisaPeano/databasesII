
CREATE TABLE IF NOT EXISTS `AppUser` (
   user_id INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,
   user_name VARCHAR(100) NOT NULL,
    user_email VARCHAR(100) NOT NULL UNIQUE,
    user_password VARCHAR(100) NOT NULL,
    created_in DATETIME
);
CREATE TABLE IF NOT EXISTS Category (
    category_id INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,
    user_id INT NOT NULL,
    category_name VARCHAR(60) NOT NULL,
    category_color CHAR(7) NULL,
    created_in DATETIME,
    is_default boolean default FALSE,
    FOREIGN KEY (user_id) REFERENCES AppUser(user_id)
);

CREATE TABLE IF NOT EXISTS ListTable (
    list_id INT NOT NULL UNIQUE AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    list_name VARCHAR(60) NOT NULL,
    list_description VARCHAR(255) NULL,
    created_in DATETIME,
    FOREIGN KEY (category_id) REFERENCES Category(category_id)

);
CREATE TABLE IF NOT EXISTS Task (
    task_id INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,
    list_id INT NOT NULL,
    task_title VARCHAR(100) NOT NULL,
    task_description TEXT NULL,
    expires_in DATETIME NULL,
    priority ENUM('LOW', 'HIGH', 'MIDDLE'),
    task_status ENUM('NEW', 'IN_PROGRESS', 'DONE', 'CANCELLED'),
    created_in DATETIME,
    completed_in DATETIME NULL,
    FOREIGN KEY (list_id) REFERENCES ListTable(list_id) ON DELETE CASCADE,
    UNIQUE (task_title, list_id)
);
CREATE TABLE IF NOT EXISTS Task_status (
    task_status_id INT AUTO_INCREMENT UNIQUE NOT NULL PRIMARY KEY,
    task_id INT NOT NULL,
    status ENUM('NEW', 'IN_PROGRESS', 'DONE', 'CANCELLED'),
    changed_in DATETIME,
    comment VARCHAR(100) NULL,
    FOREIGN KEY (task_id) REFERENCES Task(task_id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS refresh_token (
    token_id INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    token VARCHAR(255),
    expiry_date DATETIME,
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES AppUser(user_id)
);