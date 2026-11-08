import java.util.*;

/**
 * SearchVisitor accepts filename criterion and minimum-size threshold (R4).
 */
public class SearchVisitor implements Visitor {
    private final String nameCriterion; // e.g., exact match or substring, let's use exact/contains
    private final long minSizeThreshold;
    private final List<String> matchingPaths = new ArrayList<>();

    // Track current traversal path stack
    private final Deque<String> pathStack = new ArrayDeque<>();

    public SearchVisitor(String nameCriterion, long minSizeThreshold) {
        if (minSizeThreshold < 0) {
            throw new IllegalArgumentException("Minimum size threshold cannot be negative");
        }
        this.nameCriterion = nameCriterion;
        this.minSizeThreshold = minSizeThreshold;
    }

    @Override
    public void visit(FileNode file) {
        boolean nameMatches = (nameCriterion == null) || file.getName().contains(nameCriterion);
        boolean sizeMatches = file.getSize() >= minSizeThreshold;

        if (nameMatches && sizeMatches) {
            // Build root-relative path using '/' separator
            List<String> segments = new ArrayList<>(pathStack);
            segments.add(file.getName());
            matchingPaths.add(String.join("/", segments));
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        pathStack.addLast(directory.getName());
        for (FileSystemElement child : directory.getChildren().values()) {
            child.accept(this);
        }
        pathStack.removeLast();
    }

    public List<String> getMatchingPaths() {
        // R4 & Constraints: Sort lexicographically for deterministic output
        List<String> sorted = new ArrayList<>(matchingPaths);
        Collections.sort(sorted);
        return sorted;
    }
}