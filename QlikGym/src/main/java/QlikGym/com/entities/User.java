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

    private int age;
    private String gender;

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

    private double weight;
    private double height;

    // Existing methods...

    public double calculateBMI() {
        if (height <= 0 || weight <= 0) {
            return 0; // Handle invalid input
        }
        return weight / (height * height);
    }

    public String calculateAndDetermineBMIMessage() {
        double bmi = calculateBMI();
        if (age >= 18 && age <= 65) {
            if (gender.equalsIgnoreCase("male")) {
                if (bmi >= 19 && bmi <= 25) {
                    return "BMI lies in the normal range for men.";
                } else if (bmi < 19) {
                    return "Under-nourished for men. Please take care of your diet.";
                } else if (bmi >= 26 && bmi <= 30) {
                    return "Slightly overweight for men. With some exercise and diet control, you can bring it under the normal range.";
                } else {
                    return "Obesity for men. Please take care of your health. Qlik Gym experts can give you good advice at an affordable price.";
                }
            } else if (gender.equalsIgnoreCase("female")) {
                if (bmi >= 17 && bmi <= 23) {
                    return "BMI lies in the normal range for women.";
                } else if (bmi < 17) {
                    return "Under-nourished for women. Please take care of your diet.";
                } else if (bmi >= 24 && bmi <= 28) {
                    return "Slightly overweight for women. With some exercise and diet control, you can bring it under the normal range.";
                } else {
                    return "Obesity for women. Please take care of your health. Qlik Gym experts can give you good advice at an affordable price.";
                }
            }
        }
        return "BMI outside of the specified criteria.";
    }

}

