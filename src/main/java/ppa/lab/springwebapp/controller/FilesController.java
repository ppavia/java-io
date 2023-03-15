package ppa.lab.springwebapp.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ppa.lab.springwebapp.exception.RestException;
import ppa.lab.springwebapp.exception.ServiceException;
import ppa.lab.springwebapp.model.dto.FileDto;
import ppa.lab.springwebapp.model.dto.RestResponse;
import ppa.lab.springwebapp.model.mock.FileMock;
import ppa.lab.springwebapp.service.api.FileService;
import ppa.lab.springwebapp.utils.HttpResponseUtil;

@RestController
@RequestMapping("/files")
public class FilesController {

    private final FileService fileService;

    public FilesController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<FileDto>> files(
            HttpServletRequest request
    ) throws RestException {
        FileDto fileDto = null;
        try {
            String rootPath = FileMock.getFilePathsTest();
            fileDto = fileService.loadFiles(rootPath);
        } catch (ServiceException e) {
            throw new RestException(e.getMessage(), HttpStatus.NOT_FOUND, e);
        }
        return HttpResponseUtil.buildRestResponse(fileDto, HttpStatus.OK.name(), request.getRequestURI());
    }
}
