package ppa.lab.springwebapp.model.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ppa.lab.springwebapp.tooling.crypto.Signature;

import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileDto {
    private static final Logger LOG = LoggerFactory.getLogger(FileDto.class);

    private final Signature signature = new Signature();

    private String rootPath;

    private Map<String, Path> regularFiles = new HashMap<>();

    public FileDto(String rootPathName, List<Path> paths) {
        this.rootPath = rootPathName;
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

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
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
                "pathName='" + rootPath + '\'' +
                ", regularFiles=" + regularFiles +
                '}';
    }
}
