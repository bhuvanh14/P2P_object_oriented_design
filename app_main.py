# app_main.py (in repo root)
from flask import Flask
import os
import sys

# Add src/backend to Python path so imports work
sys.path.insert(0, os.path.join(os.path.dirname(__file__), 'src', 'backend'))

# Now import db from models.py (which is in repo root based on your structure)
from models import db

def create_app():
    # templates folder is in src/templates
    template_folder = os.path.join(os.path.dirname(__file__), 'src', 'templates')
    
    app = Flask(__name__, template_folder=template_folder)
    app.secret_key = os.environ.get('FLASK_SECRET', 'supersecretkey')
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

    db.init_app(app)

    # Import and register blueprints from src/backend/
    try:
        from routes_register import bp_register
        app.register_blueprint(bp_register)
        print("✅ routes_register loaded successfully")
    except Exception as e:
        print("❌ routes_register not loaded:", e)

    try:
        from routes_login import bp_login
        app.register_blueprint(bp_login)
        print("✅ routes_login loaded successfully")
    except Exception as e:
        print("❌ routes_login not loaded:", e)

    # routes_profile - blueprint name: profile_bp
    try:
        from routes_profile import profile_bp
        app.register_blueprint(profile_bp)
        print("✅ routes_profile loaded successfully (profile_bp)")
    except Exception as e:
        print("❌ routes_profile not loaded:", e)

    try:
        from routes_add_slot import bp_add_slot
        app.register_blueprint(bp_add_slot)
        print("✅ routes_add_slot loaded successfully")
    except Exception as e:
        print("❌ routes_add_slot not loaded:", e)

    try:
        from routes_view_slots import bp_view_slots
        app.register_blueprint(bp_view_slots)
        print("✅ routes_view_slots loaded successfully")
    except Exception as e:
        print("❌ routes_view_slots not loaded:", e)

    # routes_book_slot - blueprint name: bp_book
    try:
        from routes_book_slot import bp_book
        app.register_blueprint(bp_book)
        print("✅ routes_book_slot loaded successfully (bp_book)")
    except Exception as e:
        print("❌ routes_book_slot not loaded:", e)

    # routes_feedback - commented out (file doesn't exist)
    # try:
    #     from routes_feedback import bp_feedback
    #     app.register_blueprint(bp_feedback)
    #     print("✅ routes_feedback loaded successfully")
    # except Exception as e:
    #     print("⚠️ routes_feedback not available:", e)

    return app

if __name__ == '__main__':
    app = create_app()
    with app.app_context():
        db.create_all()
    app.run(debug=True)