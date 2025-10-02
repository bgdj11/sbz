package sbnz.integracija.example.dto;

public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private boolean isAdmin;

    public UserDTO() {}

    public UserDTO(Long id, String firstName, String lastName, String email, String city, boolean isAdmin) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.isAdmin = isAdmin;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}