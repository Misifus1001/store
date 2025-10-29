INSERT INTO
    rol (nombre, descripcion, estatus, fecha_creacion)
VALUES
    (
        'ADMIN',
        'Administrador del sistema',
        TRUE,
        NOW()
    ),
    ('DUEÑO', 'Dueño de tienda', TRUE, NOW()),
    ('VENDEDOR', 'Vendedor en tienda', TRUE, NOW());