-- Tabela de salas de reunião
CREATE TABLE meeting_rooms (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    capacity INTEGER NOT NULL,
    location VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de reservas
CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    room_id BIGINT NOT NULL REFERENCES meeting_rooms(id) ON DELETE CASCADE,
    participants INTEGER NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    requester VARCHAR(255) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_time_order CHECK (end_time > start_time)
);

-- Índice para otimizar consultas por período
CREATE INDEX idx_reservations_time_range
ON reservations(room_id, start_time, end_time);

