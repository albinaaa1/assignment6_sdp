import java.util.*;

/**
 * Concrete Element representing a directory holding named child elements (R1, R2, R6).
 */
public class DirectoryNode implements FileSystemElement {
    private final String name;
    private final Map<String, FileSystemElement> children = new LinkedHashMap<>();
    private DirectoryNode parent = null; // Prevents multiple parent ownership and cycles

    public DirectoryNode(String name) {
        if (name == null || name.trim().isEmpty() || name.contains("/")) {
            throw new IllegalArgumentException("Invalid directory name: " + name);
        }
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        // Directories do not contribute their own byte size directly, 
        // but sum up children if requested or handled by visitors.
        return children.values().stream().mapToLong(FileSystemElement::getSize).sum();
    }

    public Map<String, FileSystemElement> getChildren() {
        // Encapsulation: return unmodifiable view to prevent external mutation (Engineering constraints)
        return Collections.unmodifiableMap(children);
    }

    public void addChild(FileSystemElement child) {
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null");
        }
        String childName = child.getName();

        // R6: Prevent duplicate sibling names
        if (children.containsKey(childName)) {
            throw new IllegalStateException("Duplicate sibling name: " + childName);
        }

        // R6: Prevent multiple parent ownership and cycles
        if (child instanceof DirectoryNode) {
            DirectoryNode childDir = (DirectoryNode) child;
            if (childDir == this) {
                throw new IllegalArgumentException("Cannot add directory to itself (cycle prevention)");
            }
            // Check if adding this would create a cycle up the ancestor chain
            DirectoryNode current = this;
            while (current != null) {
                if (current == childDir) {
                    throw new IllegalStateException("Cycle detected: cannot add an ancestor as a child");
                }
                current = current.parent;
            }
            if (childDir.parent != null) {
                throw new IllegalStateException("Directory already has a parent");
            }
            childDir.parent = this;
        }

        children.put(childName, child);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this); // Double dispatch step 2
    }
}