package ppa.lab.springwebapp.tooling.validation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonFileValidation extends FileValidation {
    private static final String EXT = "JSON";

    private static final String JSON_CONTENT_TYPE = "application/json";

    public JsonFileValidation(){
        super();
        this.extension = EXT;
    }

    public boolean isJsonFileExtension(final String fileName) {
        return isValidFileExtension(fileName);
    }

    public boolean isJsonFile(final Path path) throws IOException {
        return checkValidNameFile(path.toString()) && isJsonFileExtension(path.toString()) && Files.probeContentType(path).equalsIgnoreCase(JSON_CONTENT_TYPE);
    }
}