# diag_imports.py — run from repo root with your venv active
import importlib, traceback, sys, os

modules = [
    ('routes_register', 'bp_register'),
    ('routes_login', 'bp_login'),
    ('routes_profile', 'bp_profile'),
    ('routes_add_slot', 'bp_add_slot'),
    ('routes_view_slots', 'bp_view_slots'),
    ('routes_book_slot', 'bp_book'),
]

print("Working dir:", os.getcwd())
print("Python sys.path (first entries):", sys.path[:3])
print("---\n")

for module_name, blueprint_name in modules:
    print(f"Checking module: {module_name} (expect blueprint var: {blueprint_name})")
    try:
        mod = importlib.import_module(module_name)
        print("  -> module imported OK")
        if hasattr(mod, blueprint_name):
            print(f"  -> found blueprint variable '{blueprint_name}'")
        else:
            print(f"  !! warning: blueprint var '{blueprint_name}' NOT found in {module_name}.py")
            print("  list of attrs:", [a for a in dir(mod) if not a.startswith('_')])
    except ModuleNotFoundError as e:
        print("  !! ModuleNotFoundError:", e)
    except Exception as e:
        print("  !! Exception importing module:")
        traceback.print_exc()
    print("---")
