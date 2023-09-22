package QlikGym.com.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Name field is required !!")
    @Size(min = 2,max = 20,message = "min 2 and max 20 characters are allowed !!")
    private String name;
    @Column(unique = true)
    private String email;
    private String password;
    private String role;
    private boolean enabled;
    private String imageUrl;
    @Column(length = 500)
    private String about;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private List<Trainer> trainers;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", enabled=" + enabled +
                ", imageUrl='" + imageUrl + '\'' +
                ", about='" + about + '\'' +
                '}';
    }
}

