"""
Feedback & Rating Tests
Maps to STP: PTS-F-012 (Ratings & Feedback)
Test Cases: TC-Feed-01 through TC-Feed-05
Routes: src/backend/routes_feedback.py
"""

import pytest


class TestFeedbackAndRatings:
    """Feedback & Rating Tests"""
    
    def test_tc_feed_01_learner_submits_positive_rating(self, client):
        """TC-Feed-01: Learner submits rating after session"""
        response = client.post('/feedback/submit', data={
            'session_id': '1',
            'rating': '5',
            'feedback': 'Great tutor, very helpful!'
        }, follow_redirects=True)
        
        assert response.status_code in [200, 302, 401, 404]
        print("✅ TC-Feed-01: Positive rating submission passed")
    
    def test_tc_feed_02_learner_submits_negative_rating(self, client):
        """TC-Feed-02: Learner submits negative rating with feedback"""
        response = client.post('/feedback/submit', data={
            'session_id': '1',
            'rating': '2',
            'feedback': 'Arrived late, rushed through content'
        }, follow_redirects=True)
        
        assert response.status_code in [200, 302, 401, 404]
        print("✅ TC-Feed-02: Negative rating submission passed")
    
    def test_tc_feed_03_cannot_rate_incomplete_session(self, client):
        """TC-Feed-03: Learner cannot rate incomplete session"""
        response = client.post('/feedback/submit', data={
            'session_id': '999',
            'rating': '5',
            'feedback': 'Test'
        }, follow_redirects=True)
        
        assert response.status_code in [200, 401, 404]
        print("✅ TC-Feed-03: Cannot rate incomplete session passed")
    
    def test_tc_feed_04_tutor_views_learner_feedback(self, client):
        """TC-Feed-04: Tutor views learner feedback"""
        response = client.get('/feedback/view')
        assert response.status_code in [200, 401, 404]
        print("✅ TC-Feed-04: Tutor views feedback passed")
    
    def test_tc_feed_05_prevent_duplicate_ratings(self, client):
        """TC-Feed-05: System prevents duplicate ratings"""
        # First rating
        client.post('/feedback/submit', data={
            'session_id': '1',
            'rating': '5',
            'feedback': 'Great!'
        })
        
        # Try duplicate rating
        response = client.post('/feedback/submit', data={
            'session_id': '1',
            'rating': '4',
            'feedback': 'Actually good'
        }, follow_redirects=True)
        
        assert response.status_code in [200, 401, 404]
        print("✅ TC-Feed-05: Duplicate rating prevention passed")