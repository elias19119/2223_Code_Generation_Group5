package io.swagger.Service;

import io.swagger.Repository.UserRepository;
import io.swagger.Security.JwtTokenProvider;
import io.swagger.model.Account;
import io.swagger.model.DTOs.AuthenticationDTO;
import io.swagger.model.DTOs.CreateUserDTO;
import io.swagger.model.DTOs.UpdateUserDTO;
import io.swagger.model.Enums.AccountStatus;
import io.swagger.model.Enums.UserRole;
import io.swagger.model.Enums.UserStatus;
import io.swagger.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {


    private UserRepository userRepository;
    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationManager authenticationManager;

    public User creatUser(CreateUserDTO userDto) {
        User user = new User();
        user.setMobileNumber(userDto.getMobileNumber());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setRoles(UserRole.CUSTOMER);
        user.setStatus(UserStatus.UNDER_REVIEW); // keep in mind that the status of the user should be changed to active
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUserName(userDto.getUserName());
        user.setPassword(userDto.getPassword());
        userRepository.save(user);
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllUsersWithNoAccount() {
        return userRepository.findAll().stream().filter(user -> user.getAccounts().size() == 0).collect(Collectors.toList());
    }

    public void updateUser(UUID userid, UpdateUserDTO updateUserDto) throws Exception {
        Optional<User> user = userRepository.findById(userid);
        if (user.isPresent()) {
            user.get().setFirstName(updateUserDto.getFirstName());
            user.get().setLastName(updateUserDto.getLastName());
            user.get().setUserName(updateUserDto.getUserName());
            user.get().setMobileNumber(updateUserDto.getMobileNumber());
            user.get().setDateOfBirth(updateUserDto.getDateOfBirth());
            user.get().setPassword(updateUserDto.getPassword());
            user.get().setRoles(updateUserDto.getRoles());
            user.get().setStatus(updateUserDto.getStatus());
            userRepository.save(user.get());
        } else
            throw new Exception("user was not found");

    }

    public Optional<User> findUserById(UUID id) throws Exception {

        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            return user;
        }
        else
            throw new Exception("user was not found");

    }

    public Optional<User> findUserByUserName(String name) throws Exception {

        return Optional.ofNullable(userRepository.findByUserName(name)
                .orElseThrow(() -> new Exception("User not found with username: " + name)));
    }

    public void deleteUser(UUID id) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setStatus(UserStatus.valueOf("DELETED"));
            for (Account a : user.get().getAccounts()) {
               a.setAccountStatus(AccountStatus.CLOSED);
            }
            userRepository.save(user.get());
        } else
            throw new Exception("user was not found");
    }

    public String login(AuthenticationDTO loginDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUserName(), loginDto.getPassword()));
            return jwtTokenProvider.createToken(loginDto.getUserName(), userRepository.findByUserName(loginDto.getUserName()).get().getRoles());
        } catch (Exception ae) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ae.getMessage());
        }
    }

    public List<User> getUsersByLimitAndOffset(int offset, int limit){
        List<User> allAccounts = userRepository.findAll(); // Fetch all users from the data source
        List<User> users = new ArrayList<>();

        int endIndex = Math.min(offset + limit, allAccounts.size());
        if (offset < endIndex) {
            users = allAccounts.subList(offset, endIndex);
        }
        return users;
    }

    /*
    public Optional<User> findUserByUserNameAndPhoneNumber(String name, String mobileNumber) {
        return userRepository.findByFirstNameAndMobileNumber(name, mobileNumber);
    }


     */
}