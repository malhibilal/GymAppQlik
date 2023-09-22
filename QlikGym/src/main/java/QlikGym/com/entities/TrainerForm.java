package QlikGym.com.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerForm {
    private Long id;
    private String name;
    private String description;
    private String contactInfo;
    private double hourlyRate;
    private boolean available;
}
