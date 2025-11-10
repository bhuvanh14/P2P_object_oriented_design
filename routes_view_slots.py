# routes_view_slots.py  -- SCRUM-11 (BD)
from flask import Blueprint, render_template
from models import db, Slot, User
from datetime import datetime

bp_view_slots = Blueprint('view_slots_bp', __name__)

@bp_view_slots.route('/view_slots')
def view_slots():
    now = datetime.now()
    available = []

    # Safe query: iterate and filter in Python to avoid depending on exact model field names
    for s in Slot.query.all():
        # skip if already booked (common field names)
        if getattr(s, 'student_id', None) or getattr(s, 'booked_by', None) or getattr(s, 'is_booked', False):
            continue

        # try to determine slot datetime (skip if in the past)
        slot_dt = None
        s_date = getattr(s, 'date', None)
        s_start = getattr(s, 'start_time', None) or getattr(s, 'start', None)
        try:
            if s_date and s_start:
                # expected date 'YYYY-MM-DD' and time 'HH:MM' (adjust if your model stores differently)
                slot_dt = datetime.strptime(f"{s_date} {s_start}", "%Y-%m-%d %H:%M")
        except Exception:
            slot_dt = None

        if slot_dt and slot_dt < now:
            continue

        # tutor name: try relationship then tutor_id lookup
        tutor_name = None
        tutor = getattr(s, 'tutor', None)
        if tutor and getattr(tutor, 'name', None):
            tutor_name = tutor.name
        else:
            tutor_id = getattr(s, 'tutor_id', None)
            if tutor_id:
                t = User.query.get(tutor_id)
                tutor_name = getattr(t, 'name', None) if t else None
        tutor_name = tutor_name or "Unknown"

        available.append({
            'id': s.id,
            'tutor_name': tutor_name,
            'subject': getattr(s, 'subject', getattr(s, 'subject_name', '')),
            'date': s_date or getattr(s, 'date_time', '') or '',
            'start_time': s_start or '',
            'end_time': getattr(s, 'end_time', '') or getattr(s, 'end', '') or '',
        })

    return render_template('view_slots.html', slots=available)
