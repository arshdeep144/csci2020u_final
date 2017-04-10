package Client;

import java.io.File;

public class ProjectFile {
    private File file;

    public ProjectFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public String toString() {
        return this.file.getName();
    }
}
