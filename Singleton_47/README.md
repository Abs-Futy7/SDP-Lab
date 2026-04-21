# Singleton_47 - Singleton Pattern with MongoDB Connection Manager (Python)

This project demonstrates six Singleton Pattern implementations in Python and compares their behavior using:

1. Instance validation (single object check)
2. Multithreading/thread-safety analysis
3. Performance measurement

It is designed for lab assignment and viva discussion.

---

## 1) Project Objective

The goal is to ensure that only one manager object is used for database access across the application.

Why this matters:
- Prevents unnecessary multiple database manager objects
- Avoids conflicting shared state
- Reduces overhead in applications where one shared manager is enough

Each implementation includes a MongoDB connection manager API:
- `get_database()`
- `get_collection(db_name, col_name)`
- `close_connection()`

---

## 2) What is an Instance?

An instance is an object created from a class.

Example idea:
- Class = blueprint
- Instance = real object built from that blueprint

In Python, `id(obj)` gives the memory identity of an object during program runtime.
If two variables have the same `id`, they refer to the same instance.

In this project, singleton correctness is checked using repeated `id(...)` values.

---

## 3) What is Thread Safety?

Thread safety means behavior remains correct when multiple threads run at the same time.

Why important for Singleton:
- If two threads check "instance is None" at the same moment, both might create separate objects.
- That breaks Singleton.

A thread-safe singleton ensures only one instance is created even under concurrency.

---

## 4) Folder and Files

- `Eager_Initialization.py`
- `Lazy_Initialization.py`
- `Synchronized_method.py`
- `Double_Checked_Locking.py`
- `Bill_Pugh_Implementation.py`
- `Enum_Based_Singleton.py`
- `run_all.ps1` - runs all scripts in sequence
- `requirements.txt` - dependencies
- `Report.tex` - LaTeX report

---

## 5) Implementation Details (How each code works)

## A) Eager Initialization

Idea:
- Instance is created at class load time (before first explicit request).

How:
- `_instance` is created once and `get_instance()` always returns it.

Pros:
- Simple
- Thread-safe by construction in this usage

Cons:
- Instance is created even if never used

Viva line:
"Eager creates early, so there is no race during first access."

---

## B) Lazy Initialization (basic, non-thread-safe)

Idea:
- Create instance only when first requested.

How:
- `if _instance is None: _instance = cls()`

In this project:
- A tiny sleep is used to widen race window and demonstrate failure under threads.

Pros:
- Delayed creation

Cons:
- Not thread-safe

Viva line:
"Two threads can pass the None-check together and both create objects."

---

## C) Synchronized Method

Idea:
- Lock the entire instance creation method.

How:
- `with lock:` around check and creation.

Pros:
- Thread-safe

Cons:
- Lock overhead on every call, slower repeated access

Viva line:
"Safe but conservative because every access pays synchronization cost."

---

## D) Double-Checked Locking

Idea:
- Check without lock first, lock only when needed.

How:
1. First check: if instance exists, return directly
2. If missing, acquire lock
3. Check again inside lock, then create

Pros:
- Thread-safe
- Better performance than full synchronization in repeated access

Cons:
- More complex than synchronized method

Viva line:
"Two checks avoid unnecessary lock once instance is already initialized."

---

## E) Bill Pugh Implementation (Inner Holder style)

Idea:
- Use nested holder class to store singleton instance, exposed through `get_instance()`.

How in this project:
- Nested holder contains `INSTANCE`
- `get_instance()` initializes holder instance once and returns it
- Access remains clean and singleton-safe under implemented lock logic

Pros:
- Clean separation of holder and access
- Lazy semantics through holder-based structure

Cons:
- Slightly harder to explain than basic lazy/eager

Viva line:
"Inner holder keeps singleton reference isolated and accessed through one method."

---

## F) Enum-based Singleton

Idea:
- Enum member is created once and reused.

How:
- `INSTANCE` enum member wraps MongoDB connection object.

Pros:
- Very clean usage
- Naturally singleton-like behavior in code

Cons:
- Less flexible than class-based variants for some inheritance-style designs

Viva line:
"Enum member identity is fixed, so all calls use the same singleton member."

---

## 6) Experiment Design

All scripts run three experiments:

1. Instance Validation
- Create/access singleton multiple times
- Print list of IDs
- If all IDs equal -> singleton works in sequential context

2. Thread Safety
- Use 10 threads
- Each thread accesses singleton
- Collect IDs
- If set size is 1 -> thread-safe

3. Performance
- Init time: first creation/access
- Access time: 1000 repeated calls

Note:
- Microsecond differences can fluctuate between runs due to OS scheduling and runtime noise.
- Stable conclusion should come from trend across multiple runs, not one run only.

---

## 7) How to Run

From project folder:

```powershell
.\run_all.ps1
```

The script:
1. Checks/creates virtual environment
2. Installs dependencies
3. Runs all six implementations one by one

---

## 8) How to Explain Your Output in Viva

If `All same? True` in Instance Validation:
- Sequential singleton behavior is correct.

If `Unique IDs collected` has more than one ID in thread test:
- Not thread-safe.
- In this project, basic Lazy usually shows multiple IDs.

If `Unique IDs collected` has exactly one ID:
- Thread-safe in practical test.

If one implementation appears faster/slower:
- Mention that repeated-access timing is affected by lock overhead and runtime noise.
- State stable pattern-level conclusions, not only one-run ranking.

---

## 9) Typical Viva Questions and Short Answers

Q1. Why use Singleton for DB manager?
- To keep one shared manager and avoid redundant manager objects.

Q2. How do you verify singleton?
- Compare object IDs across multiple accesses.

Q3. Why does lazy fail in threads?
- Race condition at first creation without lock.

Q4. Why is synchronized method slower?
- It acquires lock on every call.

Q5. Why use double-checked locking?
- To reduce lock usage after initialization while keeping safety.

Q6. What does thread-safe mean here?
- Exactly one instance created even with concurrent threads.

Q7. Is performance ranking always same?
- No, close timings can reorder; stable trends matter more.

Q8. Which result is most important for correctness?
- Thread-safety and single-instance guarantee.

---

## 10) Key Final Takeaways

- All six implementations usually pass sequential singleton validation.
- Basic Lazy Initialization fails under concurrency in this setup.
- Synchronized method is safe but often slower on repeated access.
- Enum and lock-optimized approaches are often among faster repeated-access results.
- For viva, emphasize correctness first (single instance + thread safety), then performance.
