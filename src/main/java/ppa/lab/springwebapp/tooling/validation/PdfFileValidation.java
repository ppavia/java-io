package ppa.lab.springwebapp.tooling.validation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PdfFileValidation extends FileValidation {
    private static final String EXT = "PDF";
    private static final String PDF_CONTENT_TYPE = "application/pdf";

    public PdfFileValidation(){
        super();
        this.extension = EXT;
    }

    public boolean isPdfFileExtension(final String fileName) {
        return isValidFileExtension(fileName);
    }

    public boolean isPdfFile(final Path path) throws IOException {
        return checkValidNameFile(path.toString()) && isPdfFileExtension(path.toString()) && Files.probeContentType(path).equalsIgnoreCase(PDF_CONTENT_TYPE);
    }
}
