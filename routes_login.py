# routes_login.py  (SCRUM-8)  owner: BD (Bikram Dutta)
from flask import Blueprint, render_template, request, redirect, url_for, session
from models import db, User

bp_login = Blueprint('login_bp', __name__)

@bp_login.route('/login', methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        email = request.form.get('email')
        password = request.form.get('password')

        user = User.query.filter_by(email=email, password=password).first()
        if user:
            session['user_id'] = user.id
            session['user_name'] = user.name
            # redirect to dashboard handled by profile module
            return redirect(url_for('profile_bp.dashboard'))
        else:
            return "Invalid email or password!", 401

    return render_template('login.html')
