package externalsort;

import externalsort.resource.FileResource;
import lombok.Data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Data
public class FileInformation {

    public final int RAM_SPACE = 1000 /*500 * 1024 * 1024*/;

    // counts an amount of subfiles, created during the split of original file
    private int subFileCounter = 0;

    // counts total elements of a source file
    private int totalElements = 0;

    // size of a buffer for an each sorted subfile
    private int inputBufferSubFileSize = 0;

    // remaining space, which is used during the k-way merging
    private int remainingOutputBufferSize = 0;

    // flag for the first read for the subfiles
    private boolean initialRead = false;

    // stores info about every subfile, used for dynamic read during k-way merge
    private final Map<Integer, FileResource> numberSubFileMap = new HashMap<>();

    public void closeAllFiles() throws IOException {
        for (FileResource r : numberSubFileMap.values()) {
            r.getFile().close();
        }
    }

}
