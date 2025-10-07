-- ===========================
-- CLEAN SEED (PostgreSQL)
-- ===========================

-- 0) Clean state
TRUNCATE rating_hashtags, place_hashtags, post_hashtags,
         user_blocked, user_friends, post_reports, post_likes,
         ratings, places, posts, users RESTART IDENTITY CASCADE;

-- 1) Users (password = '123456' for all)
-- bcrypt (cost=10): $2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG
INSERT INTO users (first_name,last_name,email,password,city,is_admin) VALUES
                                                                          ('Admin','User','admin@socialnet.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Belgrade',true),   -- 1
                                                                          ('Marko','Petrović','marko@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Belgrade',false), -- 2  TARGET
                                                                          ('Ana','Jovanović','ana@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Novi Sad',false),    -- 3
                                                                          ('Stefan','Nikolić','stefan@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Kragujevac',false),-- 4
                                                                          ('Milica','Stojanović','milica@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Niš',false),     -- 5
                                                                          ('Luka','Ilić','luka@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Belgrade',false),       -- 6
                                                                          ('Sara','Matić','sara@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Belgrade',false),      -- 7
                                                                          ('Ivan','Kovač','ivan@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Novi Sad',false),       -- 8
                                                                          ('Jelena','Marković','jelena@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Niš',false),      -- 9
                                                                          ('Nikola','Milošević','nikola@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Subotica',false),-- 10
                                                                          ('Tamara','Popović','tamara@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Čačak',false),     -- 11
                                                                          ('Extra','User','extra@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Belgrade',false),      -- 12
                                                                          ('Mina','Jurić','mina@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Novi Sad',false),       -- 13
                                                                          ('Vlada','Stošić','vlada@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Niš',false),         -- 14
                                                                          ('Nina','Kostić','nina@example.com','$2b$10$voWv54I9ZT6u7KYk5h1DB.Jz/kao9iMVEVQkVXboE7fwO9XqiWYCG','Belgrade',false);      -- 15

-- 2) Friends (bidirectional) + some blocks
INSERT INTO user_friends (user_id, friend_id) VALUES
                                                  (2,3),(3,2),
                                                  (2,4),(4,2),
                                                  (3,5),(5,3),
                                                  (4,5),(5,4);

INSERT INTO user_blocked (user_id, blocked_user_id) VALUES
                                                        (2,10),
                                                        (7,2);

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
                                                 (17,'bakery'),(17,'belgrade');

-- 6) Likes (samo validni user_id ∈ [1..15]; bez duplikata)
-- Dizajn:
--  * post 2 likeri: 12 korisnika {1..12}
--  * post 18 likeri: istih 12 + {13,14} ⇒ 14 total, overlap 12/14 ≈ 85.7%
--  * ostali formiraju visoko preklapanje 2↔3 korisnika i popularnost (>10 lajkova u ≤24h)

-- Post 1 (9)
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,11);

-- Post 2 (12)  POPULAR
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(2,9),(2,10),(2,11),(2,12);

-- Post 3 (11)  POPULAR
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (3,2),(3,3),(3,4),(3,5),(3,6),(3,7),(3,8),(3,9),(3,10),(3,11),(3,12);

-- Post 4 (7)
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (4,2),(4,3),(4,4),(4,5),(4,6),(4,7),(4,8);

-- Post 5 (6)
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (5,2),(5,3),(5,4),(5,6),(5,7),(5,8);

-- Post 6 (5)
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (6,2),(6,3),(6,4),(6,5),(6,6);

-- Post 7 (4)  friend ≤24h
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (7,2),(7,3),(7,4),(7,5);

-- Post 8 (3)  friend ≤24h
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (8,2),(8,4),(8,6);

-- Post 11 (7)  (popular po >10 nije nužno; fokus na sličnost korisnika)
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (11,2),(11,3),(11,6),(11,7),(11,8),(11,10),(11,12);

-- Post 12 (4)
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (12,2),(12,3),(12,7),(12,11);

-- Post 13 (4)
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (13,3),(13,5),(13,7),(13,8);

-- Post 14 (4)
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (14,2),(14,3),(14,9),(14,11);

-- Post 15 (2)  old >24h
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (15,4),(15,5);

-- Post 16 (3)
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (16,2),(16,7),(16,11);

-- Post 17 (7)
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (17,2),(17,3),(17,6),(17,7),(17,8),(17,9),(17,10);

-- Post 18 (14)  POPULAR + popular hashtag, ≥70% overlap sa post 2
INSERT INTO post_likes (post_id,user_id) VALUES
                                             (18,1),(18,2),(18,3),(18,4),(18,5),(18,6),
                                             (18,7),(18,8),(18,9),(18,10),(18,11),(18,12),
                                             (18,13),(18,14);

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

-- 8) Ratings (opciono)
INSERT INTO ratings (score, description, user_id, place_id, created_at) VALUES
                                                                            (5,'Amazing #belgradefood options nearby', 2, 1, NOW()-INTERVAL '1 days'),
                                                                            (4,'Great nature #hiking',                 4, 3, NOW()-INTERVAL '20 hours'),
                                                                            (3,'Nice views',                           3, 1, NOW()-INTERVAL '3 days');

INSERT INTO rating_hashtags (rating_id, hashtag) VALUES
                                                     (1,'belgradefood'),(2,'hiking');

-- 9) Reports (noise)
INSERT INTO post_reports (post_id,user_id) VALUES (13,9) ON CONFLICT DO NOTHING;

-- 10) (po potrebi) Marko kreira objavu sa popularnim heštegom
-- INSERT INTO post_hashtags (post_id,hashtag) VALUES (19,'belgradefood');

-- 11) Verifikacije (po potrebi otkomentarisati)
-- SELECT id, content, created_at, likes_count FROM posts ORDER BY id;
-- SELECT post_id, COUNT(*) FROM post_likes GROUP BY post_id ORDER BY post_id;
-- SELECT hashtag, COUNT(*) FROM post_hashtags
--  WHERE post_id IN (SELECT id FROM posts WHERE created_at >= NOW()-INTERVAL '24 hours')
--  GROUP BY hashtag ORDER BY COUNT(*) DESC;
