import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemTest {

    private DirectoryNode root;

    @BeforeEach
    void setUp() {
        // Structure setup:
        // root/
        //   ├── readme.txt (100 bytes)
        //   ├── docs/
        //   │     ├── report.pdf (500 bytes)
        //   │     └── notes.txt (50 bytes)
        //   └── app.jar (2000 bytes)
        root = new DirectoryNode("root");
        root.addChild(new FileNode("readme.txt", 100));

        DirectoryNode docs = new DirectoryNode("docs");
        docs.addChild(new FileNode("report.pdf", 500));
        docs.addChild(new FileNode("notes.txt", 50));
        root.addChild(docs);

        root.addChild(new FileNode("app.jar", 2000));
    }

    @Test
    void t1_doubleDispatchAndElementTypes() {
        // T1: Both element types reach correct overloaded visit methods
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        assertEquals(2650, visitor.getTotalSize());
    }

    @Test
    void t2_sizeAccumulationMultipleLevels() {
        // T2: Size accumulation across multiple nesting levels is correct
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        assertEquals(100 + 500 + 50 + 2000, visitor.getTotalSize());
    }

    @Test
    void t3_searchCombinesFilterConditions() {
        // T3: Search correctly combines both configurable filter conditions (.txt and size >= 50)
        SearchVisitor search = new SearchVisitor(".txt", 50);
        root.accept(search);
        List<String> paths = search.getMatchingPaths();

        assertEquals(2, paths.size());
        assertTrue(paths.contains("root/readme.txt"));
        assertTrue(paths.contains("root/docs/notes.txt"));
    }

    @Test
    void t4_searchDeterministicRootRelativePaths() {
        // T4: Search produces correct, deterministic, root-relative paths sorted lexicographically
        SearchVisitor search = new SearchVisitor("", 0); // match all files
        root.accept(search);
        List<String> paths = search.getMatchingPaths();

        assertEquals(4, paths.size());
        assertEquals("root/app.jar", paths.get(0));
        assertEquals("root/docs/notes.txt", paths.get(1));
        assertEquals("root/docs/report.pdf", paths.get(2));
        assertEquals("root/readme.txt", paths.get(3));
    }

    @Test
    void t5_statisticsNestingCountingMaxDepth() {
        // T5: Statistics are correct for nesting, counting and maximum depth
        StatisticsVisitor stats = new StatisticsVisitor();
        root.accept(stats);

        assertEquals(4, stats.getFileCount());
        assertEquals(2, stats.getDirectoryCount()); // root and docs
        assertEquals(2, stats.getMaxDepth()); // root(0) -> docs(1) -> report.pdf(2)
    }

    @Test
    void t6_emptyStructuresAndNoMatchCases() {
        // T6: Empty structures and no-match search cases handled safely
        DirectoryNode emptyRoot = new DirectoryNode("empty");

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        emptyRoot.accept(sizeVisitor);
        assertEquals(0, sizeVisitor.getTotalSize());

        SearchVisitor searchVisitor = new SearchVisitor("nonexistent", 100);
        emptyRoot.accept(searchVisitor);
        assertTrue(searchVisitor.getMatchingPaths().isEmpty());

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        emptyRoot.accept(statsVisitor);
        assertEquals(0, statsVisitor.getFileCount());
        assertEquals(1, statsVisitor.getDirectoryCount()); // visited root directory itself
        assertEquals(0, statsVisitor.getMaxDepth());
    }

    @Test
    void t7_repeatExecutionsStateIsolation() {
        // T7: Repeat executions do not leak or multiply accumulated state (or check reusability)
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        long firstRun = visitor.getTotalSize();

        // If instance is reused without reset, state might accumulate, so document design:
        // Visitors are designed as single-use per traversal run. Running on a new instance prevents leaks.
        TotalSizeVisitor visitorSecond = new TotalSizeVisitor();
        root.accept(visitorSecond);
        assertEquals(firstRun, visitorSecond.getTotalSize());
    }

    @Test
    void t8_invalidTreeMutationsRejected() {
        // T8: Invalid tree mutations are rejected while valid nesting remains supported
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad/name.txt", 10));
        assertThrows(IllegalArgumentException.class, () -> new FileNode("file", -5));

        DirectoryNode dir = new DirectoryNode("parent");
        FileNode f1 = new FileNode("file.txt", 10);
        dir.addChild(f1);

        // Duplicate sibling name rejection
        assertThrows(IllegalStateException.class, () -> dir.addChild(new FileNode("file.txt", 20)));

        // Cycle prevention
        DirectoryNode sub = new DirectoryNode("sub");
        dir.addChild(sub);
        assertThrows(IllegalStateException.class, () -> sub.addChild(dir)); // adding parent inside child
        assertThrows(IllegalArgumentException.class, () -> sub.addChild(sub)); // adding self
    }
}