-- Seed training types
INSERT INTO training_types (training_type_name)
SELECT 'Fitness' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'Fitness');

INSERT INTO training_types (training_type_name)
SELECT 'Yoga' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'Yoga');

INSERT INTO training_types (training_type_name)
SELECT 'Zumba' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'Zumba');

INSERT INTO training_types (training_type_name)
SELECT 'Stretching' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'Stretching');

INSERT INTO training_types (training_type_name)
SELECT 'Resistance' WHERE NOT EXISTS (SELECT 1 FROM training_types WHERE training_type_name = 'Resistance');
