-- Admin user (password: admin123)
INSERT INTO users (email, password, name, role, balance)
VALUES ('admin@demo.shop', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'Admin', 'ADMIN', 0.00);

-- Regular user (password: user123)
INSERT INTO users (email, password, name, role, balance)
VALUES ('user@demo.shop', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfMQB7yGEHjTETTpqfDnhcUBumGPexuy', 'John Doe', 'USER', 500.00);

-- Products
INSERT INTO products (name, description, price, stock, image_url, category)
VALUES
    ('Laptop Pro 15', 'High-performance laptop with 15.6" display, Intel i7, 16GB RAM, 512GB SSD', 1299.99, 10,
     'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400', 'Electronics'),

    ('Wireless Mouse', 'Ergonomic wireless mouse with long battery life and precise tracking', 29.99, 50,
     'https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=400', 'Electronics'),

    ('Mechanical Keyboard', 'Full-size mechanical keyboard with RGB backlight and blue switches', 89.99, 25,
     'https://images.unsplash.com/photo-1541140532154-b024d705b90a?w=400', 'Electronics'),

    ('USB-C Hub', '7-in-1 USB-C hub with HDMI, USB 3.0, SD card reader and PD charging', 49.99, 30,
     'https://images.unsplash.com/photo-1583394838336-acd977736f90?w=400', 'Electronics'),

    ('Monitor 27"', '4K IPS monitor 27 inch, 60Hz refresh rate, HDR support', 399.99, 8,
     'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=400', 'Electronics'),

    ('Desk Lamp LED', 'Adjustable LED desk lamp with 5 color modes and USB charging port', 34.99, 40,
     'https://images.unsplash.com/photo-1534349762230-e0cadf78f5da?w=400', 'Home'),

    ('Coffee Mug Warmer', 'Electric mug warmer keeps your coffee at perfect temperature', 19.99, 60,
     'https://images.unsplash.com/photo-1514228742587-6b1558fcca3d?w=400', 'Home'),

    ('Notebook A5', 'Premium hardcover notebook, 200 pages, dot grid pattern', 12.99, 100,
     'https://images.unsplash.com/photo-1531346878377-a5be20888e57?w=400', 'Stationery'),

    ('Wireless Headphones', 'Over-ear wireless headphones with active noise cancellation', 159.99, 15,
     'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400', 'Electronics'),

    ('Webcam HD', '1080p HD webcam with built-in microphone for video calls', 79.99, 20,
     'https://images.unsplash.com/photo-1587826080692-f439cd0b70da?w=400', 'Electronics'),

    ('Phone Stand', 'Adjustable aluminum phone and tablet stand for desk use', 15.99, 75,
     'https://images.unsplash.com/photo-1585386959984-a4155224a1ad?w=400', 'Accessories'),

    ('Backpack 30L', 'Water-resistant backpack with laptop compartment and USB port', 69.99, 18,
     'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400', 'Bags');
