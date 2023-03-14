package ppa.lab.springwebapp.service.api;

import ppa.lab.springwebapp.exception.ServiceException;
import ppa.lab.springwebapp.model.dto.SimplePersonDto;

import java.util.List;
import java.util.Optional;

public interface SimplePersonService {

    Optional<SimplePersonDto> getSimplePerson(Long id) throws ServiceException;

    SimplePersonDto getSimplePerson(String firstName, String lastName) throws ServiceException;

    List<SimplePersonDto> getSimplePersons(String firstName) throws ServiceException;
}