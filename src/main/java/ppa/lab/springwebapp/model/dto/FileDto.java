package ppa.lab.springwebapp.model.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppa.lab.springwebapp.tooling.crypto.Signature;
import ppa.lab.springwebapp.tooling.xml.XmlFileParser;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDto {
    private static final Logger LOG = LoggerFactory.getLogger(FileDto.class);

    private final Signature signature = new Signature();

    private String pathName;

    private Map<String, Path> regularFiles = new HashMap<>();

    public FileDto(String pathName, List<Path> paths) {
        this.pathName = pathName;
        setChecksumFile(paths);
    }

    private void setChecksumFile(List<Path> files) {
        files.forEach(path -> {
            try {
                this.regularFiles.put(signature.getChecksumFile(path), path);
            } catch (NoSuchAlgorithmException | IOException e) {
                LOG.error("Le calcul du checksum a échoué pour le fichier %s".formatted(path != null ? path.toString() : "NULL !!"));
            }
        });
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public Map<String, Path> getRegularFiles() {
        return regularFiles;
    }

    public void setRegularFiles(Map<String, Path> regularFiles) {
        this.regularFiles = regularFiles;
    }

    @Override
    public String toString() {
        return "FileDto{" +
                "pathName='" + pathName + '\'' +
                ", regularFiles=" + regularFiles +
                '}';
    }
}
