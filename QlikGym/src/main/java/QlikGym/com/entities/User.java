package QlikGym.com.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @NotBlank(message = "name should not be blank")
    @Size(min = 2,max = 20, message = "minimum 2 and maximum 20 characters are allowed")
    private String firstName;
    @NotBlank(message = "name should not be blank")
    @Size(min = 2,max = 20, message = "minimum 2 and maximum 20 characters are allowed")
    private String lastName;
    @NotEmpty(message = "Phone number is required")
    @Size(max = 20, message = "Phone number is too long")
    private String phoneNumber;
    @Column(unique = true)
    private String email;
    @Size(min= 5, message = "password must be of minimum 5 characters")
    private String password;

}

