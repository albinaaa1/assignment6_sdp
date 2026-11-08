public class StatisticsVisitor implements Visitor {
    private int fileCount = 0;
    private int directoryCount = 0;
    private int currentDepth = 0;
    private int maxDepth = 0;

    @Override
    public void visit(FileNode file) {
        fileCount++;
        // Глубина файла равна текущей глубине директории, в которой он лежит
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }

        currentDepth++;
        for (FileSystemElement child : directory.getChildren().values()) {
            child.accept(this);
        }
        currentDepth--;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}
