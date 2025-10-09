-- =============================================
-- RESET I REFRESH MODERATION TEST DATA
-- Skripta koja može biti pokrenuta bilo kada
-- Koristi relativna vremena od trenutka izvršavanja
-- =============================================

-- 1. OBRISI STARE TEST PODATKE
-- =============================================

-- Obriši stare prijave test korisnika
DELETE FROM user_reports WHERE author_id IN (10, 11, 12, 13, 14, 15, 16, 17, 18);

-- Obriši stara blokiranja test korisnika
DELETE FROM user_blocked WHERE blocked_user_id IN (10, 11, 12, 13, 14, 15, 16, 17, 18);

-- Obriši test korisnike (CASCADE će obrisati sve njihove postove i relacije)
DELETE FROM users WHERE id IN (10, 11, 12, 13, 14, 15, 16, 17, 18);

-- 2. KREIRAJ TEST KORISNIKE
-- =============================================

INSERT INTO users (id, first_name, last_name, email, password, city, is_admin, login_suspended_until, posting_suspended_until)
VALUES 
    (10, 'Nikola', 'Milošević', 'nikola@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Beograd', false, null, null),
    (11, 'Tamara', 'Popović', 'tamara@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Novi Sad', false, null, null),
    (12, 'Extra', 'User', 'extra@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Niš', false, null, null),
    (13, 'Mina', 'Jurić', 'mina@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Kragujevac', false, null, null),
    (14, 'Vlada', 'Stošić', 'vlada@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Subotica', false, null, null),
    (15, 'Nina', 'Kostić', 'nina@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'Pančevo', false, null, null),
    (16, 'Sumnjiv', 'Korisnik1', 'sumnjiv1@test.com', '$2a$10$abcdefghijklmnopqrstuv', 'Test City', false, null, null),
    (17, 'Blizu', 'Granice', 'blizu@test.com', '$2a$10$abcdefghijklmnopqrstuv', 'Test City', false, null, null),
    (18, 'Stare', 'Prijave', 'stare@test.com', '$2a$10$abcdefghijklmnopqrstuv', 'Test City', false, null, null)
ON CONFLICT (id) DO UPDATE SET
    first_name = EXCLUDED.first_name,
    last_name = EXCLUDED.last_name,
    email = EXCLUDED.email;

-- 3. UBACI TEST PODATKE SA RELATIVNIM VREMENIMA
-- =============================================

-- Scenario 1: Više od 5 prijava u 24h - zabrana postovanja 24h
-- Target: korisnik 10
-- NOTE: post_id je NULL jer možda postovi ne postoje u bazi
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp)
VALUES 
    (1, 10, NULL, 'Spam', NOW() - INTERVAL '2 hours'),
    (2, 10, NULL, 'Harassment', NOW() - INTERVAL '4 hours'),
    (3, 10, NULL, 'Offensive', NOW() - INTERVAL '6 hours'),
    (4, 10, NULL, 'Inappropriate', NOW() - INTERVAL '8 hours'),
    (5, 10, NULL, 'Spam', NOW() - INTERVAL '10 hours'),
    (6, 10, NULL, 'Harassment', NOW() - INTERVAL '12 hours');

-- Dodaj i jedno blokiranje
INSERT INTO user_blocked (user_id, blocked_user_id, created_at)
VALUES (1, 10, NOW() - INTERVAL '3 hours')
ON CONFLICT DO NOTHING;

-- Scenario 2: Više od 8 prijava u 48h - zabrana postovanja 48h
-- Target: korisnik 11
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp)
VALUES 
    (1, 11, NULL, 'Spam', NOW() - INTERVAL '2 hours'),
    (2, 11, NULL, 'Harassment', NOW() - INTERVAL '6 hours'),
    (3, 11, NULL, 'Offensive', NOW() - INTERVAL '12 hours'),
    (4, 11, NULL, 'Inappropriate', NOW() - INTERVAL '18 hours'),
    (5, 11, NULL, 'Spam', NOW() - INTERVAL '24 hours'),
    (6, 11, NULL, 'Harassment', NOW() - INTERVAL '30 hours'),
    (7, 11, NULL, 'Offensive', NOW() - INTERVAL '36 hours'),
    (8, 11, NULL, 'Inappropriate', NOW() - INTERVAL '42 hours'),
    (9, 11, NULL, 'Spam', NOW() - INTERVAL '46 hours');

-- Scenario 3: Više od 4 blokiranja u 24h
-- Target: korisnik 12
INSERT INTO user_blocked (user_id, blocked_user_id, created_at)
VALUES 
    (1, 12, NOW() - INTERVAL '2 hours'),
    (2, 12, NOW() - INTERVAL '5 hours'),
    (3, 12, NOW() - INTERVAL '8 hours'),
    (4, 12, NOW() - INTERVAL '11 hours'),
    (5, 12, NOW() - INTERVAL '14 hours')
ON CONFLICT DO NOTHING;

-- Scenario 4: Kombinovani slučaj - 2+ bloka u 48h I 4+ prijava u 24h
-- Target: korisnik 13
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp)
VALUES 
    (1, 13, NULL, 'Spam', NOW() - INTERVAL '2 hours'),
    (2, 13, NULL, 'Harassment', NOW() - INTERVAL '4 hours'),
    (3, 13, NULL, 'Offensive', NOW() - INTERVAL '6 hours'),
    (4, 13, NULL, 'Inappropriate', NOW() - INTERVAL '8 hours'),
    (5, 13, NULL, 'Spam', NOW() - INTERVAL '10 hours');

