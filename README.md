# Multithreading in Java: Interview Quick Reference

Practice repo for Java concurrency. Each package demonstrates one family of concepts; each section below gives a quick summary, the code to look at, and likely interview questions.

| Package | Concepts |
|---|---|
| `basics` | Thread vs Runnable, daemon threads |
| `monitorlock` | `synchronized`, monitor locks, `wait()` / `notifyAll()`, producer-consumer |
| `customlock` | `ReentrantLock`, `ReadWriteLock`, `StampedLock`, `Semaphore`, `Condition` |
| `lockfree` | `volatile` vs `AtomicInteger` (CAS) |

---

## 1. Basics (`basics`)

### Creating threads
Two ways, shown in `CustomThread` and `CustomRunnable`:

- **Extend `Thread`**: `class CustomThread extends Thread`. Uses up your single inheritance slot.
- **Implement `Runnable`**: `new Thread(new CustomRunnable(), "name")`. Preferred: separates the *task* from the *thread*, works with lambdas and executors.

Key points:
- `start()` creates a new thread and calls `run()` on it. Calling `run()` directly just runs on the current thread.
- A thread can only be started once (`IllegalThreadStateException` otherwise).
- Thread order of execution is decided by the scheduler, not by the order of `start()` calls.

### Daemon threads (`Main.test02`)
- `t.setDaemon(true)` must be called **before** `start()`.
- The JVM exits when only daemon threads are left, so they get killed abruptly (no `finally`, no clean shutdown).
- In the demo, `main` finishes first and the sleeping daemon thread never prints "completed".
- Use for background housekeeping (GC, monitoring), never for work that must finish.

**Interview Qs:** Thread vs Runnable? `start()` vs `run()`? What is a daemon thread? Runnable vs Callable?

---

## 2. Monitor Locks: `synchronized` (`monitorlock`)

### Concept
Every Java object has an intrinsic **monitor**. `synchronized` acquires it, so only one thread at a time can hold it. It gives both **mutual exclusion** and **visibility** (happens-before on release/acquire).

### Behaviours demonstrated (`MonitorLockExample`, `Main.test01`)
- `synchronized` method locks on `this`. While `task1()` sleeps for 10s holding the monitor, `task2()` blocks at its `synchronized (this)` block, but `task3()` (not synchronized) runs freely.
- Only the synchronized region blocks; non-synchronized methods are never affected.
- Locking is **per object**, not per class. Two threads on two different instances don't block each other (see `customlock.Main.test01`). For class-wide locking use `static synchronized` or `synchronized(Foo.class)`.
- Intrinsic locks are **reentrant**: a thread holding the monitor can re-enter.

### `wait()` / `notify()` / `notifyAll()` (`AddCheckout`, `Main.test02`)
- Used for **inter-thread communication**: a thread waits for a condition, another thread signals it.
- `wait()` releases the monitor and suspends the thread. `notify()` wakes one waiter, `notifyAll()` wakes all.
- Must be called **while holding the monitor**, else `IllegalMonitorStateException`.
- Demo: checkout thread waits until the add thread adds an item and calls `notifyAll()`.

### Producer-Consumer (`ProducerConsumer`)
- `addElement` / `getElement`: simple synchronized version that **doesn't wait**; just prints "full"/"empty" and drops the operation (busy-style, lossy).
- `produce` / `consume`: proper version. Waits in a **`while` loop** when the buffer is full/empty, and calls `notifyAll()` after changing state.

**Always use `while`, not `if`, around `wait()`**: guards against *spurious wakeups* and against another thread changing the condition between wake-up and re-acquiring the lock.

**Interview Qs:** What is a monitor? Why must `wait()` be in a synchronized block? `wait()` vs `sleep()` (releases lock vs keeps it)? `notify()` vs `notifyAll()`? Why `while` instead of `if`? Object-level vs class-level lock?

---

## 3. Explicit Locks (`customlock`)

`java.util.concurrent.locks` gives more control than `synchronized`: `tryLock`, timed/interruptible locking, fairness, multiple conditions, read/write separation. The cost: you **must unlock in `finally`**.

