@FunctionalInterface
public interface ItemFactory {
    CreationItem create(int x, int y, String resourcePath, int width, int height);
}