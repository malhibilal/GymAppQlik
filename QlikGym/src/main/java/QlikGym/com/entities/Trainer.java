package QlikGym.com.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
    private String contactInfo;
    private double hourlyRate;
    private boolean available;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "created_by") // This specifies the foreign key column name
    @JsonIgnore
    private User createdBy;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Appointment> appointments = new ArrayList<>();


    @Override
    public String toString() {
        return "Trainer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", contactInfo='" + contactInfo + '\'' +
                ", hourlyRate=" + hourlyRate +
                ", available=" + available +
                '}';
    }
}

