import pymongo
import threading
import time
import os
from dotenv import load_dotenv

load_dotenv()

class BillPughSingletonMongoDB:
    _instance = None
    _lock = threading.Lock()

    def __new__(cls):
        # __new__ is called BEFORE __init__ — controls whether a new object is made
        if cls._instance is None:
            with cls._lock:
                if cls._instance is None:
                    cls._instance = super().__new__(cls)  # Actually create object
                    cls._instance._client = pymongo.MongoClient(os.getenv("MONGO_URI"))
        return cls._instance  # Always return the same object

    def get_database(self, db_name="testdb"):
        return self._client[db_name]

    def get_collection(self, db_name, col_name):
        return self._client[db_name][col_name]

    def close_connection(self):
        self._client.close()


# Usage — looks like normal class instantiation!
#m1 = BillPughSingletonMongoDB()
#m2 = BillPughSingletonMongoDB()
#print(id(m1) == id(m2))  # True

# ── Experiment 1: Instance Validation ──────────────────────────
def test_instance_validation(SingletonClass, name, use_enum=False):
    print(f"\n{'='*50}")
    print(f"Instance Validation: {name}")
    if use_enum:
        instances = [SingletonClass.INSTANCE for _ in range(5)]
    else:
        instances = [SingletonClass() for _ in range(5)]

    ids = [id(i) for i in instances]
    print(f"IDs: {ids}")
    print(f"All same? {len(set(ids)) == 1}")  # True = singleton working


# ── Experiment 2: Thread Safety ─────────────────────────────────
def test_thread_safety(SingletonClass, name, use_enum=False):
    print(f"\n{'='*50}")
    print(f"Thread Safety: {name}")
    results = []

    def get_inst():
        if use_enum:
            results.append(id(SingletonClass.INSTANCE))
        else:
            results.append(id(SingletonClass()))

    threads = [threading.Thread(target=get_inst) for _ in range(10)]
    for t in threads: t.start()
    for t in threads: t.join()

    print(f"Unique IDs collected: {set(results)}")
    print(f"Thread-safe? {len(set(results)) == 1}")


# ── Experiment 3: Performance ────────────────────────────────────
def test_performance(SingletonClass, name, use_enum=False):
    print(f"\n{'='*50}")
    print(f"Performance: {name}")

    # Init time
    start = time.perf_counter()
    if use_enum:
        _ = SingletonClass.INSTANCE
    else:
        _ = SingletonClass()
    init_time = time.perf_counter() - start
    print(f"Init time: {init_time:.6f}s")

    # Access time (1000 repeated calls)
    start = time.perf_counter()
    for _ in range(1000):
        if use_enum:
            _ = SingletonClass.INSTANCE
        else:
            _ = SingletonClass()
    access_time = time.perf_counter() - start
    print(f"1000x access time: {access_time:.6f}s")


# Run all experiments
test_instance_validation(BillPughSingletonMongoDB, "Bill Pugh")

test_thread_safety(BillPughSingletonMongoDB, "Bill Pugh")

test_performance(BillPughSingletonMongoDB, "Bill Pugh")
