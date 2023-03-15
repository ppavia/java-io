package ppa.lab.springwebapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ppa.lab.springwebapp.exception.RestException;
import ppa.lab.springwebapp.exception.ServiceException;
import ppa.lab.springwebapp.model.dto.FileDto;
import ppa.lab.springwebapp.model.dto.RestResponse;
import ppa.lab.springwebapp.model.dto.SimplePersonDto;
import ppa.lab.springwebapp.service.api.SimplePersonService;
import ppa.lab.springwebapp.tooling.xml.XmlFileParser;
import ppa.lab.springwebapp.utils.HttpResponseUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FilesController {
    private static final Logger LOG = LoggerFactory.getLogger(FilesController.class);
    private XmlFileParser xmlFileParser;

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
            String rootPath = System.getProperty("user.home") + "/Pictures";
            paths = xmlFileParser.loadXmlFiles(rootPath);
            fileDto = new FileDto(rootPath, paths);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RestException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return HttpResponseUtil.buildRestResponse(fileDto, HttpStatus.OK.name(), request.getRequestURI(), "parse files");
    }
}
