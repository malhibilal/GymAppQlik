package QlikGym.com.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Appointment {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private LocalDateTime dateTime;
        private String location;
        private String notes;
        @ManyToOne
        private User user;

        @ManyToOne
        private Trainer trainer;


    }
