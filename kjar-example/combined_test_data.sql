-- ===========================
-- COMBINED TEST DATA (FEED + MODERATION)
-- PostgreSQL
-- ===========================

-- 0) Clean state
TRUNCATE rating_hashtags, place_hashtags, post_hashtags,
         user_blocked, user_friends, post_reports, user_reports, post_likes,
         ratings, places, posts, users RESTART IDENTITY CASCADE;

-- ===========================
-- PART 1: FEED TEST DATA
-- ===========================

-- 1) Users (password = '123456' for all)
-- bcrypt (cost=10, Spring format $2a$): $2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK
INSERT INTO users (id, first_name,last_name,email,password,city,is_admin) VALUES
    (1,  'Admin','User','admin@socialnet.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Belgrade',true),   -- 1
    (2,  'Marko','Petrović','marko@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Belgrade',false), -- 2  TARGET (BASE user)
    (3,  'Ana','Jovanović','ana@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Novi Sad',false),    -- 3
    (4,  'Stefan','Nikolić','stefan@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Kragujevac',false),-- 4
    (5,  'Milica','Stojanović','milica@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Niš',false),     -- 5
    (6,  'Luka','Ilić','luka@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Belgrade',false),       -- 6
    (7,  'Sara','Matić','sara@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Belgrade',false),      -- 7
    (8,  'Ivan','Kovač','ivan@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Novi Sad',false),       -- 8
    (9,  'Jelena','Marković','jelena@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Niš',false),      -- 9
    (10, 'Nikola','Milošević','nikola@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Beograd',false), -- 10 MODERATION TARGET
    (11, 'Tamara','Popović','tamara@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Novi Sad',false), -- 11 MODERATION TARGET
    (12, 'Extra','User','extra@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Niš',false),         -- 12 NEW user + MODERATION TARGET
    (13, 'Mina','Jurić','mina@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Kragujevac',false),  -- 13 MODERATION TARGET
    (14, 'Vlada','Stošić','vlada@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Subotica',false),  -- 14 MODERATION TARGET
    (15, 'Nina','Kostić','nina@example.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Pančevo',false),    -- 15 MODERATION TARGET
    (16, 'Sumnjiv','Korisnik1','sumnjiv1@test.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Test City',false), -- 16 MODERATION TARGET
    (17, 'Blizu','Granice','blizu@test.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Test City',false),     -- 17 MODERATION TARGET
    (18, 'Stare','Prijave','stare@test.com','$2a$10$16l4595sy4iYng0SdhgcpuaQZlPJNdqajyelV4v8rs9F05b4WycnK','Test City',false);     -- 18 MODERATION TARGET

-- 2) Friends (bidirectional) + some blocks
INSERT INTO user_friends (user_id, friend_id) VALUES
    (2,3),(3,2),
    (2,4),(4,2),
    (3,5),(5,3),
    (4,5),(5,4);

INSERT INTO user_blocked (user_id, blocked_user_id, created_at) VALUES
    (2,10, NOW()-INTERVAL '1 hour'),
    (7,2,  NOW()-INTERVAL '2 days');

-- 3) Places
INSERT INTO places (name,country,city,description,average_rating,created_at) VALUES
    ('Kalemegdan','Serbia','Belgrade','Fortress & park #fortress #history',4.5,NOW()-INTERVAL '10 days'),
    ('Zlatibor','Serbia','Zlatibor','Mountain resort #nature',4.2,NOW()-INTERVAL '5 days'),
    ('Tara National Park','Serbia','Bajina Basta','Views #hiking #park',4.7,NOW()-INTERVAL '2 days');

