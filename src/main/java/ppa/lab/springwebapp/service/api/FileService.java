package ppa.lab.springwebapp.service.api;

import ppa.lab.springwebapp.exception.ServiceException;
import ppa.lab.springwebapp.model.dto.FileDto;

public interface FileService {

    FileDto loadFiles (String rootPath) throws ServiceException;
}
