package externalsort;

import externalsort.resource.FileResource;
import externalsort.resource.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class ExternalSort {

    private final FileSplitter fileSplitter;

    private final FileInformation fileInformation;

    private final PartialFileReader partialFileReader;

    private String maxString;


    public ExternalSort() {
        fileInformation = new FileInformation();
        partialFileReader = new PartialFileReader(fileInformation);
        fileSplitter = new FileSplitter(fileInformation, partialFileReader);
    }


    // facade method
    public String sortFile(String fileName) throws IOException {
        FileResource resource = new FileResource(fileName, new RandomAccessFile(fileName, "rw"));
        List<Resource> subFileResources = fileSplitter.splitFileIntoSubFilesByName(resource);
        ArrayList<ArrayList<String>> listOfSortedLists = partialFileReader.readSubFilesIntoBuffer(subFileResources);
        String s = kWayMerge(listOfSortedLists, fileInformation.getInputBufferSubFileSize(),
                fileInformation.getRemainingOutputBufferSize(), fileName);
        fileInformation.closeAllFiles();
        return s;
    }

    // main algorithm
    private String kWayMerge(ArrayList<ArrayList<String>> listOfLists, int bufferSize,
                             int remainingOutputBufferSize, String fileName) throws IOException {
        generateMaxString();
        int totalElements = fileInformation.getTotalElements();
        String[] fileNameSplit = fileName.split("\\.");
        String fileNameBeforeDot = fileNameSplit[0];
        String ext = fileNameSplit[1];
        String sortedFileName = fileNameBeforeDot + "_sorted_" + "." + ext;
        RandomAccessFile resultFile = new RandomAccessFile(sortedFileName, "rw");
        int size = listOfLists.size();
        // initial minimum String value to be compared with
        // algorithm will write info to this buffer until it reaches a remaining capacity
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        int p = 0;
        // while all elements are not read
        while (p < totalElements) {
            String min = maxString;
            int minPos = -1;
            // iteration over each sublist
            for (int i = 0; i < size; i++) {
                ArrayList<String> subList = listOfLists.get(i);
                // if sublist has some elements
                if (subList.size() > 0) {
                    String firstElementOfList = subList.get(0);
                    // in case respective file is read fully
                    if (firstElementOfList.equals("")) {
                        continue;
                    }
                    // compares current element(string) with minimum, remembers it
                    if (firstElementOfList.compareTo(min) <= 0) {
                        min = firstElementOfList;
                        minPos = i;
                    }
                    // if a sublist is empty, loads another part of respective subfile
                } else {
                    listOfLists.set(i,
                            (ArrayList<String>) partialFileReader.readFilePartToStringArray(
                                    fileInformation.getNumberSubFileMap().get(i + 1), bufferSize));
                }
            }
            // removes a found minimum element
            listOfLists.get(minPos).remove(0);
            // if buffer will be full, writes a result to file and desolates it
            if (result.size() + min.getBytes().length + "\n".getBytes().length > remainingOutputBufferSize) {
                resultFile.write(result.toByteArray());
                result = new ByteArrayOutputStream();
            }
            result.write(min.getBytes());
            result.write("\n".getBytes());
            p++;

        }
        // writes remains of a buffer to file
        resultFile.write(result.toByteArray());
        resultFile.close();
        return sortedFileName;
    }

    private void generateMaxString() {
        StringBuilder maxStringBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            maxStringBuilder.insert(0, Character.MAX_VALUE);
        }
        maxString = maxStringBuilder.toString();
    }


}