-- 4) Posts (19 komada; #belgradefood burst u ≤24h za popularan hešteg)
INSERT INTO posts (content, author_id, likes_count, created_at) VALUES
    ('Ćevapi tour in Dorćol #belgradefood #meat',              6,  0, NOW()-INTERVAL '6 hours'),   -- 1
    ('Best burek in Vračar? #belgradefood #burek',            7,  0, NOW()-INTERVAL '5 hours'),   -- 2  (popular posle UPDATE)
    ('Kafane crawl tonight #belgradefood #night',             8,  0, NOW()-INTERVAL '4 hours'),   -- 3
    ('Street food fest at Kalenić #belgradefood #fest',       9,  0, NOW()-INTERVAL '3 hours'),   -- 4
    ('New bakery opened! #belgradefood #bakery',             10,  0, NOW()-INTERVAL '2 hours'),   -- 5
    ('Hidden kafana gem #belgradefood #rakija',              11,  0, NOW()-INTERVAL '1 hours'),   -- 6
    ('Sunset run Ada Ciganlija #running #belgrade',           3,  0, NOW()-INTERVAL '20 hours'),  -- 7  friend
    ('Arduino meetup tonight #hardware #belgrade',            4,  0, NOW()-INTERVAL '2 hours'),   -- 8  friend
    ('Old trip photo #throwback',                             3,  0, NOW()-INTERVAL '3 days'),    -- 9  old
    ('Weekend hike #tara #hiking',                            4,  0, NOW()-INTERVAL '40 hours'),  -- 10 old
    ('FPGA 101 #hardware #fpga',                              6,  0, NOW()-INTERVAL '8 hours'),   -- 11
    ('React + Spring tips #java #react',                      7,  0, NOW()-INTERVAL '6 hours'),   -- 12
    ('Grilled cheese wars #food',                             8,  0, NOW()-INTERVAL '7 hours'),   -- 13
    ('Kalemegdan views #belgrade #travel',                    9,  0, NOW()-INTERVAL '9 hours'),   -- 14
    ('Zlatibor morning #zlatibor #nature',                   10,  0, NOW()-INTERVAL '26 hours'),  -- 15 old
    ('Night coding session #java',                           11,  0, NOW()-INTERVAL '22 hours'),  -- 16
    ('Bakeries map #bakery #belgrade',                        6,  0, NOW()-INTERVAL '3 hours'),   -- 17
    ('Belgrade gourmet #belgradefood #guide',                 7,  0, NOW()-INTERVAL '55 minutes'),-- 18
    ('My first post #hello',                                  2,  0, NOW()-INTERVAL '1 hours');   -- 19

-- 5) Post hashtags
INSERT INTO post_hashtags (post_id, hashtag) VALUES
    (1,'belgradefood'),(1,'meat'),
    (2,'belgradefood'),(2,'burek'),
    (3,'belgradefood'),(3,'night'),
    (4,'belgradefood'),(4,'fest'),
    (5,'belgradefood'),(5,'bakery'),
    (6,'belgradefood'),(6,'rakija'),
    (18,'belgradefood'),(18,'guide'),
    (7,'running'),(7,'belgrade'),
    (8,'hardware'),(8,'belgrade'),
    (9,'throwback'),
    (10,'tara'),(10,'hiking'),
    (11,'hardware'),(11,'fpga'),
    (12,'java'),(12,'react'),
    (13,'food'),
    (14,'belgrade'),(14,'travel'),
    (15,'zlatibor'),(15,'nature'),
    (16,'java'),
    (17,'bakery'),(17,'belgrade'),
    (19,'hello');

-- 6) Likes - STRATEGIJA: User 6 i User 12 sa VISOKIM overlapom + POSTOVI KOJE DRUGI NE LAJKUJU
-- Cilj: Pearson >= 0.5 - ključ je da lajkuju iste + oba NE lajkuju iste!
-- User 2 lajkuje: {1,2,3,4,5,6,7,8,11,12,14,16,17,18} = 14 postova (OSTAJE NETAKNUTO)
-- User 6 lajkuje: {2,3,4,5,11,12,18} = 7 postova
-- User 12 lajkuje: {2,3,4,5,11,12,19} = 7 postova
-- OBA lajkuju: {2,3,4,5,11,12} = 6 postova
-- OBA NE lajkuju: {1,6,7,8,9,10,13,14,15,16,17} = mnogo (+ korelacija)
-- Razlike: 6 lajkuje {18}, 12 lajkuje {19}
-- Union sa SVIM drugim korisnicima će dati prostor za varijansu!

