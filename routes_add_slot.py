# ...existing code...
from flask import Blueprint, render_template, request, redirect, url_for, session
from models import db, Slot, User
from sqlalchemy import and_

bp_add_slot = Blueprint('add_slot_bp', __name__)

@bp_add_slot.route('/become_tutor', methods=['GET', 'POST'])
def become_tutor():
    # ensure logged in
    if 'user_id' not in session:
        return redirect(url_for('login_bp.login'))

    user = User.query.get(session['user_id'])
    if request.method == 'POST':
        subjects = request.form.get('subjects', '').strip()
        contact = request.form.get('contact', '').strip()
        bio = request.form.get('bio', '').strip()

        # store on user if fields exist
        if hasattr(user, 'subject'):
            user.subject = subjects
        if hasattr(user, 'contact'):
            user.contact = contact
        if hasattr(user, 'bio'):
            user.bio = bio
        # try to mark role/is_tutor if model supports it
        try:
            if hasattr(user, 'role'):
                user.role = 'tutor'
            if hasattr(user, 'is_tutor'):
                user.is_tutor = True
        except Exception:
            pass

        db.session.add(user)
        db.session.commit()
        return render_template('become_tutor.html', user=user, message="You are now registered as a tutor.")

    return render_template('become_tutor.html', user=user)


@bp_add_slot.route('/add_slot', methods=['GET', 'POST'])
def add_slot():
    # ensure logged in
    if 'user_id' not in session:
        return redirect(url_for('login_bp.login'))

    tutor_id = session['user_id']
    user = User.query.get(tutor_id)
    # If user is not a tutor, send them to become_tutor page
    is_tutor = getattr(user, 'is_tutor', False) or getattr(user, 'role', '') == 'tutor' or bool(getattr(user, 'subject', None))
    if not is_tutor:
        return redirect(url_for('add_slot_bp.become_tutor'))

    # Optional: check role if you stored it: if current_user.role != 'tutor': deny

    if request.method == 'POST':
        date = request.form.get('date')           # expected format YYYY-MM-DD
        start_time = request.form.get('start_time') # HH:MM
        end_time = request.form.get('end_time')     # HH:MM
        subject = request.form.get('subject')

        # basic validation
        if not date or not start_time or not end_time:
            return "Date, start and end times are required", 400

        # prevent overlapping slots for same tutor (simple check)
        overlap = Slot.query.filter(
            Slot.tutor_id == tutor_id,
            Slot.date == date,
            # naive overlap check: start_time < existing_end AND end_time > existing_start
            (Slot.start_time < end_time) & (Slot.end_time > start_time)
        ).first()

        if overlap:
            return "Overlapping slot exists for this tutor on this date/time", 400

        s = Slot(
            tutor_id=tutor_id,
            date=date,
            start_time=start_time,
            end_time=end_time,
            subject=subject or ""
        )
        db.session.add(s)
        db.session.commit()
        # After creating a slot, redirect to dashboard so user sees updated list
        return redirect(url_for('profile_bp.dashboard'))

    return render_template('add_slot.html')
# ...existing code...