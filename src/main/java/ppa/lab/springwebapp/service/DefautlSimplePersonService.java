package ppa.lab.springwebapp.service;

import org.springframework.stereotype.Service;
import ppa.lab.springwebapp.exception.ServiceException;
import ppa.lab.springwebapp.model.dto.SimplePersonDto;
import ppa.lab.springwebapp.model.repository.SimplePersonRepository;
import ppa.lab.springwebapp.service.api.SimplePersonService;
import ppa.spring.domain.bean.SimplePerson;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DefautlSimplePersonService implements SimplePersonService {
    private SimplePersonRepository simplePersonRepository;

    public DefautlSimplePersonService(SimplePersonRepository simplePersonRepository) {
        this.simplePersonRepository = simplePersonRepository;
    }

    @Override public Optional<SimplePersonDto> getSimplePerson(Long id) {

        Optional<SimplePerson> simplePerson = simplePersonRepository.findById(id);
        if(simplePerson.isPresent()) {
            return Optional.of(mapSimplePerson(simplePerson.get()));
        }
        return Optional.empty();
    }

    @Override public SimplePersonDto getSimplePerson(String firstName, String lastName) throws ServiceException {
        SimplePerson simplePerson = simplePersonRepository.findByFirstNameAndLastName(firstName, lastName);
        if(simplePerson == null) {
            throw new ServiceException(String.format("la personne [%s,%s] n'a pas été trouvée.", firstName, lastName));
        }

        return mapSimplePerson(simplePerson);
    }
    @Override public List<SimplePersonDto> getSimplePersons(String firstName) throws ServiceException {
        List<SimplePerson> simplePersons = simplePersonRepository.findByFirstName(firstName);
        return simplePersons.stream()
                .map(simplePerson -> mapSimplePerson(simplePerson))
                .collect(Collectors.toList());
    }


    private SimplePersonDto mapSimplePerson (SimplePerson simplePerson) {
        SimplePersonDto simplePersonDto = new SimplePersonDto();

        simplePersonDto.setId(simplePerson.getId());
        simplePersonDto.setFirstName(simplePerson.getFirstName());
        simplePersonDto.setLastName(simplePerson.getLastName());

        return simplePersonDto;
    }
}
