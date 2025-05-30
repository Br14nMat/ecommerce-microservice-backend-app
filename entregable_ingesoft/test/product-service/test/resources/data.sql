INSERT INTO categories (category_id, category_title, image_url, parent_category_id) VALUES
(1, 'Deportes', 'deportes.jpg', NULL),
(2, 'Libros', 'libros.jpg', NULL),
(3, 'Jardinería', 'jardineria.jpg', NULL);

INSERT INTO products (product_id, product_title, image_url, sku, price_unit, quantity, category_id) VALUES
(1, 'Balon de futbol', 'ball_football.jpg', 'SKU-DEP-BF1', 29.99, 75, 1),
(2, 'Novela Cien Años de Soledad', 'cien_anos.jpg', 'SKU-LIB-CA1', 15.99, 200, 2),
(3, 'Kit de Herramientas para Jardín', 'garden_tools.jpg', 'SKU-JAR-KH1', 45.99, 50, 3);
