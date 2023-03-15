package ppa.lab.springwebapp.model.dto;

import java.nio.file.Path;
import java.util.List;

public class FileDto {

    private String pathName;

    private List<Path> regularFiles;

    public FileDto(String pathName, List<Path> regularFiles) {
        this.pathName = pathName;
        this.regularFiles = regularFiles;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public List<Path> getRegularFiles() {
        return regularFiles;
    }

    public void setRegularFiles(List<Path> regularFiles) {
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
