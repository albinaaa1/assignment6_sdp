public interface FileSystemElement {
    void accept(Visitor visitor);
    String getName();
    long getSize();
}