### 3.1 `ReentrantLock` (`SharedResource.process(lock)`, `Main.test02`)
- One shared lock object passed to two threads working on **different** resource objects. Because the lock is shared, they run one after another (about 4s each), unlike `synchronized` on separate instances, where the lock is the object itself.
- Reentrant: same thread can lock multiple times (hold count), must unlock the same number of times.
- Extras over `synchronized`: `tryLock()`, `tryLock(timeout)`, `lockInterruptibly()`, fair mode `new ReentrantLock(true)`.

### 3.2 `ReadWriteLock` / `ReentrantReadWriteLock` (`SharedResource.produce/consume`, `Main.test03`)
- **Read lock is shared**: many readers at once. **Write lock is exclusive**: no readers or writers.
- Demo: two threads take the read lock together (overlapping timestamps), the write-lock threads wait their turn.
- Best when reads greatly outnumber writes.
- Writer can downgrade to a reader, but you **cannot upgrade** read to write (deadlocks).
- Note: the methods are named `produce` (read lock) and `consume` (write lock) in this repo.

### 3.3 `StampedLock` (`CustomResource`, `Main.test04`)
- Returns a **stamp (version)** on each lock operation. Three modes:
  - `writeLock()`: exclusive.
  - `readLock()`: shared pessimistic read.
  - `tryOptimisticRead()`: **no lock taken**; read the data, then `validate(stamp)`. If a writer got in meanwhile, validation fails and you retry or fall back to a real read lock.
- Demo: two optimistic readers and one writer; the writer invalidates the stamp, so the reader goes down the "rollback" branch.
- Faster than `ReentrantReadWriteLock` for read-heavy workloads.
- **Not reentrant**, and has no `Condition` support.
- Optimistic readers must only *read* inside the optimistic section, then validate before using the values.

### 3.4 `Semaphore` (`SemaphoreResource`, `Main.test05`)
- A counter of **permits**. `acquire()` blocks when none left, `release()` returns one.
- `new Semaphore(2)` with 4 threads: only 2 run the critical section at a time, the other 2 start when permits free up (about 1.5s later).
- Use for **resource pools / throttling** (connection pool, rate limiting). `Semaphore(1)` behaves like a mutex, except it is **not owner-bound** (a different thread may release).

### 3.5 `Condition` (`ProducerReceiver`, `Main.test06`)
- The explicit-lock replacement for `wait/notify`: `condition.await()`, `signal()`, `signalAll()`; created via `lock.newCondition()`.
- Advantage: **multiple conditions per lock** (e.g. `notFull` and `notEmpty`) so you wake only the relevant group.
- Same rules: hold the lock when calling, re-check the condition in a loop.

### `synchronized` vs `ReentrantLock` (very common question)

| | `synchronized` | `ReentrantLock` |
|---|---|---|
| Release | Automatic | Manual (`finally`) |
| Try / timeout | No | `tryLock()`, `tryLock(t)` |
| Interruptible wait | No | `lockInterruptibly()` |
| Fairness | No | Optional |
| Conditions | One (`wait/notify`) | Many |

**Interview Qs:** Reentrant meaning? Fair vs unfair lock? When ReadWriteLock vs StampedLock? What is optimistic locking? Semaphore vs mutex? `await()` vs `wait()`? Why unlock in `finally`?

---

## 4. Lock-Free Concurrency (`lockfree`)

Demo: 2 threads increment an `AtomicCounter`, 2 threads increment a `VolatileCounter`, 100,000 times each, then `join()` and print.

| | Result | Why |
|---|---|---|
| `AtomicInteger` | Always 200,000 | `addAndGet` is one atomic **CAS** (compare-and-swap) operation, no lock |
| `volatile int` with `counter += 1` | Usually **less than** 200,000 | `+=` is read, add, write: three steps, so updates are lost |

### `volatile`
- Guarantees **visibility** (writes immediately visible to other threads) and **ordering** (no reordering across the volatile access).
- Does **not** give atomicity for compound operations (`++`, check-then-act).
- Good for flags (`volatile boolean running`), safe publication, double-checked locking.

### Atomics and CAS
- CAS: "if the value is still X, set it to Y", done in a single CPU instruction; retry in a loop on failure. No blocking, no deadlock.
- Weak under very high contention (many retries), and has the **ABA problem** (see `AtomicStampedReference`).
- Alternatives: `LongAdder` for high-contention counters, `AtomicReference` for objects.

### `join()`
- `t.join()` makes the calling thread wait until `t` finishes. Without it, `main` would print before the counters finish.

