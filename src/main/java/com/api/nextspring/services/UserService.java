package com.api.nextspring.services;

import java.util.Set;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import com.api.nextspring.dto.ChangePasswordDto;
import com.api.nextspring.dto.UserDto;
import com.api.nextspring.dto.optionals.OptionalUserDto;
import com.api.nextspring.entity.RoleEntity;

import jakarta.servlet.http.HttpServletResponse;

/**
 * This interface defines the methods that can be used to interact with the User
 * entity. It defines methods for retrieving, creating, updating and deleting
 * users.
 * 
 * @author Athirson Silva
 * @see com.api.nextspring.services.impl.UserServiceImpl
 */
public interface UserService {

	/**
	 * Returns the current user based on the provided token.
	 * 
	 * @param token The token used to authenticate the user.
	 * @return A UserDto object representing the current user.
	 */
	public UserDto getCurrentUser(String token);

	/**
	 * Updates the current user based on the provided token and request.
	 * 
	 * @param token   The token used to authenticate the user.
	 * @param request An OptionalUserDto object containing the updated user
	 *                information.
	 * @return A UserDto object representing the updated user.
	 */
	public UserDto updateCurrentUser(String token, OptionalUserDto request);

	/**
	 * Deletes the current user based on the provided token.
	 * 
	 * @param token The token used to authenticate the user.
	 */
	public void deleteCurrentUser(String token);

	/**
	 * Changes the current user's password based on the provided token and request.
	 * 
	 * @param token   The token used to authenticate the user.
	 * @param request A ChangePasswordDto object containing the new password.
	 * @return A UserDto object representing the updated user.
	 */
	public UserDto changeCurrentUserPassword(String token, ChangePasswordDto request);

	/**
	 * Returns the roles associated with the current user based on the provided
	 * token.
	 * 
	 * @param token The token used to authenticate the user.
	 * @return A Set of RoleEntity objects representing the roles associated with
	 *         the current user.
	 */
	public Set<RoleEntity> getUserRole(String token);

	/**
	 * Uploads a photo for the current user based on the provided token and file.
	 * 
	 * @param token The token used to authenticate the user.
	 * @param file  The MultipartFile object containing the photo to be uploaded.
	 * @return A UserDto object representing the updated user.
	 */
	public UserDto uploadPhoto(String token, MultipartFile file);

	/**
	 * Downloads the photo associated with the provided user ID.
	 * 
	 * @param id The UUID of the user whose photo is to be downloaded.
	 * @return An InputStreamResource object representing the photo.
	 */
	public InputStreamResource downloadPhotoByUser(UUID id);

	/**
	 * Exports user data to an Excel file and sends it as a response.
	 * 
	 * @param response The HttpServletResponse object used to send the response.
	 */
	public void exportToExcel(HttpServletResponse response);

	/**
	 * Resets the current user's password to a default value based on the provided
	 * token.
	 * 
	 * @param token The token used to authenticate the user.
	 */
	public void resetCurrentUserPassword(String token);
}
