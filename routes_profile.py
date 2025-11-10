from flask import Blueprint, render_template, session, redirect, url_for
from models import db, User  # keep imports minimal; add other models if available

profile_bp = Blueprint('profile_bp', _name_)

@profile_bp.route('/dashboard')
def dashboard():
    user_id = session.get('user_id')
    if not user_id:
        return redirect(url_for('login_bp.login'))

    user = User.query.get(user_id)
    # Safe fallbacks in case relationships/fields differ
    tutoring_sessions = getattr(user, 'sessions', None) or []
    tutors = getattr(user, 'tutors', None) or []
    subjects = getattr(user, 'subjects', None) or []
    requests_list = getattr(user, 'requests', None) or []

    return render_template('dashboard.html',
                           name=session.get('user_name', getattr(user, 'name', 'User')),
                           tutoring_sessions=tutoring_sessions,
                           tutors=tutors,
                           subjects=subjects,
                           requests_list=requests_list)