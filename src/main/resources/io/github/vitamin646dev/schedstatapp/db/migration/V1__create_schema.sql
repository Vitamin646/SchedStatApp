-- Strength training schema.

CREATE TABLE exercise (
	id           INTEGER PRIMARY KEY AUTOINCREMENT,
	name         TEXT NOT NULL UNIQUE COLLATE NOCASE,
	muscle_group TEXT,
	notes        TEXT
);

CREATE TABLE workout (
	id     INTEGER PRIMARY KEY AUTOINCREMENT,
	date   TEXT NOT NULL,
	status TEXT NOT NULL CHECK (status IN ('PLANNED', 'DONE', 'SKIPPED')),
	notes  TEXT
);

CREATE INDEX idx_workout_date ON workout (date);

CREATE TABLE workout_tag (
	workout_id INTEGER NOT NULL REFERENCES workout (id) ON DELETE CASCADE,
	tag        TEXT NOT NULL,
	PRIMARY KEY (workout_id, tag)
);

CREATE TABLE workout_exercise (
	id          INTEGER PRIMARY KEY AUTOINCREMENT,
	workout_id  INTEGER NOT NULL REFERENCES workout (id) ON DELETE CASCADE,
	exercise_id INTEGER NOT NULL REFERENCES exercise (id) ON DELETE RESTRICT,
	position    INTEGER NOT NULL,
	notes       TEXT
);

CREATE INDEX idx_workout_exercise_workout ON workout_exercise (workout_id);
CREATE INDEX idx_workout_exercise_exercise ON workout_exercise (exercise_id);

CREATE TABLE exercise_set (
	id                  INTEGER PRIMARY KEY AUTOINCREMENT,
	workout_exercise_id INTEGER NOT NULL REFERENCES workout_exercise (id) ON DELETE CASCADE,
	position            INTEGER NOT NULL,
	reps                INTEGER NOT NULL CHECK (reps > 0),
	weight_kg           REAL NOT NULL CHECK (weight_kg >= 0),
	rpe                 REAL CHECK (rpe IS NULL OR (rpe >= 1 AND rpe <= 10))
);

CREATE INDEX idx_exercise_set_workout_exercise ON exercise_set (workout_exercise_id);