INSERT INTO user_blocked (user_id, blocked_user_id, created_at)
VALUES 
    (1, 13, NOW() - INTERVAL '12 hours'),
    (2, 13, NOW() - INTERVAL '24 hours'),
    (3, 13, NOW() - INTERVAL '36 hours')
ON CONFLICT DO NOTHING;

-- Scenario 5: Brza eskalacija - više od 3 blokiranja u 6h
-- Target: korisnik 14
INSERT INTO user_blocked (user_id, blocked_user_id, created_at)
VALUES 
    (1, 14, NOW() - INTERVAL '1 hour'),
    (2, 14, NOW() - INTERVAL '2 hours'),
    (3, 14, NOW() - INTERVAL '3 hours'),
    (4, 14, NOW() - INTERVAL '4 hours')
ON CONFLICT DO NOTHING;

-- Scenario 6: Hronično problematično ponašanje - više od 12 prijava u 7 dana
-- Target: korisnik 15
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp)
VALUES 
    (1, 15, NULL, 'Spam', NOW() - INTERVAL '6 hours'),
    (2, 15, NULL, 'Harassment', NOW() - INTERVAL '12 hours'),
    (3, 15, NULL, 'Offensive', NOW() - INTERVAL '1 day'),
    (4, 15, NULL, 'Inappropriate', NOW() - INTERVAL '2 days'),
    (5, 15, NULL, 'Spam', NOW() - INTERVAL '2 days'),
    (6, 15, NULL, 'Harassment', NOW() - INTERVAL '3 days'),
    (7, 15, NULL, 'Offensive', NOW() - INTERVAL '3 days'),
    (8, 15, NULL, 'Inappropriate', NOW() - INTERVAL '4 days'),
    (9, 15, NULL, 'Spam', NOW() - INTERVAL '4 days'),
    (1, 15, NULL, 'Harassment', NOW() - INTERVAL '5 days'),
    (2, 15, NULL, 'Offensive', NOW() - INTERVAL '5 days'),
    (3, 15, NULL, 'Inappropriate', NOW() - INTERVAL '6 days'),
    (4, 15, NULL, 'Spam', NOW() - INTERVAL '6 days');

-- Scenario 7: Mešoviti slučaj - aktivira više pravila
-- Target: korisnik 16
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp)
VALUES 
    (1, 16, NULL, 'Spam', NOW() - INTERVAL '2 hours'),
    (2, 16, NULL, 'Harassment', NOW() - INTERVAL '4 hours'),
    (3, 16, NULL, 'Offensive', NOW() - INTERVAL '6 hours'),
    (4, 16, NULL, 'Inappropriate', NOW() - INTERVAL '8 hours'),
    (5, 16, NULL, 'Spam', NOW() - INTERVAL '10 hours'),
    (6, 16, NULL, 'Harassment', NOW() - INTERVAL '12 hours'),
    (7, 16, NULL, 'Offensive', NOW() - INTERVAL '30 hours'),
    (8, 16, NULL, 'Inappropriate', NOW() - INTERVAL '35 hours'),
    (9, 16, NULL, 'Spam', NOW() - INTERVAL '40 hours');

INSERT INTO user_blocked (user_id, blocked_user_id, created_at)
VALUES 
    (1, 16, NOW() - INTERVAL '3 hours'),
    (2, 16, NOW() - INTERVAL '6 hours')
ON CONFLICT DO NOTHING;

-- Scenario 8: Korisnik blizu granice ali ne prelazi (tačno 5 prijava, ne prelazi granicu)
-- Target: korisnik 17
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp)
VALUES 
    (1, 17, NULL, 'Spam', NOW() - INTERVAL '2 hours'),
    (2, 17, NULL, 'Harassment', NOW() - INTERVAL '4 hours'),
    (3, 17, NULL, 'Offensive', NOW() - INTERVAL '6 hours'),
    (4, 17, NULL, 'Inappropriate', NOW() - INTERVAL '8 hours'),
    (5, 17, NULL, 'Spam', NOW() - INTERVAL '10 hours');

-- Scenario 9: Stare prijave koje ne bi trebalo da triggeruju pravila (prijave starije od 7 dana)
-- Target: korisnik 18
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp)
VALUES 
    (1, 18, NULL, 'Spam', NOW() - INTERVAL '8 days'),
    (2, 18, NULL, 'Harassment', NOW() - INTERVAL '9 days'),
    (3, 18, NULL, 'Offensive', NOW() - INTERVAL '10 days'),
    (4, 18, NULL, 'Inappropriate', NOW() - INTERVAL '11 days'),
    (5, 18, NULL, 'Spam', NOW() - INTERVAL '12 days');


SELECT 'Moderation test data uspešno reset-ovani!' AS status;
SELECT 'Očekivani rezultati detekcije:' AS info;
SELECT '- 6-7 sumljivih korisnika (ID: 10, 11, 12, 13, 14, 15, 16)' AS rezultat;
SELECT '- Korisnik 17 NE bi trebalo da se detektuje (tačno na granici)' AS rezultat;
SELECT '- Korisnik 18 NE bi trebalo da se detektuje (stare prijave)' AS rezultat;
