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
import io.swagger.model.Responses.GetUserResponseDTO;
import io.swagger.model.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public List<GetUserResponseDTO> findUsersByFilter(String filter, Integer offset, Integer limit) {
        List<User> userList;
        if (filter != null && filter.equals("WITHOUT_ACCOUNTS")) {
            userList = userRepository.findAll().stream()
                    .filter(user -> user.getAccounts().isEmpty())
                    .collect(Collectors.toList());
        } else {
            userList = userRepository.findAll();
        }

        if(offset != null  && limit !=null ){
            int endIndex = Math.min(offset + limit, userList.size());
            if (offset < endIndex) {
                userList = userList.subList(offset, endIndex);
            }
        }
        return userList.stream()
                .map(this::convertToGetUserResponseDTO)
                .collect(Collectors.toList());
    }
    public void updateUser(UUID userid, UpdateUserDTO updateUserDto) throws Exception {
        Optional<User> user = userRepository.findById(userid);
        if (!user.isPresent()) {
            throw new Exception("user was not found");
        } else
        user.get().setFirstName(updateUserDto.getFirstName());
        user.get().setLastName(updateUserDto.getLastName());
        user.get().setUserName(updateUserDto.getUserName());
        user.get().setMobileNumber(updateUserDto.getMobileNumber());
        user.get().setDateOfBirth(updateUserDto.getDateOfBirth());
        user.get().setPassword(updateUserDto.getPassword());
        user.get().setStatus(updateUserDto.getStatus());
        userRepository.save(user.get());

    }

    public Optional<GetUserResponseDTO> findUserById(UUID id) throws Exception {

        Optional<User> user = userRepository.findById(id);
        if(!user.isPresent()){
            throw new Exception("user was not found");
        }
        else
        return user.map(this::convertToGetUserResponseDTO);

    }

    public void deleteUser(UUID id) throws Exception {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new Exception("user was not found");
        } else
            user.get().setStatus(UserStatus.valueOf("DELETED"));
             for (Account a : user.get().getAccounts()) {
                a.setAccountStatus(AccountStatus.CLOSED);
             }
            userRepository.save(user.get());

    }

    public String login(AuthenticationDTO loginDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getUserName(), loginDto.getPassword()));
            return jwtTokenProvider.createToken(loginDto.getUserName(), userRepository.findByUserName(loginDto.getUserName()).get().getRoles());
        } catch (Exception ae) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ae.getMessage());
        }
    }

    private GetUserResponseDTO convertToGetUserResponseDTO(User user) {
        GetUserResponseDTO responseDTO = new GetUserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setUserName(user.getUserName());
        responseDTO.setMobileNumber(user.getMobileNumber());
        responseDTO.setFirstName(user.getFirstName());
        responseDTO.setLastName(user.getLastName());
        responseDTO.setDateOfBirth(user.getDateOfBirth());
        responseDTO.setRoles(user.getRoles());
        responseDTO.setStatus(user.getStatus());
        responseDTO.setAccounts(user.getAccounts());

        return responseDTO;
    }


    public Optional<GetUserResponseDTO> findByUserNameAndMobileNumber(String name, String mobileNumber) throws Exception {

        Optional<User> user =  userRepository.findByuserNameAndMobileNumber(name, mobileNumber);
        if (!user.isPresent()){
            throw  new Exception("user was not found");
        }
        else
            return user.map(this::convertToGetUserResponseDTO);

    }

}