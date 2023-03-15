package ppa.lab.springwebapp.tooling.validation;

import java.util.Optional;

public class FileValidation {
    private static final String EXT = "UNB";

    protected String extension;

    FileValidation(){
        this.extension = EXT;
    }

    public boolean checkValidNameFile(final String fileName) {
        return fileName != null && fileName.length() > 0;
    }

    public Optional<String> getExtensionByStringHandling(final String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    protected boolean isValidExtension(final String fileName, final String extensionFile) {
        Optional<String> ext = getExtensionByStringHandling(fileName);
        return ext.isPresent() && ext.get().toUpperCase().equals(extensionFile);
    }

    protected boolean isValidFileExtension(String fileName) {
        return isValidExtension(fileName, getExtension());
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
