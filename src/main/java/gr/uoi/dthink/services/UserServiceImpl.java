package gr.uoi.dthink.services;

import gr.uoi.dthink.model.Project;
import gr.uoi.dthink.model.User;
import gr.uoi.dthink.model.UserRole;
import gr.uoi.dthink.repos.UserRepository;
import gr.uoi.dthink.repos.UserRoleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserRoleRepository userRoleRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public Set<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    public Set<Project> findAllProjects() {
        User user = this.getLoggedInUser();
        assert user != null;
        return user.getProjects();
    }

    @Override
    public boolean userExists(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        return user != null;
    }

    @Override
    public boolean isAdmin() {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return details.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
    }

    @Override
    public String getLoggedInUserName(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public User getLoggedInUser(){
        return this.findByEmail(this.getLoggedInUserName());
    }

    @Override
    public User save(User user) {
        userRepository.save(user);
        return user;
    }

    @Override
    public User initSave(User user) {
        user.setProfilePic("default.png");
        if(user.getRole()==null)
            user.setRole(userRoleRepository.findByName("USER"));
        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllButMe() {
        int index=0;
        long id = this.getLoggedInUser().getId();
        List<User> users = this.findAll();
        for(User user: users){
            if(user.getId()==id)
                break;
            index++;
        }
        users.remove(index);
        return users;
    }
}
