package ppa.lab.springwebapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ppa.lab.springwebapp.exception.RestException;
import ppa.lab.springwebapp.model.dto.FileDto;
import ppa.lab.springwebapp.model.dto.RestResponse;
import ppa.lab.springwebapp.tooling.xml.XmlFileParser;
import ppa.lab.springwebapp.utils.HttpResponseUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final XmlFileParser xmlFileParser;

    public FilesController(XmlFileParser xmlFileParser) {
        this.xmlFileParser = xmlFileParser;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<FileDto>> files(
            HttpServletRequest request
    ) throws RestException {
        List<Path> paths = null;
        FileDto fileDto = null;
        try {
            String rootPath = getFilePathesTest();
            paths = xmlFileParser.loadXmlFiles(rootPath);
            fileDto = new FileDto(rootPath, paths);
        } catch (IOException e) {
            throw new RestException(e.getMessage(), HttpStatus.NOT_FOUND, e);
        }
        return HttpResponseUtil.buildRestResponse(fileDto, HttpStatus.OK.name(), request.getRequestURI());
    }

    private String getFilePathesTest() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String xmlPathName = "filestest";
        URL resource = classLoader.getResource(xmlPathName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! %s".formatted(xmlPathName));
        }
        try {
            return resource.toURI().getPath();
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }
    }
}
