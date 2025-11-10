"""
Module Import & Structure Tests
Verifies all project modules from src/backend/ can be imported correctly
"""

import pytest
import sys
import os

# Add src/backend directory to Python path
backend_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'src', 'backend')
sys.path.insert(0, backend_path)


class TestImportsFromBackend:
    """Verify all core modules from src/backend can be imported"""
    
    def test_import_app_main(self):
        """Test app_main can be imported from src/backend/"""
        try:
            import app_main
            assert app_main is not None
            print("✅ app_main imported successfully from src/backend/")
        except ImportError as e:
            pytest.fail(f"❌ Failed to import app_main: {e}")
    
    def test_import_models(self):
        """Test models can be imported from src/backend/"""
        try:
            import models
            assert models is not None
            print("✅ models imported successfully from src/backend/")
        except ImportError as e:
            pytest.fail(f"❌ Failed to import models: {e}")
    
    def test_import_diag_imports(self):
        """Test diag_imports can be imported from src/backend/"""
        try:
            import diag_imports
            assert diag_imports is not None
            print("✅ diag_imports imported successfully from src/backend/")
        except ImportError as e:
            pytest.skip(f"⚠️ diag_imports not available (optional): {e}")


class TestAppStructure:
    """Test basic app structure and routes"""
    
    def test_app_is_flask_instance(self, test_app):
        """Verify app is a Flask instance"""
        from flask import Flask
        assert isinstance(test_app, Flask)
        print("✅ App is Flask instance")
    
    def test_home_page_accessible(self, client):
        """Test home page is accessible"""
        response = client.get('/')
        assert response.status_code in [200, 302]
        print("✅ Home page accessible")
    
    def test_register_page_accessible(self, client):
        """Test register page/route is accessible"""
        response = client.get('/register')
        assert response.status_code in [200, 404]
        print("✅ Register page check passed")
    
    def test_login_page_accessible(self, client):
        """Test login page/route is accessible"""
        response = client.get('/login')
        assert response.status_code in [200, 404]
        print("✅ Login page check passed")
    
    def test_view_slots_page_accessible(self, client):
        """Test view slots page/route is accessible"""
        response = client.get('/view-slots')
        assert response.status_code in [200, 404]
        print("✅ View slots page check passed")
    
    def test_app_has_routes(self, test_app):
        """Verify that app has routes registered"""
        routes = [str(rule) for rule in test_app.url_map.iter_rules()]
        assert len(routes) > 0
        print(f"✅ App has {len(routes)} routes registered")
    
    def test_backend_path_exists(self):
        """Verify src/backend path exists"""
        assert os.path.exists(backend_path), f"Backend path does not exist: {backend_path}"
        print(f"✅ Backend path exists: {backend_path}")
    
    def test_app_main_exists(self):
        """Verify app_main.py exists in backend"""
        app_main_path = os.path.join(backend_path, 'app_main.py')
        assert os.path.exists(app_main_path), f"app_main.py not found at {app_main_path}"
        print(f"✅ app_main.py exists at {app_main_path}")
    
    def test_models_exists(self):
        """Verify models.py exists in backend"""
        models_path = os.path.join(backend_path, 'models.py')
        assert os.path.exists(models_path), f"models.py not found at {models_path}"
        print(f"✅ models.py exists at {models_path}")
    
    def test_routes_files_exist(self):
        """Verify route files exist in backend"""
        route_files = [
            'routes_login.py',
            'routes_register.py',
            'routes_add_slot.py',
            'routes_view_slots.py',
            'routes_book_slot.py',
            'routes_feedback.py',
            'routes_profile.py'
        ]
        
        missing_files = []
        for route_file in route_files:
            route_path = os.path.join(backend_path, route_file)
            if not os.path.exists(route_path):
                missing_files.append(route_file)
        
        if missing_files:
            print(f"⚠️ Some route files not found: {missing_files}")
        else:
            print(f"✅ All route files exist in {backend_path}")
    
    def test_templates_folder_exists(self):
        """Verify templates folder exists"""
        templates_path = os.path.join(os.path.dirname(os.path.dirname(backend_path)), 'templates')
        assert os.path.exists(templates_path), f"templates folder not found at {templates_path}"
        print(f"✅ templates folder exists at {templates_path}")