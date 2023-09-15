package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.User;

/**
 * @author BT
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	/**
	 * @param username
	 * @return
	 */
	Optional<User> findByUsername(String username);
}
