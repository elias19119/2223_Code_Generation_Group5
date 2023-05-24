package io.swagger.Controller;

import io.swagger.Service.UserService;
import io.swagger.annotations.ApiParam;
import io.swagger.model.*;

import io.swagger.model.DTOs.UpdateUserDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.exceptions.ApiRequestException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/users")

public class UserController {
    private final UserService userService;
    private final HttpServletRequest request;


    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_BANK','ROLE_EMPLOYEE')")
    // TODO: 5/22/2023 this method as of now cannot return all users, if the filter is not selected. It is suppose to return all users, if no option is selected.
    public ResponseEntity<Iterable<User>> getAllUsers(@RequestParam(value = "offset", required = false) Integer offset,
                                                      @RequestParam(value = "limit", required = false) Integer limit, @ApiParam(required = false, allowableValues = "WITHOUT_ACCOUNTS, WITH_ACCOUNTS", value = "Available values : WITH_ACCOUNT, WITHOUT_ACCOUNT\n" +
            "\n" +
            "Default value : WITH_ACCOUNT\n") String filter) {
        List<User> filteredUsers = new ArrayList<>();
        if (request.getParameter("filter") != null) {
            if (filter.equals("WITH_ACCOUNTS")) {
                filteredUsers.addAll(userService.getAllUsers());
            } else if (filter.equals("WITHOUT_ACCOUNTS")) {
                filteredUsers.addAll(userService.getAllUsersWithNoAccount());
            }
        }

        if(request.getParameter("offset") != null && request.getParameter("limit") != null){
            filteredUsers.addAll(userService.getUsersByLimitAndOffset(offset,limit));
        }

        return new ResponseEntity<>(HttpStatus.OK).status(200).body(filteredUsers);
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
    public ResponseEntity<User> getUserById(@PathVariable("id") UUID id) throws Exception {
        try {
            Optional<User> user = userService.findUserById(id);
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


}


