-- Medical Knowledge Database for Chatbot
-- Rule-based symptom assessment and medical advice

-- Medical Knowledge Base Table
CREATE TABLE medical_knowledge (
    id INT PRIMARY KEY AUTO_INCREMENT,
    category VARCHAR(50) NOT NULL,
    symptom VARCHAR(255) NOT NULL,
    condition_name VARCHAR(100) NOT NULL,
    severity ENUM('low', 'medium', 'high', 'emergency') NOT NULL,
    self_care_advice TEXT,
    when_to_see_doctor TEXT,
    emergency_indicators TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Chat Sessions Table
CREATE TABLE chat_sessions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    session_start TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    session_end TIMESTAMP NULL,
    session_status ENUM('active', 'completed', 'escalated') DEFAULT 'active',
    FOREIGN KEY (patient_id) REFERENCES users(id),
    INDEX idx_patient_session (patient_id, session_start)
);

-- Chat Messages Table
CREATE TABLE chat_messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    session_id INT NOT NULL,
    message_type ENUM('user', 'bot') NOT NULL,
    message_content TEXT NOT NULL,
    message_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id),
    INDEX idx_session_messages (session_id, message_timestamp)
);

-- Sample Medical Knowledge Data
INSERT INTO medical_knowledge (category, symptom, condition_name, severity, self_care_advice, when_to_see_doctor, emergency_indicators) VALUES
('respiratory', 'cough', 'Common Cold', 'low', 
 'Rest, drink warm fluids, use honey and lemon, gargle with salt water', 
 'If cough lasts more than 2 weeks, if you have fever >101°F, or if coughing up blood', 
 'Difficulty breathing, chest pain, blue lips, high fever'),

('respiratory', 'fever', 'Flu', 'medium',
 'Rest, stay hydrated, take acetaminophen for fever, monitor temperature',
 'If fever >103°F, lasts more than 3 days, or if you have severe headache or stiff neck',
 'Confusion, difficulty breathing, chest pain, seizures, rash'),

('digestive', 'nausea', 'Indigestion', 'low',
 'Eat small meals, avoid spicy foods, drink ginger tea, rest after meals',
 'If vomiting persists >24 hours, if you see blood in vomit, or if you have severe abdominal pain',
 'Severe abdominal pain, bloody vomit, high fever, signs of dehydration'),

('pain', 'headache', 'Tension Headache', 'low',
 'Rest in quiet room, apply cold compress, gentle neck massage, stay hydrated',
 'If headache is severe, sudden, or accompanied by fever, stiff neck, or vision changes',
 'Worst headache of your life, confusion, weakness, difficulty speaking, vision loss'),

('cardiovascular', 'chest_pain', 'Muscle Strain', 'medium',
 'Apply heat/cold packs, gentle stretching, rest, avoid heavy lifting',
 'If pain is severe, persistent, or accompanied by shortness of breath or sweating',
 'Crushing chest pain, pain radiating to arm/jaw, shortness of breath, sweating, dizziness'),

('neurological', 'dizziness', 'Dehydration', 'low',
 'Drink water slowly, sit or lie down, avoid sudden movements, eat salty snacks',
 'If dizziness is severe, persistent, or accompanied by chest pain or confusion',
 'Fainting, chest pain, severe headache, confusion, difficulty speaking'),

('general', 'fatigue', 'Overexertion', 'low',
 'Get adequate sleep, maintain regular schedule, eat balanced meals, exercise moderately',
 'If fatigue is severe, lasts >2 weeks, or is accompanied by weight loss or fever',
 'Extreme fatigue preventing daily activities, confusion, fainting, chest pain'),

('skin', 'rash', 'Allergic Reaction', 'medium',
 'Apply cool compress, avoid scratching, take antihistamine, identify and avoid trigger',
 'If rash spreads rapidly, if you have difficulty breathing, or if rash is painful',
 'Difficulty breathing, swelling of face/tongue, hives covering large area, fever');
