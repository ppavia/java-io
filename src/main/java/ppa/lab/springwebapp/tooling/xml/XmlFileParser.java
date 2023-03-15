package ppa.lab.springwebapp.tooling.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ppa.lab.springwebapp.tooling.validation.XmlFileValidation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class XmlFileParser {
    private static final Logger LOG = LoggerFactory.getLogger(XmlFileParser.class);

    private XmlFileValidation xmlFileValidation;

    public XmlFileParser(XmlFileValidation xmlFileValidation) {
        this.xmlFileValidation = xmlFileValidation;
    }

    public List<Path> loadXmlFiles (String pathName) throws IOException {
        Path rootPath = (new File(pathName)).toPath();
        if(!Files.isDirectory(rootPath)) {
            return new ArrayList<>();
        }
        try (Stream<Path> files = Files.list(rootPath)) {
            return files
                    .filter(f -> Files.isRegularFile(f) && isXml(f))
                    .toList();
        }
    }

    private boolean isXml (Path file) {
        try {
            return xmlFileValidation.isXmlFile(file);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return false;
    }
}
