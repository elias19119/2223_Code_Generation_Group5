package io.swagger.Repository;
import io.swagger.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUserName(String userName);
    Optional<User> findByuserNameAndMobileNumber(String userName, String mobileNumber);
    Optional<User> findById(UUID userid);

}