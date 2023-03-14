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
import ppa.lab.springwebapp.model.dto.RestResponse;
import ppa.lab.springwebapp.model.dto.SimplePersonDto;
import ppa.lab.springwebapp.service.api.SimplePersonService;
import ppa.lab.springwebapp.utils.HttpResponseUtil;

import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {
    private static final Logger LOG = LoggerFactory.getLogger(PersonController.class);
    private SimplePersonService simplePersonService;

    public PersonController(SimplePersonService simplePersonService) {
        this.simplePersonService = simplePersonService;
    }

    @GetMapping(value = "/{firstName}/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<SimplePersonDto>> person(
            HttpServletRequest request
            , @PathVariable("firstName") String firstName
            , @PathVariable("lastName") String lastName
    ) throws RestException {
        SimplePersonDto simplePerson = null;
        try {
            simplePerson = simplePersonService.getSimplePerson(firstName, lastName);
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
            throw new RestException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return HttpResponseUtil.buildRestResponse(simplePerson, HttpStatus.OK.name(), request.getRequestURI(), "find person");
    }

    @GetMapping(value = "/{firstName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RestResponse<List<SimplePersonDto>>> person(
            HttpServletRequest request
            , @PathVariable("firstName") String firstName
    ) throws RestException {
        List<SimplePersonDto> simplePersons = null;
        try {
            simplePersons = simplePersonService.getSimplePersons(firstName);
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
            throw new RestException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return HttpResponseUtil.buildRestResponse(simplePersons, HttpStatus.OK.name(), request.getRequestURI(), "find person");
    }

    @GetMapping(value = "/AV", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimplePersonDto> person(
            HttpServletRequest request
    ) throws RestException {
       String firstName = "Adrien";
       String lastName = "Vergnaud";
        SimplePersonDto simplePerson = null;
        try {
            simplePerson = simplePersonService.getSimplePerson(firstName, lastName);
        } catch (ServiceException e) {
            LOG.error(e.getMessage(), e);
            throw new RestException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        return HttpResponseUtil.buildRestResponse(simplePerson);
    }

    @GetMapping("/400")
    public ResponseEntity<Void> noPerson() throws RestException {
        throw new RestException("a bad request", HttpStatus.NOT_FOUND);
    }
}
