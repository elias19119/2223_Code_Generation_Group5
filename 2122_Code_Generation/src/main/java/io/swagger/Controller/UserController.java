package io.swagger.Controller;

import io.swagger.Service.UserService;
import io.swagger.annotations.ApiParam;
import io.swagger.model.*;

import io.swagger.model.DTOs.UpdateUserDTO;
import io.swagger.model.Responses.GetUserResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.exceptions.ApiRequestException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Iterable<GetUserResponseDTO>> getAllUsers(
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @ApiParam(required = false, allowableValues = "WITHOUT_ACCOUNTS, WITH_ACCOUNTS",
                    value = "Available values: WITH_ACCOUNTS, WITHOUT_ACCOUNTS\n" +
                            "Default value: WITH_ACCOUNTS\n")
            @RequestParam(value = "filter", required = false, defaultValue = "WITH_ACCOUNTS") String filter) {

        List<GetUserResponseDTO> filteredUsers;
        try{
            return new ResponseEntity<>(HttpStatus.OK).status(200).body(userService.findUsersByFilter(filter,offset,limit));
        }catch (Exception e){
            throw new ApiRequestException(e.getMessage(),HttpStatus.BAD_REQUEST);

        }
    }



    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> updateUser(@PathVariable("id") UUID userid , @RequestBody UpdateUserDTO updateUserDTO) throws Exception {
        try {
            userService.updateUser(userid,updateUserDTO);
           return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            throw new ApiRequestException(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}")
    //@PreAuthorize("hasAnyRole('ROLE_CUSTOMER')")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetUserResponseDTO> getUserById(@PathVariable("id") UUID id) throws Exception {
        try {
            Optional<GetUserResponseDTO> user = userService.findUserById(id);
            return new ResponseEntity<User>(HttpStatus.FOUND).status(200).body(user.get());

        } catch (Exception e) {
            throw new ApiRequestException(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id) throws Exception {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            throw new ApiRequestException(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/findUsers")
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    public ResponseEntity<Optional<GetUserResponseDTO>> findUserByUsernameAndPhoneNumber(@RequestParam(value = "mobileNumber", required = true) String mobileNumber,
                                                                                         @RequestParam(value = "userName", required = true) String userName) throws Exception {
        try {
            Optional<GetUserResponseDTO> user = userService.findByUserNameAndMobileNumber(userName,mobileNumber);
            return new ResponseEntity<GetUserResponseDTO>(HttpStatus.OK).status(200).body(user);
        } catch (Exception e) {
            throw new ApiRequestException(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }
}


