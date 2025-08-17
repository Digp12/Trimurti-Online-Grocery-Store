package com.example.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.config.CustomUser;
import com.example.model.UserDtls;
import com.example.repository.UserRepository;
import com.example.service.UserService;
import com.example.util.AppConstant;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDtls saveUser(UserDtls user) {
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);

		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDtls saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public UserDtls getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<UserDtls> getUsers(String role) {
		return userRepository.findByRole(role);
	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {

		Optional<UserDtls> findByuser = userRepository.findById(id);

		if (findByuser.isPresent()) {
			UserDtls userDtls = findByuser.get();
			userDtls.setIsEnable(status);
			userRepository.save(userDtls);
			return true;
		}

		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDtls user) {
		int attempt = user.getFailedAttempt() + 1;
		user.setFailedAttempt(attempt);
		userRepository.save(user);
	}

	@Override
	public void userAccountLock(UserDtls user) {
		user.setAccountNonLocked(false);
		user.setLockTime(new Date());
		userRepository.save(user);
	}

	@Override
	public boolean unlockAccountTimeExpired(UserDtls user) {

		long lockTime = user.getLockTime().getTime();
		long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

		long currentTime = System.currentTimeMillis();

		if (unLockTime < currentTime) {
			user.setAccountNonLocked(true);
			user.setFailedAttempt(0);
			user.setLockTime(null);
			userRepository.save(user);
			return true;
		}

		return false;
	}

	@Override
	public void resetAttempt(int userId) {

	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDtls findByEmail = userRepository.findByEmail(email);
		findByEmail.setResetToken(resetToken);
		userRepository.save(findByEmail);
	}

	@Override
	public UserDtls getUserByToken(String token) {
		return userRepository.findByResetToken(token);
	}

	@Override
	public UserDtls updateUser(UserDtls user) {
		return userRepository.save(user);
	}

	@Override
	public UserDtls updateUserProfile(UserDtls user, MultipartFile img) {

		Optional<UserDtls> optionalUser = userRepository.findById(user.getId());
		if (optionalUser.isEmpty()) {
			throw new RuntimeException("User not found with ID: " + user.getId());
		}

		UserDtls dbUser = optionalUser.get();

		String imageName = img.isEmpty() ? dbUser.getProfileImage() : img.getOriginalFilename();
		dbUser.setProfileImage(imageName);

		dbUser.setName(user.getName());
		dbUser.setMobileNumber(user.getMobileNumber());
		dbUser.setAddress(user.getAddress());
		dbUser.setCity(user.getCity());
		dbUser.setState(user.getState());
		dbUser.setPincode(user.getPincode());

		UserDtls updatedUser = userRepository.save(dbUser);

		if (!img.isEmpty()) {
			try {
				String uploadDir = new File("src/main/resources/static/img/profile_img").getAbsolutePath();

				File folder = new File(uploadDir);
				if (!folder.exists()) {
					folder.mkdirs();
				}

				Path path = Paths.get(uploadDir + File.separator + img.getOriginalFilename());
				Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return updatedUser;
	}

	@Override
	public UserDtls getLoggedInUserDetails() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication.getPrincipal().equals("anonymousUser")) {
			return null;
		}

		CustomUser customUser = (CustomUser) authentication.getPrincipal();
		String email = customUser.getUsername();

		return getUserByEmail(email);
	}

	@Override
	public UserDtls saveAdmin(UserDtls user) {
		user.setRole("ROLE_ADMIN");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFailedAttempt(0);

		String encodePassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodePassword);
		UserDtls saveUser = userRepository.save(user);
		return saveUser;
	}

	@Override
	public Boolean existsEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public long getUserCount() {
		return userRepository.count(); // Count all users in the user table
	}

	@Override
	public long getEnabledUserCount(Boolean isEnable) {
		return userRepository.countByIsEnable(true);
	}

}
