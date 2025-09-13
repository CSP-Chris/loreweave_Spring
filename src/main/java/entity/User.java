package entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;

// https://spring.io/guides/gs/accessing-data-jpa

@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;


    @OneToOne(mappedBy = "user") // One character per player
    @JsonManagedReference
    public Character character;

    protected User() {}

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    @Override
    public String toString() {
        return String.format(
                "User[id=%d, firstName='%s', lastName='%s', email='%s']",
                id, firstName, lastName, email);
    }

}