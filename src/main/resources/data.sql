-- ADMIN
INSERT INTO users (id, name, nickname, email, password, birth_date, height, weight, goal, activity_level, role, is_active, created_at)
VALUES
    (1, 'Admin', 'admin', 'admin@nutritrack.com',
     '$2a$10$nHIMMEVUOzlkpR1KYt94A.V/Y6zNfB0QJhYHeWnRr8S/3qC/9IAWq', -- contraseña: Hola12345
     '1990-01-01', 1.0, 1.0, 'MAINTAIN', 'SEDENTARY', 'ADMIN', true, NOW());

-- USERS
INSERT INTO users (id, name, nickname, email, password, birth_date, height, weight, goal, activity_level, role, is_active, created_at)
VALUES
    (2, 'Alice', 'alice123', 'alice@example.com',
     '$2a$10$nHIMMEVUOzlkpR1KYt94A.V/Y6zNfB0QJhYHeWnRr8S/3qC/9IAWq', -- contraseña: Hola12345
     '2000-05-15', 165.0, 60.0, 'GAIN', 'ACTIVE', 'USER', true, NOW()),

    (3, 'Bob', 'bob456', 'bob@example.com',
     '$2a$10$nHIMMEVUOzlkpR1KYt94A.V/Y6zNfB0QJhYHeWnRr8S/3qC/9IAWq', -- contraseña: Hola12345
     '1995-09-30', 180.0, 75.0, 'GAIN', 'MODERATE', 'USER', true, NOW());



INSERT INTO allergens (id, name) VALUES (1, 'Gluten');
INSERT INTO allergens (id, name) VALUES (2, 'Lactose');
INSERT INTO allergens (id, name) VALUES (3, 'Nuts');
INSERT INTO allergens (id, name) VALUES (4, 'Egg');
INSERT INTO allergens (id, name) VALUES (5, 'Fish');
INSERT INTO allergens (id, name) VALUES (6, 'Shellfish');
INSERT INTO allergens (id, name) VALUES (7, 'Soy');
INSERT INTO allergens (id, name) VALUES (8, 'Sesame');
INSERT INTO allergens (id, name) VALUES (9, 'Mustard');
INSERT INTO allergens (id, name) VALUES (10, 'Celery');
INSERT INTO allergens (id, name) VALUES (11, 'Sulfites');
INSERT INTO allergens (id, name) VALUES (12, 'Peanuts');

