"""
Pytest Configuration & Fixtures
Peer-to-Peer Tutoring Scheduler Test Suite
Adjusted for src/backend/ structure
"""

import pytest
import sys
import os
from datetime import datetime, timedelta

# Add src/backend directory to Python path
backend_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'src', 'backend')
sys.path.insert(0, backend_path)


@pytest.fixture(scope='session')
def test_app():
    """Create and configure test app from src/backend/app_main.py"""
    try:
        from app_main import app
        
        app.config['TESTING'] = True
        app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///:memory:'
        app.config['WTF_CSRF_ENABLED'] = False
        app.config['SECRET_KEY'] = 'test-secret-key'
        
        with app.app_context():
            try:
                from app_main import db
                db.create_all()
            except Exception as db_error:
                print(f"⚠️ Database initialization skipped: {db_error}")
            
            yield app
            
            try:
                from app_main import db
                db.session.remove()
                db.drop_all()
            except:
                pass
                
    except Exception as e:
        print(f"❌ Error initializing test app: {e}")
        print(f"Backend path: {backend_path}")
        pytest.skip(f"Could not initialize app from {backend_path}: {e}")


@pytest.fixture
def client(test_app):
    """Test client for making HTTP requests"""
    return test_app.test_client()


@pytest.fixture
def runner(test_app):
    """CLI runner for app commands"""
    return test_app.test_cli_runner()


@pytest.fixture
def test_user_data():
    """Sample test data for users - TC-Auth tests"""
    return {
        'learner': {
            'email': 'learner@example.com',
            'username': 'learner_user',
            'password': 'SecureP@ss123',
            'role': 'student'
        },
        'tutor': {
            'email': 'tutor@example.com',
            'username': 'tutor_user',
            'password': 'SecureP@ss123',
            'role': 'tutor'
        },
        'duplicate': {
            'email': 'duplicate@example.com',
            'password': 'SecureP@ss456'
        }
    }


@pytest.fixture
def slot_data():
    """Sample slot data for testing - TC-Prof tests"""
    tomorrow = (datetime.now() + timedelta(days=1)).strftime('%Y-%m-%d')
    return {
        'subject': 'Mathematics',
        'date': tomorrow,
        'time': '10:00',
        'duration': '1',
        'rate': 25.0
    }