**Interview Qs:** volatile vs synchronized vs atomic? Why is `volatile int++` not thread-safe? What is CAS? ABA problem? `AtomicInteger` vs `LongAdder`?

---

## Core Concepts Cheat Sheet

- **Race condition**: result depends on thread timing. Fix with atomicity or locking.
- **Visibility**: one thread's write may not be seen by another. Fix with `volatile`, locks, or atomics.
- **Atomicity**: operation can't be interrupted midway. `synchronized`, locks and atomics provide it.
- **Deadlock**: threads wait on each other forever. Needs mutual exclusion, hold-and-wait, no preemption, circular wait. Prevent with consistent lock ordering or `tryLock` with timeout.
- **Starvation / livelock**: thread never gets a turn / threads keep reacting to each other without progress.
- **Happens-before**: JMM rule that guarantees visibility (lock release then acquire, volatile write then read, `start()`, `join()`).
- **Thread states**: NEW, RUNNABLE, BLOCKED (waiting for monitor), WAITING (`wait`, `join`), TIMED_WAITING (`sleep`, timed wait), TERMINATED.

## Where Each Concept Lives

| Concept | File |
|---|---|
| Thread / Runnable | `basics/CustomThread.java`, `basics/CustomRunnable.java` |
| Daemon thread | `basics/Main.java` (`test02`) |
| `synchronized` monitor | `monitorlock/MonitorLockExample.java` |
| `wait` / `notifyAll` | `monitorlock/AddCheckout.java` |
| Producer-consumer (monitor) | `monitorlock/ProducerConsumer.java` |
| `ReentrantLock`, `ReadWriteLock` | `customlock/SharedResource.java` |
| `StampedLock` | `customlock/CustomResource.java` |
| `Semaphore` | `customlock/SemaphoreResource.java` |
| `Condition` await/signal | `customlock/ProducerReceiver.java` |
| `volatile` vs `Atomic` | `lockfree/` |

---

## Known Issues in This Code (great interview talking points)

Spotting these in an interview shows real understanding:

1. **`AddCheckout.addItem()` is not `synchronized`** but calls `notifyAll()`, which throws `IllegalMonitorStateException`. It also reads/writes `cartEmpty` without the lock, and `cartEmpty` isn't `volatile`. Fix: make `addItem` synchronized. Also use `while (cartEmpty)` instead of `if`.
2. **`ProducerConsumer` demo (`monitorlock.Main.test05`)**: the producer runs 5 iterations but the consumer runs 6 (`i<=5`), so the last `consume()` waits forever (hang).
3. **`PriorityQueue` is used as the buffer** in `ProducerConsumer` and `ProducerReceiver`. It orders by priority, not FIFO, and is not thread-safe. Use `ArrayDeque`/`LinkedList` under a lock, or better a `BlockingQueue`.
4. **`ProducerReceiver.producer()` always calls `condition.await()`** at the end, even after producing, so the final producer iteration can wait forever with no one to signal it. The consumer also sleeps while holding the lock (`Thread.sleep(1500)`) and uses `if` instead of `while` around `await()`.
5. **`CustomResource.producer()`** (optimistic read) writes `a = 26` without holding any lock; an optimistic read should only read and validate. Also, `producerReadLock()` calls `validate()` on a stamp from a held read lock, which will fail if a writer is waiting or has been admitted.
6. **`VolatileCounter` is deliberately wrong** (lost updates). That is the point of the demo, but name it correctly in an interview: *volatile is not atomic*.
7. **Javadoc method names drift** in `customlock.Main` (comments say `test07`–`test10` but the methods are `test01`–`test04`).

## Production-Level Next Steps (topics interviewers often ask next)

Not yet in this repo, worth adding and revising:

- `ExecutorService` / thread pools (`newFixedThreadPool`, `ThreadPoolExecutor` params), `Callable` and `Future`, `CompletableFuture`
- `BlockingQueue` (`ArrayBlockingQueue`, `LinkedBlockingQueue`) as the idiomatic producer-consumer
- `ConcurrentHashMap`, `CopyOnWriteArrayList`
- `CountDownLatch`, `CyclicBarrier`, `Phaser`, `Exchanger`
- `ThreadLocal`, `ForkJoinPool` and parallel streams
- Deadlock demo and detection (`jstack`), virtual threads (Java 21+)
