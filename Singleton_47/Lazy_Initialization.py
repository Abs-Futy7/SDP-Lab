import pymongo
import threading
import time
import os
from dotenv import load_dotenv

load_dotenv()

class LazySingletonMongoDB:
    _instance = None  # Starts as None — nothing created yet

    def __init__(self):
        # No guard here; each thread that slips through creates its OWN client
        self._client = pymongo.MongoClient(os.getenv("MONGO_URI"))

    @classmethod
    def get_instance(cls):
        if cls._instance is None:          # Check — NOT protected by a lock!
            # Artificial delay to widen the race window so multiple threads
            # can all pass the None-check before any of them assigns _instance.
            time.sleep(0.05)
            cls._instance = cls()
        return cls._instance               # May return DIFFERENT objects!

    def get_database(self, db_name="testdb"):
        return self._client[db_name]

    def get_collection(self, db_name, col_name):
        return self._client[db_name][col_name]

    def close_connection(self):
        self._client.close()


# ── Experiment 1: Instance Validation ──────────────────────────
def test_instance_validation(SingletonClass, name, use_enum=False):
    # Reset singleton state for a fresh test
    SingletonClass._instance = None

    print(f"\n{'='*50}")
    print(f"Instance Validation: {name}")
    if use_enum:
        instances = [SingletonClass.INSTANCE for _ in range(5)]
    else:
        instances = [SingletonClass.get_instance() for _ in range(5)]

    ids = [id(i) for i in instances]
    print(f"IDs: {ids}")
    print(f"All same? {len(set(ids)) == 1}")  # True = singleton working


# ── Experiment 2: Thread Safety ─────────────────────────────────
def test_thread_safety(SingletonClass, name, use_enum=False):
    # Reset to force fresh creation inside threads
    SingletonClass._instance = None

    print(f"\n{'='*50}")
    print(f"Thread Safety: {name}")
    results = []
    lock = threading.Lock()  # only to safely append to results list

    def get_inst():
        inst = SingletonClass.get_instance()
        with lock:
            results.append(id(inst))

    threads = [threading.Thread(target=get_inst) for _ in range(10)]
    for t in threads: t.start()
    for t in threads: t.join()

    unique = set(results)
    print(f"Unique IDs collected: {unique}")
    print(f"Number of unique instances: {len(unique)}")
    print(f"Thread-safe? {len(unique) == 1}")


# ── Experiment 3: Performance ────────────────────────────────────
def test_performance(SingletonClass, name, use_enum=False):
    # Reset so init time is measured fresh
    SingletonClass._instance = None

    print(f"\n{'='*50}")
    print(f"Performance: {name}")

    # Init time
    start = time.perf_counter()
    _ = SingletonClass.get_instance()
    init_time = time.perf_counter() - start
    print(f"Init time: {init_time:.6f}s")

    # Access time (1000 repeated calls — instance already exists, no sleep)
    start = time.perf_counter()
    for _ in range(1000):
        _ = SingletonClass.get_instance()
    access_time = time.perf_counter() - start
    print(f"1000x access time: {access_time:.6f}s")


# Run all experiments
test_instance_validation(LazySingletonMongoDB, "Lazy")

test_thread_safety(LazySingletonMongoDB, "Lazy")       # WILL show multiple instances!

test_performance(LazySingletonMongoDB, "Lazy")