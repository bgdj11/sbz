-- =============================================
-- DDL Script for SocialNet Database (PostgreSQL)
-- =============================================

-- Create database (run this first if database doesn't exist)
-- CREATE DATABASE sbz;

-- Use the database
-- \c sbz;

-- =============================================
-- 1. Users table
-- =============================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    city VARCHAR(255),
    is_admin BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 2. Posts table
-- =============================================
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    likes_count INTEGER DEFAULT 0,
    reports_count INTEGER DEFAULT 0,
    author_id BIGINT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- 3. Places table
-- =============================================
CREATE TABLE places (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    description TEXT,
    average_rating DECIMAL(3,2) DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- 4. Ratings table
-- =============================================
CREATE TABLE ratings (
    id BIGSERIAL PRIMARY KEY,
    score INTEGER NOT NULL CHECK (score >= 1 AND score <= 5),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE,
    UNIQUE(user_id, place_id) -- One rating per user per place
);

-- =============================================
-- 5. Post Likes (Many-to-Many)
-- =============================================
CREATE TABLE post_likes (
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- 6. Post Reports (Many-to-Many)
-- =============================================
CREATE TABLE post_reports (
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- =============================================
-- 7. User Friends (Many-to-Many - Bidirectional)
-- =============================================
CREATE TABLE user_friends (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (user_id != friend_id) -- User cannot be friend with themselves
);

-- =============================================
-- 8. User Blocked (Many-to-Many)
-- =============================================
CREATE TABLE user_blocked (
    user_id BIGINT NOT NULL,
    blocked_user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, blocked_user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (blocked_user_id) REFERENCES users(id) ON DELETE CASCADE,
    CHECK (user_id != blocked_user_id) -- User cannot block themselves
);

-- =============================================
-- 9. Post Hashtags (One-to-Many via ElementCollection)
-- =============================================
CREATE TABLE post_hashtags (
    post_id BIGINT NOT NULL,
    hashtag VARCHAR(255) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE
);

-- =============================================
-- 10. Place Hashtags (One-to-Many via ElementCollection)
-- =============================================
CREATE TABLE place_hashtags (
    place_id BIGINT NOT NULL,
    hashtag VARCHAR(255) NOT NULL,
    FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE
);

-- =============================================
-- 11. Rating Hashtags (One-to-Many via ElementCollection)
-- =============================================
CREATE TABLE rating_hashtags (
    rating_id BIGINT NOT NULL,
    hashtag VARCHAR(255) NOT NULL,
    FOREIGN KEY (rating_id) REFERENCES ratings(id) ON DELETE CASCADE
);

-- =============================================
-- INDEXES for better performance
-- =============================================

-- Users indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_is_admin ON users(is_admin);

-- Posts indexes
CREATE INDEX idx_posts_author_id ON posts(author_id);
CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_posts_likes_count ON posts(likes_count DESC);

-- Places indexes
CREATE INDEX idx_places_country ON places(country);
CREATE INDEX idx_places_city ON places(city);
CREATE INDEX idx_places_average_rating ON places(average_rating DESC);
CREATE INDEX idx_places_name ON places(name);

-- Ratings indexes
CREATE INDEX idx_ratings_user_id ON ratings(user_id);
CREATE INDEX idx_ratings_place_id ON ratings(place_id);
CREATE INDEX idx_ratings_score ON ratings(score);
CREATE INDEX idx_ratings_created_at ON ratings(created_at DESC);

-- Hashtags indexes
CREATE INDEX idx_post_hashtags_hashtag ON post_hashtags(hashtag);
CREATE INDEX idx_place_hashtags_hashtag ON place_hashtags(hashtag);
CREATE INDEX idx_rating_hashtags_hashtag ON rating_hashtags(hashtag);

-- Friend relationships indexes
CREATE INDEX idx_user_friends_user_id ON user_friends(user_id);
CREATE INDEX idx_user_friends_friend_id ON user_friends(friend_id);

-- =============================================
-- SAMPLE DATA (Optional)
-- =============================================

-- Insert admin user
INSERT INTO users (first_name, last_name, email, password, city, is_admin) VALUES
('Admin', 'User', 'admin@socialnet.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Belgrade', true);

-- Insert regular users (password is 'password123' bcrypted)
INSERT INTO users (first_name, last_name, email, password, city, is_admin) VALUES
('Marko', 'Petrovic', 'marko@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Belgrade', false),
('Ana', 'Jovanovic', 'ana@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Novi Sad', false),
('Stefan', 'Nikolic', 'stefan@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Kragujevac', false),
('Milica', 'Stojanovic', 'milica@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'Nis', false);

-- Insert sample places
INSERT INTO places (name, country, city, description, average_rating) VALUES
('Kalemegdan', 'Serbia', 'Belgrade', 'Historic fortress and park in the heart of Belgrade #fortress #history #park', 4.5),
('Petrovaradin Fortress', 'Serbia', 'Novi Sad', 'Beautiful fortress overlooking the Danube river #fortress #danube #music', 4.3),
('Studenica Monastery', 'Serbia', 'Kraljevo', 'UNESCO World Heritage medieval monastery #monastery #unesco #medieval', 4.8),
('Zlatibor', 'Serbia', 'Zlatibor', 'Mountain resort perfect for relaxation #mountain #nature #resort', 4.2),
('Tara National Park', 'Serbia', 'Bajina Basta', 'Stunning natural park with amazing views #nature #park #hiking', 4.7);

-- Insert sample posts
INSERT INTO posts (content, author_id, likes_count) VALUES
('Just visited Kalemegdan! Amazing views of the Danube. #belgrade #kalemegdan #travel', 2, 5),
('Beautiful sunset at Zlatibor mountain! #zlatibor #sunset #nature', 3, 8),
('History lesson at Studenica Monastery. Such peaceful place. #monastery #history #spirituality', 4, 3),
('Weekend trip to Tara National Park. Breathtaking! #tara #nationalpark #hiking', 2, 12),
('Petrovaradin Fortress is perfect for photography! #novisad #fortress #photography', 5, 6);

-- Insert sample hashtags for posts
INSERT INTO post_hashtags (post_id, hashtag) VALUES
(1, 'belgrade'), (1, 'kalemegdan'), (1, 'travel'),
(2, 'zlatibor'), (2, 'sunset'), (2, 'nature'),
(3, 'monastery'), (3, 'history'), (3, 'spirituality'),
(4, 'tara'), (4, 'nationalpark'), (4, 'hiking'),
(5, 'novisad'), (5, 'fortress'), (5, 'photography');

-- Insert sample ratings
INSERT INTO ratings (score, description, user_id, place_id) VALUES
(5, 'Amazing place! Must visit when in Belgrade. #mustvisit', 2, 1),
(4, 'Great for history lovers. Beautiful architecture. #history #architecture', 3, 1),
(5, 'Perfect weekend getaway. Fresh air and nature. #weekend #nature', 4, 4),
(5, 'UNESCO site for a reason. Incredible medieval art. #unesco #medieval #art', 2, 3),
(4, 'Great for hiking and outdoor activities. #hiking #outdoor', 5, 5);

-- Insert sample friendships (bidirectional)
INSERT INTO user_friends (user_id, friend_id) VALUES
(2, 3), (3, 2), -- Marko and Ana are friends
(2, 4), (4, 2), -- Marko and Stefan are friends
(3, 5), (5, 3), -- Ana and Milica are friends
(4, 5), (5, 4); -- Stefan and Milica are friends

-- Insert sample post likes
INSERT INTO post_likes (post_id, user_id) VALUES
(1, 3), (1, 4), (1, 5), -- Post 1 liked by Ana, Stefan, Milica
(2, 2), (2, 4), (2, 5), -- Post 2 liked by Marko, Stefan, Milica  
(3, 2), (3, 5), -- Post 3 liked by Marko, Milica
(4, 3), (4, 5), -- Post 4 liked by Ana, Milica
(5, 2), (5, 3), (5, 4); -- Post 5 liked by Marko, Ana, Stefan

-- =============================================
-- VERIFICATION QUERIES
-- =============================================

-- Check if tables are created
-- SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';

-- Check sample data
-- SELECT u.first_name, u.last_name, u.email FROM users u;
-- SELECT p.content, u.first_name, u.last_name FROM posts p JOIN users u ON p.author_id = u.id;
-- SELECT pl.name, pl.city, pl.country, pl.average_rating FROM places pl ORDER BY pl.average_rating DESC;