public class FileNode implements FileSystemElement {
    private final String name;
    private final long size;

    public FileNode(String name, long size) {
        if (name == null || name.trim().isEmpty() || name.contains("/")) {
            throw new IllegalArgumentException("Invalid file name: " + name);
        }
        if (size < 0) {
            throw new IllegalArgumentException("File size cannot be negative: " + size);
        }
        this.name = name;
        this.size = size;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this); // Double dispatch step 2
    }
}