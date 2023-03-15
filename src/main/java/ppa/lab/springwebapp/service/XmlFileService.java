package ppa.lab.springwebapp.service;

import org.springframework.stereotype.Service;
import ppa.lab.springwebapp.exception.ServiceException;
import ppa.lab.springwebapp.exception.TechnicalException;
import ppa.lab.springwebapp.model.dto.FileDto;
import ppa.lab.springwebapp.service.api.FileService;
import ppa.lab.springwebapp.tooling.xml.XmlFileParser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class XmlFileService implements FileService {
    private final XmlFileParser xmlFileParser;

    public XmlFileService(XmlFileParser xmlFileParser) {
        this.xmlFileParser = xmlFileParser;
    }

    @Override
    public FileDto loadFiles(String rootPath) throws ServiceException {
        List<Path> paths = null;
        Map<String, Map<String, String>> xmls;
        try {
            paths = xmlFileParser.loadXmlFiles(rootPath);
            xmls = readXmls(paths);
        } catch (IOException e) {
            throw new ServiceException(e.getMessage(), e);
        }
        return new FileDto(rootPath, paths);
    }

    private Map<String, Map<String, String>> readXmls (List<Path> paths) {
        Map<String, Map<String, String>> xmls = new HashMap<>();
        paths.forEach(path -> {
            try {
                xmls.put(path.normalize().toString(), xmlFileParser.readInputXmlFile(path.toFile()));
            } catch (TechnicalException e) {
                throw new RuntimeException(e);
            }
        });
        return xmls;
    }
}