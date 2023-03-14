package ppa.lab.springwebapp.model.dto;

public class SimplePersonDto {
    private String firstName;

    private String lastName;

    private Long id;

    public SimplePersonDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public SimplePersonDto() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
