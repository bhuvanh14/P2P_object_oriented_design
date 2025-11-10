# models.py
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime

db = SQLAlchemy()

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    email = db.Column(db.String(100), unique=True, nullable=False)
    password = db.Column(db.String(100), nullable=False)
    role = db.Column(db.String(20), nullable=True)  # 'student' or 'tutor'
    contact = db.Column(db.String(120), nullable=True)
    subject = db.Column(db.String(200), nullable=True)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)

class Slot(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    tutor_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    student_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=True)
    date = db.Column(db.String(20), nullable=False)  # YYYY-MM-DD
    start_time = db.Column(db.String(10), nullable=False)  # HH:MM
    end_time = db.Column(db.String(10), nullable=False)  # HH:MM
    subject = db.Column(db.String(100), nullable=True)
    is_booked = db.Column(db.Boolean, default=False)
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    tutor = db.relationship('User', foreign_keys=[tutor_id], backref='slots_as_tutor')
    student = db.relationship('User', foreign_keys=[student_id], backref='slots_as_student')

class StudentRating(db.Model):
    """Stores tutor ratings of student performance after a session"""
    id = db.Column(db.Integer, primary_key=True)
    session_id = db.Column(db.Integer, db.ForeignKey('slot.id'), nullable=False)
    tutor_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    student_id = db.Column(db.Integer, db.ForeignKey('user.id'), nullable=False)
    rating = db.Column(db.Integer, nullable=False)  # 1-5
    created_at = db.Column(db.DateTime, default=datetime.utcnow)
    
    # Relationships
    session = db.relationship('Slot', backref='ratings')
    tutor = db.relationship('User', foreign_keys=[tutor_id], backref='ratings_given')
    student = db.relationship('User', foreign_keys=[student_id], backref='ratings_received')
