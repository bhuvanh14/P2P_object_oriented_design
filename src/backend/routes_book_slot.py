# routes_book_slot.py  (SCRUM-12)  owner: BH (Bhuvan Hebbar)
from flask import Blueprint, render_template, request, redirect, url_for, session, abort
from models import db, Slot, User
from sqlalchemy.exc import SQLAlchemyError

# Blueprint variable name expected by app_main: bp_book
bp_book = Blueprint('book_bp', __name__)

@bp_book.route('/book/<int:slot_id>', methods=['GET', 'POST'])
def book_slot(slot_id):
    slot = Slot.query.get(slot_id)
    if not slot:
        return "Slot not found", 404

    # Ensure logged in
    if 'user_id' not in session:
        return redirect(url_for('login_bp.login'))

    current_user_id = session.get('user_id')
    # determine tutor id from slot (try common field/relationship names)
    tutor_id = getattr(slot, 'tutor_id', None)
    if tutor_id is None:
        tutor = getattr(slot, 'tutor', None)
        tutor_id = getattr(tutor, 'id', None) if tutor else None

    # normalize types and add debug log for troubleshooting (optional)
    try:
        current_user_id = int(current_user_id)
    except Exception:
        pass
    try:
        tutor_id = int(tutor_id) if tutor_id is not None else None
    except Exception:
        pass

    # Prevent tutor from booking their own slot
    if tutor_id is not None and current_user_id == tutor_id:
        # return a user-friendly message or redirect back to dashboard
        return render_template('book_error.html', message="You cannot book your own slot."), 400

    if request.method == 'POST':
        # booking logic here: mark slot as booked, set student_id/booked_by, commit
        try:
            # SQLite does not honor row locking; this is a simple re-check
            slot = Slot.query.get(slot_id)
            if getattr(slot, 'is_booked', False):
                return "Slot already booked", 400

            slot.is_booked = True
            slot.student_id = current_user_id

            # Try to determine tutor user and link tutor <-> student relationship so
            # the student's "My Tutors" list updates after booking.
            tutor_user = None
            tutor_id = getattr(slot, 'tutor_id', None)
            if tutor_id is None:
                tutor = getattr(slot, 'tutor', None)
                tutor_id = getattr(tutor, 'id', None) if tutor else None

            if tutor_id:
                tutor_user = User.query.get(tutor_id)

            student_user = User.query.get(current_user_id)

            # Add tutor to student's tutors collection if available
            if tutor_user and student_user:
                # common relationship name: student.tutors
                if hasattr(student_user, 'tutors'):
                    if tutor_user not in student_user.tutors:
                        student_user.tutors.append(tutor_user)
                # alternative name: my_tutors
                elif hasattr(student_user, 'my_tutors'):
                    if tutor_user not in student_user.my_tutors:
                        student_user.my_tutors.append(tutor_user)

                # add reciprocal relation if model exposes it (optional)
                if hasattr(tutor_user, 'students'):
                    if student_user not in tutor_user.students:
                        tutor_user.students.append(student_user)
                elif hasattr(tutor_user, 'pupils'):
                    if student_user not in tutor_user.pupils:
                        tutor_user.pupils.append(student_user)

            # persist changes
            db.session.add(slot)
            if student_user:
                db.session.add(student_user)
            if tutor_user:
                db.session.add(tutor_user)
            db.session.commit()
            # After successful booking redirect to dashboard so "My Tutors" is rebuilt from DB
            return redirect(url_for('profile_bp.dashboard'))
        except SQLAlchemyError as e:
            db.session.rollback()
            return render_template('book_error.html', message="Database error during booking."), 500

    # GET → show confirmation page
    return render_template('book_confirm.html', slot=slot)
