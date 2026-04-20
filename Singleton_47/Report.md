# Singleton Pattern — Experimental Results Report

**Course:** Software Design Patterns Lab  
**Assignment:** Singleton_47  
**Environment:** Python 3.13 · pymongo 4.7.3 · MongoDB Atlas (SRV)  
**Platform:** Windows 11 · AMD64

---

## 1. Instance Validation Results

Each implementation was called 5 times sequentially. The memory address (Python `id()`) of
each returned object was recorded. If all five IDs are identical the singleton constraint holds.

| Implementation       | Object IDs (5 calls)                                                                 | All Same? |
|----------------------|--------------------------------------------------------------------------------------|-----------|
| Eager Initialization | `1331929550864 × 5`                                                                  | ✅ Yes    |
| Lazy Initialization  | `2213336813920 × 5`                                                                  | ✅ Yes    |
| Synchronized Method  | `1625296015376 × 5`                                                                  | ✅ Yes    |
| Double-Checked Lock  | `2405007495184 × 5`                                                                  | ✅ Yes    |
| Bill Pugh (`__new__`)| `1818754486288 × 5`                                                                  | ✅ Yes    |
| Enum-Based           | `1451381950512 × 5`                                                                  | ✅ Yes    |

**Observation:** Under sequential (single-threaded) access every implementation correctly
returns the same object on every call. The singleton invariant is maintained in all six cases.

---

## 2. Multithreading Results

Ten threads were spawned simultaneously, each calling `get_instance()` (or accessing
`INSTANCE` for the Enum). The `id()` of the returned object was collected from every thread.
Results show how many **distinct** objects were created.

> **Lazy Initialization Note:** The `get_instance()` method contains a deliberate
> `time.sleep(0.05)` between the `None`-check and the assignment to widen the race window and
> expose the inherent thread-safety flaw. No lock protects the critical section.

### Raw Thread Output

**Eager Initialization**
```
Unique IDs collected : {1331929550864}
Number of unique instances : 1
Thread-safe? True
```

**Lazy Initialization**
```
Unique IDs collected : {2213342206848, 2213341617056, 2213341618272, 2213341344496,
                        2213341648272, 2213336748176, 2213341662800, 2213341663824,
                        2213340294160, 2213341647184}
Number of unique instances : 10
Thread-safe? False
```

**Synchronized Method**
```
Unique IDs collected : {1625296015376}
Number of unique instances : 1
Thread-safe? True
```

**Double-Checked Locking**
```
Unique IDs collected : {2405007495184}
Number of unique instances : 1
Thread-safe? True
```

**Bill Pugh (`__new__`)**
```
Unique IDs collected : {1818754486288}
Number of unique instances : 1
Thread-safe? True
```

**Enum-Based**
```
Unique IDs collected : {1451381950512}
Number of unique instances : 1
Thread-safe? True
```

### Summary Table

| Implementation       | Unique Instances (10 threads) | Thread-Safe? |
|----------------------|-------------------------------|--------------|
| Eager Initialization | 1                             | ✅ Yes       |
| Lazy Initialization  | **10**                        | ❌ No        |
| Synchronized Method  | 1                             | ✅ Yes       |
| Double-Checked Lock  | 1                             | ✅ Yes       |
| Bill Pugh (`__new__`)| 1                             | ✅ Yes       |
| Enum-Based           | 1                             | ✅ Yes       |

**Key Finding:** Lazy Initialization is the only implementation that fails under concurrency.
Every thread passed the `_instance is None` guard before any thread had finished creating and
assigning the instance, resulting in **10 separate `MongoClient` objects** — one per thread.
All five remaining implementations maintained exactly one instance across all 10 threads.

---

## 3. Performance Measurements

Two metrics were recorded:

- **Init time** — elapsed time for the very first `get_instance()` call (instance creation).
- **1000× access time** — elapsed time to call `get_instance()` 1 000 times once the
  instance already exists.

> The state was reset (`_instance = None`) before each measurement phase to ensure the init
> timing reflects true first-creation cost.

### Raw Performance Output

| Implementation       | Init Time (s) | 1000× Access Time (s) |
|----------------------|---------------|-----------------------|
| Eager Initialization | `0.000001`    | `0.000101`            |
| Lazy Initialization  | `4.182411`    | `0.000112`            |
| Synchronized Method  | `0.000001`    | `0.000215`            |
| Double-Checked Lock  | `0.000001`    | `0.000095`            |
| Bill Pugh (`__new__`)| `0.000001`    | `0.000107`            |
| Enum-Based           | `0.000000`    | `0.000029`            |

### Notes on Individual Results

- **Eager & Enum** show `0.000001 s` / `0.000000 s` init time because the instance is
  constructed at **class-load** time; the `get_instance()` / `INSTANCE` call incurs no
  creation overhead — it is a pure attribute lookup.
- **Lazy Initialization** reports `4.182 s` init time. This is the combined cost of the
  deliberate `sleep(0.05)` race-window delay **plus** the actual MongoDB Atlas SRV
  connection handshake (`~4.13 s` on the test network). The high value reflects the real
  cost paid when no lock gates the first connection attempt.
- **Synchronized Method** has the highest repeated-access time (`0.000215 s`) because
  every one of the 1 000 calls acquires and releases the lock, even when the instance is
  already initialised.
- **Double-Checked Locking** (`0.000095 s`) is faster than Synchronized because the lock
  is only acquired on first creation; subsequent calls return immediately after the first
  `None`-check.
- **Enum-Based** is the fastest for repeated access (`0.000029 s`) — attribute lookup on
  an Enum member involves no method call overhead whatsoever.

---

## 4. Comparative Summary

| Implementation       | Init Strategy  | Thread-Safe | Unique Instances (concurrent) | 1000× Access (s) | Relative Speed |
|----------------------|----------------|-------------|-------------------------------|------------------|----------------|
| Eager Initialization | At class load  | ✅          | 1                             | 0.000101         | Fast           |
| Lazy Initialization  | On first call  | ❌          | **10**                        | 0.000112         | Fast (access)  |
| Synchronized Method  | On first call  | ✅          | 1                             | 0.000215         | Slowest        |
| Double-Checked Lock  | On first call  | ✅          | 1                             | 0.000095         | Fast           |
| Bill Pugh (`__new__`)| On first call  | ✅          | 1                             | 0.000107         | Fast           |
| Enum-Based           | At class load  | ✅          | 1                             | **0.000029**     | **Fastest**    |

### Findings

1. **Fastest repeated access:** Enum-Based (`0.000029 s`) — no method call, direct attribute.
2. **Slowest repeated access:** Synchronized Method (`0.000215 s`) — lock acquired on
   every call regardless of whether the instance exists.
3. **Only thread-unsafe implementation:** Lazy Initialization — all 10 concurrent threads
   created independent `MongoClient` instances, violating the singleton guarantee.
4. **Best balance of safety + speed:** Double-Checked Locking — thread-safe, and 2.3×
   faster than Synchronized on repeated access because post-creation calls skip the lock.
5. **Eager and Enum** trade deferred initialization for zero-cost repeated access; they are
   ideal when the database connection is always needed and startup cost is acceptable.
6. **Bill Pugh** (`__new__`-based) offers a Pythonic lazy+safe alternative with access
   speed comparable to Eager (`0.000107 s`), without defining a nested helper class.
