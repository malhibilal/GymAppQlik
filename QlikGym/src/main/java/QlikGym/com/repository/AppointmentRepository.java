package QlikGym.com.repository;

import QlikGym.com.entities.Appointment;
import QlikGym.com.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

  //  Page<Appointment> findAppointmentsByUser(Long id, Pageable pageable);

    Page<Appointment> findAppointmentsByUser(User user, Pageable pageable);
}
