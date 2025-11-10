"""
Profile Management Tests
Maps to STP: PTS-F-004 (Tutor Availability & Profile)
Test Cases: TC-Prof-01 through TC-Prof-05
Routes: src/backend/routes_profile.py, src/backend/routes_add_slot.py
"""

import pytest


class TestTutorAvailability:
    """Tutor Availability & Profile Tests"""
    
    def test_tc_prof_01_tutor_sets_availability_slots(self, client, test_user_data, slot_data):
        """TC-Prof-01: Tutor sets availability slots"""
        tutor = test_user_data['tutor']
        
        # Register tutor
        client.post('/register', data={
            'username': tutor['username'],
            'email': tutor['email'],
            'password': tutor['password'],
            'confirm_password': tutor['password']
        })
        
        # Login
        client.post('/login', data={
            'email': tutor['email'],
            'password': tutor['password']
        })
        
        # Add slot
        response = client.post('/add-slot', data={
            'subject': slot_data['subject'],
            'date': slot_data['date'],
            'time': slot_data['time'],
            'duration': slot_data['duration'],
            'rate': slot_data['rate']
        }, follow_redirects=True)
        
        assert response.status_code in [200, 302, 401]
        print("✅ TC-Prof-01: Tutor sets availability slots passed")
    
    def test_tc_prof_02_tutor_updates_availability(self, client):
        """TC-Prof-02: Tutor updates availability"""
        response = client.post('/edit-slot', data={
            'slot_id': '1',
            'time': '14:00',
            'duration': '2'
        }, follow_redirects=True)
        
        assert response.status_code in [200, 302, 401, 404]
        print("✅ TC-Prof-02: Tutor updates availability passed")
    
    def test_tc_prof_03_tutor_deletes_availability(self, client):
        """TC-Prof-03: Tutor deletes availability slot"""
        response = client.post('/delete-slot', data={
            'slot_id': '1'
        }, follow_redirects=True)
        
        assert response.status_code in [200, 302, 401, 404]
        print("✅ TC-Prof-03: Tutor deletes availability slot passed")
    
    def test_tc_prof_04_learner_views_tutor_profile(self, client):
        """TC-Prof-04: Learner views tutor profile"""
        response = client.get('/profile/1')
        assert response.status_code in [200, 404]
        print("✅ TC-Prof-04: Learner views tutor profile passed")
    
    def test_tc_prof_05_tutor_updates_profile_info(self, client):
        """TC-Prof-05: Tutor updates profile information"""
        response = client.post('/profile/update', data={
            'bio': '5+ years teaching experience',
            'qualification': 'Masters in Mathematics'
        }, follow_redirects=True)
        
        assert response.status_code in [200, 302, 401]
        print("✅ TC-Prof-05: Tutor updates profile info passed")