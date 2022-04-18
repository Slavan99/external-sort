package externalsort;

import externalsort.resource.FileResource;
import externalsort.resource.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class PartialFileReader {

    private final FileInformation fileInformation;

    public PartialFileReader(FileInformation information) {
        fileInformation = information;
    }

    // reads first parts of an each subfile and stores in a list
    public ArrayList<ArrayList<String>> readSubFilesIntoBuffer(List<Resource> subFiles) throws IOException {
        ArrayList<ArrayList<String>> result = new ArrayList<>();

        int inputBufferSize = fileInformation.RAM_SPACE / (fileInformation.getSubFileCounter() + 1);

        fileInformation.setInputBufferSubFileSize(inputBufferSize);

        fileInformation.setRemainingOutputBufferSize(
                fileInformation.RAM_SPACE - (inputBufferSize * fileInformation.getSubFileCounter())
        );


        for (int i = 1; i <= fileInformation.getSubFileCounter(); i++) {

            FileResource resource = (FileResource) subFiles.get(i - 1);
            fileInformation.getNumberSubFileMap().put(i, resource);


            List<String> fileContent = readFilePartToStringArray(resource, inputBufferSize);

            result.add((ArrayList<String>) fileContent);

        }

        return result;
    }

    public List<String> readFilePartToStringArray(FileResource resource, int bufferSize) throws IOException {
        RandomAccessFile file = resource.getFile();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        long pos = file.getFilePointer();
        String line = file.readLine();
        while (line != null) {
            byte[] lineBytes = line.getBytes(StandardCharsets.UTF_8);
            // if buffer wil not overflow after writing
            if (byteArrayOutputStream.size() + lineBytes.length + "\n".getBytes().length <= bufferSize) {
                byteArrayOutputStream.write(lineBytes);
                if (!fileInformation.isInitialRead()) {
                    fileInformation.incTotalElements();
                }
                // otherwise, sets a position to beginning of a last unread word
            } else {
                file.seek(pos);
                break;
            }
            byteArrayOutputStream.write("\n".getBytes());
            pos = file.getFilePointer();
            line = file.readLine();
        }

        return Arrays.stream(byteArrayOutputStream.toString().split("\n")).collect(Collectors.toList());
    }

}
