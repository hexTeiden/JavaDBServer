-- Bookings table for occupancy analytics
-- Run this in SSMS against MarieDB after the hotels table exists

USE MarieDB;
GO

IF OBJECT_ID('dbo.bookings', 'U') IS NOT NULL
    DROP TABLE dbo.bookings;
GO

CREATE TABLE dbo.bookings (
    id             INT IDENTITY(1,1) PRIMARY KEY,
    hotel_id       INT  NOT NULL REFERENCES dbo.hotels(id),
    checkin_date   DATE NOT NULL,
    rooms_occupied INT  NOT NULL CHECK (rooms_occupied >= 0),
    beds_occupied  INT  NOT NULL CHECK (beds_occupied  >= 0)
);
GO

-- Sample data (adjust hotel_id values to match your hotels table)
INSERT INTO dbo.bookings (hotel_id, checkin_date, rooms_occupied, beds_occupied) VALUES
    (1, '2024-01-10', 12, 20),
    (1, '2024-01-22', 8,  14),
    (1, '2024-02-05', 15, 28),
    (1, '2024-03-14', 10, 18),
    (2, '2024-01-08', 20, 35),
    (2, '2024-02-19', 18, 30),
    (2, '2024-03-01', 22, 40),
    (2, '2024-03-25', 17, 32),
    (3, '2024-01-15', 5,  9),
    (3, '2024-02-28', 7,  12),
    (3, '2024-04-10', 9,  16),
    (1, '2025-01-05', 14, 24),
    (1, '2025-02-12', 11, 19),
    (2, '2025-01-20', 25, 44),
    (2, '2025-03-08', 19, 33);
GO

SELECT * FROM dbo.bookings;
GO
