package ppa.lab.springwebapp.tooling;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ppa.lab.springwebapp.tooling.validation.XmlFileValidation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileValidationTest {
    private ClassLoader classLoader = getClass().getClassLoader();

    private String xmlPathName = "dataTest.xml";

    private Path xmlPath;

    private XmlFileValidation xmlFileValidation;

    @BeforeAll
    void setUp() throws URISyntaxException {
        xmlFileValidation = new XmlFileValidation();
        xmlPath = getFile(xmlPathName);
    }

    @Test
    void isXmlFileExtensionTest_ok() {
        String fileName = "toto.xml";
        assertTrue(xmlFileValidation.isXmlFileExtension(fileName));
    }

    @Test
    void isXmlFileExtensionTest_badExtension() {
        String fileName = "toto.txt";
        assertFalse(xmlFileValidation.isXmlFileExtension(fileName));
    }

    @Test
    void isXmlFileExtensionTest_null() {
        assertFalse(xmlFileValidation.isXmlFileExtension(null));
    }

    @Test
    void isXmlFileTest_ok() {
        try {
            assertTrue(xmlFileValidation.isXmlFile(xmlPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------------------------------------------------------------
    // setup data
    // ---------------------------------------------------------------------

    private Path getFile (final String fileName) throws URISyntaxException {
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! %s".formatted(fileName));
        }
        return Path.of(resource.toURI());
    }
}
