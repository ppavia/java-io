package ppa.lab.springwebapp.model.mock;

import ppa.spring.domain.bean.SimplePerson;

public class SimplePersonMock {

    public SimplePerson getSimplePerson() {
        SimplePerson person = new SimplePerson();
        person.setFirstName("gérard");
        person.setLastName("bouchard");
        return person;
    }

    public SimplePersonRecord getSimplePersonRecord() {
        SimplePersonRecord simplePerson = new SimplePersonRecord(10L, "Jon", "Doe");
        simplePerson.lastName();
        return simplePerson;
    }
}
