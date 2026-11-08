public class Main {
    public static void main(String[] args) {
        DirectoryNode root = new DirectoryNode("root");
        root.addChild(new FileNode("readme.txt", 150));

        DirectoryNode src = new DirectoryNode("src");
        src.addChild(new FileNode("Main.java", 1200));
        src.addChild(new FileNode("Helper.java", 450));
        root.addChild(src);

        // 1. TotalSizeVisitor
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.println("Total Size: " + sizeVisitor.getTotalSize() + " bytes");

        // 2. SearchVisitor
        SearchVisitor searchVisitor = new SearchVisitor(".java", 500);
        root.accept(searchVisitor);
        System.out.println("Search Results (.java >= 500 bytes): " + searchVisitor.getMatchingPaths());

        // 3. StatisticsVisitor
        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        System.out.println("Files count: " + statsVisitor.getFileCount());
        System.out.println("Directories count: " + statsVisitor.getDirectoryCount());
        System.out.println("Max tree depth: " + statsVisitor.getMaxDepth());
    }
}