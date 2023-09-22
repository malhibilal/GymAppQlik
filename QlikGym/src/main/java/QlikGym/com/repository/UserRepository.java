package QlikGym.com.repository;

import QlikGym.com.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    public User findByEmail(String email);

    @Query("select u from User u where u.email = :username")
    User getUserByUserName(String username);

}