-- Post 1 (9) - User 2 + DRUGI (NE 6 NI 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (1,2),(1,3),(1,4),(1,5),(1,7),(1,8),(1,9),(1,10),(1,11);

-- Post 2 (12)  POPULAR - User 2, User 6, User 12 + DRUGI
INSERT INTO post_likes (post_id,user_id) VALUES
    (2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(2,9),(2,10),(2,11),(2,12);

-- Post 3 (12)  POPULAR - User 2, User 6, User 12 + DRUGI
INSERT INTO post_likes (post_id,user_id) VALUES
    (3,2),(3,3),(3,4),(3,5),(3,6),(3,7),(3,8),(3,9),(3,10),(3,11),(3,12);

-- Post 4 (8) - User 2, User 6, User 12 + DRUGI
INSERT INTO post_likes (post_id,user_id) VALUES
    (4,2),(4,3),(4,4),(4,5),(4,6),(4,7),(4,8),(4,12);

-- Post 5 (7) - User 2, User 6, User 12 + DRUGI
INSERT INTO post_likes (post_id,user_id) VALUES
    (5,2),(5,3),(5,4),(5,6),(5,7),(5,8),(5,12);

-- Post 6 (5) - User 2 + DRUGI (NE 6 NI 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (6,2),(6,3),(6,4),(6,5);

-- Post 7 (4) - User 2 + DRUGI (NE 6 NI 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (7,2),(7,3),(7,4),(7,5);

-- Post 8 (3) - User 2 + DRUGI (NE 6 NI 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (8,2),(8,4);

-- Post 9 (2) - DRUGI (NE 2, NE 6, NE 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (9,3),(9,5);

-- Post 10 (2) - DRUGI (NE 2, NE 6, NE 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (10,4),(10,5);

-- Post 11 (7) - User 2, User 6, User 12 + DRUGI
INSERT INTO post_likes (post_id,user_id) VALUES
    (11,2),(11,3),(11,6),(11,7),(11,8),(11,10),(11,12);

-- Post 12 (5) - User 2, User 6, User 12 + DRUGI
INSERT INTO post_likes (post_id,user_id) VALUES
    (12,2),(12,3),(12,6),(12,11),(12,12);

-- Post 13 (4) - DRUGI (NE 2, NE 6, NE 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (13,3),(13,5),(13,7),(13,8);

-- Post 14 (4) - User 2 + DRUGI (NE 6 NI 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (14,2),(14,3),(14,9),(14,11);

-- Post 15 (2) - DRUGI (NE 2, NE 6, NE 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (15,4),(15,5);

-- Post 16 (3) - User 2 + DRUGI (NE 6 NI 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (16,2),(16,7),(16,11);

-- Post 17 (6) - User 2 + DRUGI (NE 6 NI 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (17,2),(17,3),(17,7),(17,8),(17,9),(17,10);

-- Post 18 (13)  POPULAR - User 2, User 6 + DRUGI (NE 12)
INSERT INTO post_likes (post_id,user_id) VALUES
    (18,1),(18,2),(18,3),(18,4),(18,5),(18,6),
    (18,7),(18,8),(18,9),(18,10),(18,11),
    (18,13),(18,14);

-- Post 19 (3) - User 12 + DRUGI (NE 2, NE 6)
INSERT INTO post_likes (post_id,user_id) VALUES
    (19,3),(19,5),(19,12);

-- 7) Harmonizuj likes_count
WITH c AS (
    SELECT p.id, COUNT(pl.user_id)::int AS cnt
    FROM posts p LEFT JOIN post_likes pl ON pl.post_id = p.id
    GROUP BY p.id
)
UPDATE posts p
SET likes_count = c.cnt
FROM c
WHERE c.id = p.id;

-- 8) Ratings
INSERT INTO ratings (score, description, user_id, place_id, created_at) VALUES
    (5,'Amazing #belgradefood options nearby', 2, 1, NOW()-INTERVAL '1 days'),
    (4,'Great nature #hiking',                 4, 3, NOW()-INTERVAL '20 hours'),
    (3,'Nice views',                           3, 1, NOW()-INTERVAL '3 days');

INSERT INTO rating_hashtags (rating_id, hashtag) VALUES
    (1,'belgradefood'),(2,'hiking');

-- 9) Reports (noise)
INSERT INTO post_reports (post_id,user_id) VALUES (13,9) ON CONFLICT DO NOTHING;

-- ===========================
-- PART 2: MODERATION TEST DATA
-- ===========================

-- Scenario 1: Više od 5 prijava u 24h - zabrana postovanja 24h
-- Target: korisnik 10
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp) VALUES 
    (1, 10, NULL, 'Spam', NOW() - INTERVAL '2 hours'),
    (2, 10, NULL, 'Harassment', NOW() - INTERVAL '4 hours'),
    (3, 10, NULL, 'Offensive', NOW() - INTERVAL '6 hours'),
    (4, 10, NULL, 'Inappropriate', NOW() - INTERVAL '8 hours'),
    (5, 10, NULL, 'Spam', NOW() - INTERVAL '10 hours'),
    (6, 10, NULL, 'Harassment', NOW() - INTERVAL '12 hours');

-- Dodaj i jedno blokiranje (note: već postoji 2->10 iz feed data)
INSERT INTO user_blocked (user_id, blocked_user_id, created_at) VALUES 
    (3, 10, NOW() - INTERVAL '3 hours')
ON CONFLICT DO NOTHING;

-- Scenario 2: Više od 8 prijava u 48h - zabrana postovanja 48h
-- Target: korisnik 11
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp) VALUES 
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
-- Target: korisnik 12 (Extra - NEW user)
INSERT INTO user_blocked (user_id, blocked_user_id, created_at) VALUES 
    (1, 12, NOW() - INTERVAL '2 hours'),
    (3, 12, NOW() - INTERVAL '5 hours'),
    (4, 12, NOW() - INTERVAL '8 hours'),
    (5, 12, NOW() - INTERVAL '11 hours'),
    (6, 12, NOW() - INTERVAL '14 hours')
ON CONFLICT DO NOTHING;

-- Scenario 4: Kombinovani slučaj - 2+ bloka u 48h I 4+ prijava u 24h
-- Target: korisnik 13
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp) VALUES 
    (1, 13, NULL, 'Spam', NOW() - INTERVAL '2 hours'),
    (2, 13, NULL, 'Harassment', NOW() - INTERVAL '4 hours'),
    (3, 13, NULL, 'Offensive', NOW() - INTERVAL '6 hours'),
    (4, 13, NULL, 'Inappropriate', NOW() - INTERVAL '8 hours'),
    (5, 13, NULL, 'Spam', NOW() - INTERVAL '10 hours');

INSERT INTO user_blocked (user_id, blocked_user_id, created_at) VALUES 
    (1, 13, NOW() - INTERVAL '12 hours'),
    (3, 13, NOW() - INTERVAL '24 hours'),
    (4, 13, NOW() - INTERVAL '36 hours')
ON CONFLICT DO NOTHING;

-- Scenario 5: Brza eskalacija - više od 3 blokiranja u 6h
-- Target: korisnik 14
INSERT INTO user_blocked (user_id, blocked_user_id, created_at) VALUES 
    (1, 14, NOW() - INTERVAL '1 hour'),
    (2, 14, NOW() - INTERVAL '2 hours'),
    (3, 14, NOW() - INTERVAL '3 hours'),
    (4, 14, NOW() - INTERVAL '4 hours')
ON CONFLICT DO NOTHING;

-- Scenario 6: Hronično problematično ponašanje - više od 12 prijava u 7 dana
-- Target: korisnik 15
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp) VALUES 
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
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp) VALUES 
    (1, 16, NULL, 'Spam', NOW() - INTERVAL '2 hours'),
    (2, 16, NULL, 'Harassment', NOW() - INTERVAL '4 hours'),
    (3, 16, NULL, 'Offensive', NOW() - INTERVAL '6 hours'),
    (4, 16, NULL, 'Inappropriate', NOW() - INTERVAL '8 hours'),
    (5, 16, NULL, 'Spam', NOW() - INTERVAL '10 hours'),
    (6, 16, NULL, 'Harassment', NOW() - INTERVAL '12 hours'),
    (7, 16, NULL, 'Offensive', NOW() - INTERVAL '30 hours'),
    (8, 16, NULL, 'Inappropriate', NOW() - INTERVAL '35 hours'),
    (9, 16, NULL, 'Spam', NOW() - INTERVAL '40 hours');

INSERT INTO user_blocked (user_id, blocked_user_id, created_at) VALUES 
    (1, 16, NOW() - INTERVAL '3 hours'),
    (2, 16, NOW() - INTERVAL '6 hours')
ON CONFLICT DO NOTHING;

-- Scenario 8: Korisnik blizu granice ali ne prelazi (tačno 5 prijava, ne prelazi granicu)
-- Target: korisnik 17
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp) VALUES 
    (1, 17, NULL, 'Spam', NOW() - INTERVAL '2 hours'),
    (2, 17, NULL, 'Harassment', NOW() - INTERVAL '4 hours'),
    (3, 17, NULL, 'Offensive', NOW() - INTERVAL '6 hours'),
    (4, 17, NULL, 'Inappropriate', NOW() - INTERVAL '8 hours'),
    (5, 17, NULL, 'Spam', NOW() - INTERVAL '10 hours');

-- Scenario 9: Stare prijave koje ne bi trebalo da triggeruju pravila (prijave starije od 7 dana)
-- Target: korisnik 18
INSERT INTO user_reports (reporter_id, author_id, post_id, reason, timestamp) VALUES 
    (1, 18, NULL, 'Spam', NOW() - INTERVAL '8 days'),
    (2, 18, NULL, 'Harassment', NOW() - INTERVAL '9 days'),
    (3, 18, NULL, 'Offensive', NOW() - INTERVAL '10 days'),
    (4, 18, NULL, 'Inappropriate', NOW() - INTERVAL '11 days'),
    (5, 18, NULL, 'Spam', NOW() - INTERVAL '12 days');

-- ===========================
-- SUMMARY
-- ===========================

SELECT '✅ Combined test data uspešno učitani!' AS status;
SELECT '' AS blank;
SELECT '📊 FEED TEST USERS:' AS info;
SELECT '  - User 2 (Marko) - BASE korisnik sa prijateljima, očekuje score >= 5' AS rezultat;
SELECT '  - User 12 (Extra) - NEW korisnik bez prijatelja, očekuje similarity >= 0.5' AS rezultat;
SELECT '' AS blank;
SELECT '🔒 MODERATION TEST USERS:' AS info;
SELECT '  - Očekuje se 6-7 sumljivih korisnika (ID: 10, 11, 12, 13, 14, 15, 16)' AS rezultat;
SELECT '  - Korisnik 17 NE bi trebalo da se detektuje (tačno na granici)' AS rezultat;
SELECT '  - Korisnik 18 NE bi trebalo da se detektuje (stare prijave)' AS rezultat;
