/* Cara membuat db dari terminal: sqlite3 vbook.db < villa_booking.sql */

CREATE TABLE `villas` (
  `id` INTEGER PRIMARY KEY,
  `name` TEXT NOT NULL,
  `description` text NOT NULL,
  `address` text NOT NULL
);

CREATE TABLE `room_types` (
  `id` INTEGER PRIMARY KEY,
  `villa` INTEGER NOT NULL,
  `name` TEXT NOT NULL,
  `quantity` INTEGER DEFAULT 1,
  `capacity` INTEGER DEFAULT 1,
  `price` INTEGER NOT NULL,
  `bed_size` TEXT NOT NULL /* hanya bernilai: double, queen, king */,
  `has_desk` INTEGER DEFAULT 0 /* hanya bernilai 0 dan 1 (boolean) */,
  `has_ac` INTEGER DEFAULT 0 /* hanya bernilai 0 dan 1 (boolean) */,
  `has_tv` INTEGER DEFAULT 0 /* hanya bernilai 0 dan 1 (boolean) */,
  `has_wifi` INTEGER DEFAULT 0 /* hanya bernilai 0 dan 1 (boolean) */,
  `has_shower` INTEGER DEFAULT 0 /* hanya bernilai 0 dan 1 (boolean) */,
  `has_hotwater` INTEGER DEFAULT 0 /* hanya bernilai 0 dan 1 (boolean) */,
  `has_fridge` INTEGER DEFAULT 0 /* hanya bernilai 0 dan 1 (boolean) */
);

CREATE TABLE `customers` (
  `id` INTEGER PRIMARY KEY,
  `name` TEXT NOT NULL,
  `email` TEXT NOT NULL,
  `phone` TEXT
);

CREATE TABLE `bookings` (
  `id` INTEGER PRIMARY KEY,
  `customer` INTEGER,
  `room_type` INTEGER,
  `checkin_date` TEXT NOT NULL, /* timestamp dalam format YYYY-MM-DD hh:mm:ss */
  `checkout_date` TEXT NOT NULL, /* timestamp dalam format YYYY-MM-DD hh:mm:ss */
  `price` INTEGER,
  `voucher` INTEGER,
  `final_price` INTEGER,
  `payment_status` TEXT DEFAULT 'waiting' /* hanya bernilai: waiting, failed, success */,
  `has_checkedin` INTEGER DEFAULT 0 /* hanya bernilai 0 dan 1 (boolean) */,
  `has_checkedout` INTEGER DEFAULT 0 /* hanya bernilai 0 dan 1 (boolean) */
);

CREATE TABLE `reviews` (
  `booking` INTEGER PRIMARY KEY,
  `star` INTEGER NOT NULL,
  `title` TEXT NOT NULL,
  `content` TEXT NOT NULL
);

CREATE TABLE `vouchers` (
  `id` INTEGER PRIMARY KEY,
  `code` TEXT NOT NULL,
  `description` TEXT NOT NULL,
  `discount` REAL NOT NULL,
  `start_date` TEXT NOT NULL, /* timestamp dalam format YYYY-MM-DD hh:mm:ss */
  `end_date` TEXT NOT NULL /* timestamp dalam format YYYY-MM-DD hh:mm:ss */
);


