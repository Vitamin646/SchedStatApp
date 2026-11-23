-- Starter exercise catalog. OR IGNORE keeps exercises the user already created with the same name.

INSERT OR IGNORE INTO exercise (name, muscle_group) VALUES
	('Squat', 'Legs'),
	('Front Squat', 'Legs'),
	('Leg Press', 'Legs'),
	('Lunge', 'Legs'),
	('Calf Raise', 'Legs'),
	('Deadlift', 'Back'),
	('Barbell Row', 'Back'),
	('Pull-up', 'Back'),
	('Lat Pulldown', 'Back'),
	('Romanian Deadlift', 'Hamstrings'),
	('Hip Thrust', 'Glutes'),
	('Bench Press', 'Chest'),
	('Incline Bench Press', 'Chest'),
	('Dip', 'Chest'),
	('Overhead Press', 'Shoulders'),
	('Lateral Raise', 'Shoulders'),
	('Biceps Curl', 'Arms'),
	('Triceps Extension', 'Arms'),
	('Plank', 'Core');
