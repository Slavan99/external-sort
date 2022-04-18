package externalsort;

import externalsort.resource.FileResource;
import externalsort.resource.Resource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// this class splits file into subfiles
public class FileSplitter {

    private final FileInformation fileInformation;

    private final PartialFileReader partialFileReader;

    public FileSplitter(FileInformation information, PartialFileReader fileReader) {
        fileInformation = information;
        partialFileReader = fileReader;
    }


    public List<Resource> splitFileIntoSubFilesByName(FileResource resource) throws IOException {
        String fileName = resource.getName();
        List<Resource> resultList = new ArrayList<>();
        RandomAccessFile file = resource.getFile();
        String[] fileNameSplit = fileName.split("\\.");
        String fileNameBeforeDot = fileNameSplit[0];
        String ext = fileNameSplit[1];
        int partitionIndex = 1;
        while (file.getFilePointer() != file.length()) {
            String[] strings = partialFileReader.readFilePartToStringArray(
                    resource, fileInformation.RAM_SPACE).toArray(new String[0]
            );
            Arrays.sort(strings);
            String subFileName = fileNameBeforeDot + "_sorted_" + partitionIndex + "." + ext;
            Resource subFileResource = new FileResource(subFileName, new RandomAccessFile(subFileName, "rw"));
            resultList.add(subFileResource);
            writeStringArrayToFile(strings, subFileResource);

            partitionIndex++;
        }
        fileInformation.setInitialRead(true);
        fileInformation.setSubFileCounter(partitionIndex - 1);

        return resultList;

    }

    private void writeStringArrayToFile(String[] array, Resource fileResource) {
        String fileName = fileResource.getName();
        try (BufferedWriter outputWriter = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : array) {
                outputWriter.write(line);
                if (!array[array.length - 1].equals(line))
                    outputWriter.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
