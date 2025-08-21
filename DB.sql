-- =========================================================
-- Udemy-like Database Schema (PostgreSQL)
-- =========================================================
-- Requirements:
--   - PostgreSQL 13+
--   - pgcrypto extension for gen_random_uuid()
-- =========================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS citext;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- -----------------------------
-- Enum types
-- -----------------------------
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
    CREATE TYPE user_role AS ENUM ('student', 'instructor', 'admin');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'payment_status') THEN
    CREATE TYPE payment_status AS ENUM ('pending', 'paid', 'failed', 'refunded');
  END IF;

  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'payout_status') THEN
    CREATE TYPE payout_status AS ENUM ('pending', 'paid');
  END IF;
END$$;

-- -----------------------------
-- Users
-- -----------------------------
CREATE TABLE IF NOT EXISTS users (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  email            CITEXT UNIQUE NOT NULL,
  full_name        TEXT NOT NULL,
  password         TEXT NOT NULL,              -- bcrypt hash
  role             user_role NOT NULL DEFAULT 'student',
  bio              TEXT,
  profile_picture  TEXT,                       -- URL
  social_links     JSONB,                      -- { "github": "...", "linkedin": "..." }
  last_login_at    TIMESTAMPTZ,
  created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Helpful index for role-based lookups
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- -----------------------------
-- Courses
-- -----------------------------
CREATE TABLE IF NOT EXISTS courses (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  title            TEXT NOT NULL,
  description      TEXT,
  price            NUMERIC(10,2) NOT NULL DEFAULT 0.00,
  duration_minutes INTEGER NOT NULL DEFAULT 0,           -- total duration (minutes)
  rating_count     INTEGER NOT NULL DEFAULT 0 CHECK (rating_count >= 0),
  rating_average   NUMERIC(3,2) NOT NULL DEFAULT 0.00 CHECK (rating_average >= 0 AND rating_average <= 5),
  enrolled_count   INTEGER NOT NULL DEFAULT 0 CHECK (enrolled_count >= 0),
  language         TEXT,
  thumbnail        TEXT,                                  -- URL
  created_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at       TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  instructor_id    UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT
    -- You can enforce instructor role at the app layer or with a trigger.
);

CREATE INDEX IF NOT EXISTS idx_courses_instructor ON courses(instructor_id);
CREATE INDEX IF NOT EXISTS idx_courses_title_trgm ON courses USING GIN (title gin_trgm_ops);
CREATE INDEX IF NOT EXISTS idx_courses_description_trgm ON courses USING GIN (description gin_trgm_ops);

-- -----------------------------
-- Categories & CourseCategories (M2M)
-- -----------------------------
CREATE TABLE IF NOT EXISTS categories (
  id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name  TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS course_categories (
  course_id   UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
  category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
  PRIMARY KEY (course_id, category_id)
);

-- -----------------------------
-- Tags & CourseTags (M2M)
-- -----------------------------
CREATE TABLE IF NOT EXISTS tags (
  id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name  TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS course_tags (
  course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
  tag_id    UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
  PRIMARY KEY (course_id, tag_id)
);

-- -----------------------------
-- Sections & Videos
-- -----------------------------
CREATE TABLE IF NOT EXISTS sections (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  course_id  UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
  title      TEXT NOT NULL,
  position   INTEGER NOT NULL DEFAULT 1 CHECK (position >= 1),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (course_id, position)
);

CREATE INDEX IF NOT EXISTS idx_sections_course ON sections(course_id);

CREATE TABLE IF NOT EXISTS videos (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  section_id  UUID NOT NULL REFERENCES sections(id) ON DELETE CASCADE,
  title       TEXT NOT NULL,
  url         TEXT NOT NULL,         -- storage/streaming URL
  duration_seconds INTEGER NOT NULL DEFAULT 0 CHECK (duration_seconds >= 0),
  position    INTEGER NOT NULL DEFAULT 1 CHECK (position >= 1),
  created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (section_id, position)
);

CREATE INDEX IF NOT EXISTS idx_videos_section ON videos(section_id);

-- -----------------------------
-- Enrollments (one per user/course)
-- -----------------------------
CREATE TABLE IF NOT EXISTS enrollments (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  course_id   UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
  enrolled_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  progress    NUMERIC(5,2) NOT NULL DEFAULT 0.00 CHECK (progress >= 0 AND progress <= 100),
  completed   BOOLEAN NOT NULL DEFAULT FALSE,
  UNIQUE (user_id, course_id)
);

CREATE INDEX IF NOT EXISTS idx_enrollments_user ON enrollments(user_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_course ON enrollments(course_id);

-- -----------------------------
-- Reviews (comments + optional rating)
-- -----------------------------
CREATE TABLE IF NOT EXISTS reviews (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  course_id  UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
  comment    TEXT,
  rating     INTEGER CHECK (rating BETWEEN 1 AND 5),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (user_id, course_id)   -- one review per user per course (adjust if you want multiples)
);

CREATE INDEX IF NOT EXISTS idx_reviews_course ON reviews(course_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user ON reviews(user_id);

-- -----------------------------
-- Wishlist (one row per user/course)
-- -----------------------------
CREATE TABLE IF NOT EXISTS wishlists (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id    UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  course_id  UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
  added_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  UNIQUE (user_id, course_id)
);

CREATE INDEX IF NOT EXISTS idx_wishlists_user ON wishlists(user_id);

-- -----------------------------
-- Payments / Orders
-- -----------------------------
CREATE TABLE IF NOT EXISTS payments (
  id               UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- order_id
  user_id          UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  course_id        UUID NOT NULL REFERENCES courses(id) ON DELETE RESTRICT,
  amount           NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
  payment_status   payment_status NOT NULL DEFAULT 'pending',
  transaction_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  method           TEXT NOT NULL,  -- e.g., 'stripe', 'paypal', 'credit_card'
  -- If you allow buying the same course multiple times (e.g., gifts), drop this:
  UNIQUE (user_id, course_id, payment_status) 
);

CREATE INDEX IF NOT EXISTS idx_payments_user ON payments(user_id);
CREATE INDEX IF NOT EXISTS idx_payments_course ON payments(course_id);

-- -----------------------------
-- Instructor Payouts
-- -----------------------------
CREATE TABLE IF NOT EXISTS instructor_payouts (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  instructor_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  course_id      UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
  amount         NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
  payout_status  payout_status NOT NULL DEFAULT 'pending',
  payout_date    TIMESTAMPTZ,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_payouts_instructor ON instructor_payouts(instructor_id);
CREATE INDEX IF NOT EXISTS idx_payouts_course ON instructor_payouts(course_id);

-- -----------------------------
-- Housekeeping triggers (optional but recommended)
-- -----------------------------
-- Auto-update updated_at on row changes (example for courses; repeat as needed)
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname = 'trg_courses_set_updated_at'
  ) THEN
    CREATE TRIGGER trg_courses_set_updated_at
    BEFORE UPDATE ON courses
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname = 'trg_sections_set_updated_at'
  ) THEN
    CREATE TRIGGER trg_sections_set_updated_at
    BEFORE UPDATE ON sections
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname = 'trg_videos_set_updated_at'
  ) THEN
    CREATE TRIGGER trg_videos_set_updated_at
    BEFORE UPDATE ON videos
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;

  IF NOT EXISTS (
    SELECT 1 FROM pg_trigger WHERE tgname = 'trg_users_set_updated_at'
  ) THEN
    CREATE TRIGGER trg_users_set_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END$$;

-- -----------------------------
-- (Optional) Maintain enrolled_count and rating aggregates with views or triggers.
-- You can compute these on the fly or use materialized views for performance.
-- -----------------------------

-- Example views:
CREATE OR REPLACE VIEW course_enrollment_stats AS
SELECT
  c.id AS course_id,
  COUNT(e.id) AS enrolled_count
FROM courses c
LEFT JOIN enrollments e ON e.course_id = c.id
GROUP BY c.id;

CREATE OR REPLACE VIEW course_rating_stats AS
SELECT
  c.id AS course_id,
  COUNT(r.id) AS rating_count,
  COALESCE(AVG(r.rating)::NUMERIC(3,2), 0.00) AS rating_average
FROM courses c
LEFT JOIN reviews r ON r.course_id = c.id AND r.rating IS NOT NULL
GROUP BY c.id;
