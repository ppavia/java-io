package ppa.lab.springwebapp.tooling.xml;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class XmlFileParser {

    public List<Path> loadXmlFiles (String pathName) throws IOException {
        Path rootPath = Path.of(pathName);
        if(!Files.isDirectory(rootPath)) {
            return new ArrayList<>();
        }
        try (Stream<Path> files = Files.list(rootPath)) {
            return files
                    .filter(Files::isRegularFile)
                    .toList();
        }
    }

    public boolean isXml (Path file) {
        return true;
    }
}
