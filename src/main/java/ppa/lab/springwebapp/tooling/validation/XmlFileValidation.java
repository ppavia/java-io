package ppa.lab.springwebapp.tooling.validation;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class XmlFileValidation extends FileValidation {
    private static final String EXT = "XML";
    private static final String XML_CONTENT_TYPE = "text/xml";
    public XmlFileValidation(){
        super();
        this.extension = EXT;
    }

    public boolean isXmlFileExtension(final String fileName) {
        return isValidFileExtension(fileName);
    }

    public boolean isXmlFile(final Path path) throws IOException {
        return checkValidNameFile(path.toString()) && isXmlFileExtension(path.toString()) && Files.probeContentType(path).equalsIgnoreCase(XML_CONTENT_TYPE);
    }
}
