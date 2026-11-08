public class TotalSizeVisitor implements Visitor {
    private long totalSize = 0;

    @Override
    public void visit(FileNode file) {
        totalSize += file.getSize();
    }

    @Override
    public void visit(DirectoryNode directory) {
        // Traversal walker/client logic or internal recursion over unmodifiable children
        for (FileSystemElement child : directory.getChildren().values()) {
            child.accept(this);
        }
    }

    public long getTotalSize() {
        return totalSize;
    }
}