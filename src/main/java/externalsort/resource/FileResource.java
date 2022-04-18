package externalsort.resource;

import java.io.RandomAccessFile;

public class FileResource implements Resource {

    private final String name;

    private final RandomAccessFile file;

    public FileResource(String name, RandomAccessFile file) {
        this.name = name;
        this.file = file;
    }

    @Override
    public String getName() {
        return name;
    }

    public RandomAccessFile getFile() {
        return file;
    }
}
