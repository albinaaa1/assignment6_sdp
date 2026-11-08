
# Advanced File System — Visitor Pattern (Assignment 06)

**Group:** SE-2527  
**Name:** Onglassyn Albina 


---

## Overview

This project implements an in-memory hierarchical file system leveraging the **Visitor behavioral pattern** in Java 17. It decouples independent operations from the composite node structures (`FileNode` and `DirectoryNode`), using **Double Dispatch** to eliminate runtime type discrimination (`instanceof` or `getClass()` branching).

### Supported Visitors
1. **`TotalSizeVisitor`**: Computes the total byte size of all files in a tree/subtree (directories do not contribute standalone size).
2. **`SearchVisitor`**: Searches for files based on a configurable filename criterion and minimum size threshold (`size >= threshold`), returning deterministic, sorted, root-relative paths.
3. **`StatisticsVisitor`**: Collects total file count, directory count (including visited root), and maximum tree depth.

---

## Design Choices & Integrity Rules

- **Double Dispatch**: Each node implements `accept(Visitor visitor)`, which calls the corresponding overloaded `visitor.visit(this)` method.
- **Tree Integrity**:
  - Rejects negative file sizes (`IllegalArgumentException`).
  - Rejects duplicate sibling names inside the same directory (`IllegalStateException`).
  - Prevents cycles and multiple parent ownership when adding child directories.
- **Encapsulation**: `DirectoryNode.getChildren()` returns an unmodifiable view (`Collections.unmodifiableMap`) to preserve directory state invariants.
- **Traversal**: Managed via internal iteration walker within element nodes and visitors. Each reachable element is visited exactly once per traversal.

---

## How to Run

### Requirements
- **Java 17** or higher
- **Maven 3.x**

### 1. Run Automated Unit Tests (JUnit 5)
To execute all 8 automated tests from the root of the project:

```bash
mvn test

```

### 2. Run the Demonstration Application

To run the executable demonstration (`Main.java`):

```bash
mvn compile exec:java -Dexec.mainClass="org.example.Main"

```

*Or run `Main.java` directly inside IntelliJ IDEA.*

---

## Test Suite Summary

The test suite is located in `src/test/java/FileSystemTest.java` and covers all required checkpoints:

* **T1**: Verifies double dispatch routing to correct overloaded visit methods.
* **T2**: Verifies multi-level subtree size calculation.
* **T3**: Verifies combining filename matching and size threshold filters.
* **T4**: Verifies deterministic, lexicographically sorted root-relative path generation.
* **T5**: Verifies exact file count, directory count, and maximum tree depth metrics.
* **T6**: Ensures safe handling of empty directory structures and no-match search queries.
* **T7**: Verifies state isolation across repeat executions.
* **T8**: Verifies rejection of invalid mutations (duplicate siblings, negative sizes, cyclical references).

---

## Extension Boundary Trade-off
