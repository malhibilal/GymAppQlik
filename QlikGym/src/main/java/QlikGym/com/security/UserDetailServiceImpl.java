package QlikGym.com.security;

import QlikGym.com.entities.User;
import QlikGym.com.exception.UserNotFoundException;
import QlikGym.com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    // this method will load the user
    @Override
    public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
        User user=userRepository.findByEmail(email);
        if(user==null){
            throw new UserNotFoundException("User not found !!");
        }
        // we have to make an object of CustomUserDetails
        CustomUserDetails customUserDetails=new CustomUserDetails(user);
        return customUserDetails;
    }

}
