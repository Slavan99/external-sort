package externalsort;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Enter the file name");
        Scanner scanner = new Scanner(System.in);
        String fileName = scanner.next();
        ExternalSort externalSort = new ExternalSort();
        String sortedFileName = externalSort.sortFile(fileName);
        System.out.println("A sorted file is located in " + sortedFileName);


    }

}
