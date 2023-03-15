package ppa.lab.springwebapp.tooling.validation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class XmlFileValidation extends FileValidation {
    private static final String EXT = "XML";
    private static final List<String> XML_CONTENT_TYPE = List.of("text/xml", "application/xml");
    public XmlFileValidation(){
        super();
        this.extension = EXT;
    }

    public boolean isXmlFileExtension(final String fileName) {
        return isValidFileExtension(fileName);
    }

    public boolean isXmlFile(final Path path) throws IOException {
        return checkValidNameFile(path.toString())
                && isXmlFileExtension(path.toString())
                && XML_CONTENT_TYPE.contains(Files.probeContentType(path).toLowerCase());
    }
}
