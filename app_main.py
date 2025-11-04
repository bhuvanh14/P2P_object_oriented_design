# app_main.py
from flask import Flask
from models import db
import os

def create_app():
    app = Flask(__name__, static_folder='static', template_folder='templates')
    app.secret_key = os.environ.get('FLASK_SECRET', 'supersecretkey')
    app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///database.db'
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

    db.init_app(app)

    # Import and register blueprints only if file exists
    try:
        from routes_register import bp_register
        app.register_blueprint(bp_register)
    except Exception as e:
        print("routes_register not loaded:", e)

    try:
        from routes_login import bp_login
        app.register_blueprint(bp_login)
    except Exception as e:
        print("routes_login not loaded:", e)

    try:
        from routes_profile import bp_profile
        app.register_blueprint(bp_profile)
    except Exception as e:
        print("routes_profile not loaded:", e)

    return app

if __name__ == '__main__':
    app = create_app()
    with app.app_context():
        db.create_all()
    app.run(debug=True)
