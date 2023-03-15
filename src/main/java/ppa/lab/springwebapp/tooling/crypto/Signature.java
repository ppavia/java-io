package ppa.lab.springwebapp.tooling.crypto;

import jakarta.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
public class Signature {

    public String getChecksumFile (Path path) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(path));
        byte[] digest = md.digest();
        return DatatypeConverter.printHexBinary(digest);
    }
}
