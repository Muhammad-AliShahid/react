package be.rubus.workshop.security.challenges;

import org.apache.commons.codec.binary.Base32OutputStream;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.apache.commons.codec.binary.BaseNCodecOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class Challenge1 {

    private static final int BUFFER_SIZE = 1024 * 1024; //this is actually bytes
    private static final String B_32_TXT = "logo.b32.txt";
    private static final String B_64_TXT = "logo.b64.txt";

    public static void main(String[] args) throws IOException, URISyntaxException {
        generateBase32EncodedVersion();
        generateBase64EncodedVersion();

        URL logo = Challenge1.class.getResource("/logo.jpeg");

        File f = new File(logo.toURI());
        long originalSize = f.length();

        System.out.printf("The size of the logo : %s%n", originalSize);

        System.out.printf("The size of the Base64 encoded file : %s%n", getFileSize(B_64_TXT));
        System.out.printf("The size of the Base32 encoded file : %s%n", getFileSize(B_32_TXT));

    }

    private static long getFileSize(String fileName) {
        File file = new File(fileName);
        return file.length();
    }

    private static void generateBase32EncodedVersion() throws IOException {
        Base32OutputStream outputStream = new Base32OutputStream(new FileOutputStream(B_32_TXT));

        transformFile(outputStream);
        outputStream.close();
    }


    private static void generateBase64EncodedVersion() throws IOException {
        Base64OutputStream outputStream = new Base64OutputStream(new FileOutputStream(B_64_TXT));


        transformFile(outputStream);
        outputStream.close();
    }

    private static void transformFile(BaseNCodecOutputStream outputStream) throws IOException {
        try (InputStream resource = Challenge1.class.getResourceAsStream("/logo.jpeg")) {
            if (resource == null) {
                throw new RuntimeException("the file logo.jpg is not found at top level of the class path");
            }
            byte[] buffer = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = resource.read(buffer)) > 0) {
                outputStream.write(buffer, 0, read);
            }
        }
    }
}
