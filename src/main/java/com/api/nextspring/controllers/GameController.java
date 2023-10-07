package com.api.nextspring.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.nextspring.dto.GameDto;
import com.api.nextspring.dto.Response;
import com.api.nextspring.dto.optionals.OptionalGameDto;
import com.api.nextspring.exceptions.RestApiException;
import com.api.nextspring.services.GameService;
import com.api.nextspring.services.LinkingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Tag(name = "Game", description = "Game endpoint for creating, getting, updating and deleting games")
public class GameController {
	private final GameService gameServices;
	private final LinkingService linkingService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create a new game endpoint")
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses({
			@ApiResponse(responseCode = "400", description = "Bad Request, the user did not send all required data", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "401", description = "Unauthorized, the user is not logged in or does not have access permition", content = @Content(mediaType = "application/json"))
	})
	public ResponseEntity<Response<String, GameDto>> createGame(@Valid @RequestBody GameDto request,
			HttpServletRequest servletRequest) {
		GameDto gameDto = gameServices.create(request);

		gameDto = linkingService.addHateoasLinksToClass(servletRequest, "games", gameDto);

		Response<String, GameDto> response = new Response<>("Game created successfully!", gameDto);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/search")
	@Operation(summary = "Search a game by name endpoint")
	@ResponseStatus(HttpStatus.OK)
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses({
			@ApiResponse(responseCode = "400", description = "Bad Request, the user did not send all required data", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "401", description = "Unauthorized, the user is not logged in or does not have access permition", content = @Content(mediaType = "application/json"))
	})
	public ResponseEntity<Response<String, List<GameDto>>> getGames(
			@RequestParam(value = "query", defaultValue = "") String query, HttpServletRequest servletRequest) {
		if (query.isEmpty() || query.isBlank()) {
			throw new RestApiException(HttpStatus.BAD_REQUEST, "Query parameter with the game information is required!");
		}

		List<GameDto> gameList = gameServices.searchByKeyword(query);

		if (gameList.size() == 0) {
			Response<String, List<GameDto>> response = new Response<>("No game found with given information's!",
					gameList);

			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		for (GameDto gameDto : gameList) {
			gameDto = linkingService.addHateoasLinksToClass(servletRequest, "games", gameDto);
		}

		Response<String, List<GameDto>> response = new Response<>("Games found with given information's!", gameList);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping
	@Operation(summary = "Get all games endpoint")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponses({
			@ApiResponse(responseCode = "400", description = "Bad Request, the user did not send all required data", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "401", description = "Unauthorized, the user is not logged in or does not have access permition", content = @Content(mediaType = "application/json"))
	})
	public ResponseEntity<Response<String, List<GameDto>>> getAllGames(HttpServletRequest servletRequest) {
		List<GameDto> gameList = gameServices.findAll();

		for (GameDto gameDto : gameList) {
			gameDto = linkingService.addHateoasLinksToClass(servletRequest, "games", gameDto);
		}

		Response<String, List<GameDto>> response = new Response<>("All games found!", gameList);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a game by id endpoint")
	@ResponseStatus(HttpStatus.OK)
	@ApiResponses({
			@ApiResponse(responseCode = "400", description = "Bad Request, the user did not send all required data", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "401", description = "Unauthorized, the user is not logged in or does not have access permition", content = @Content(mediaType = "application/json"))
	})
	public ResponseEntity<Response<String, GameDto>> getGameById(@PathVariable(value = "id") UUID id,
			HttpServletRequest servletRequest) {
		GameDto gameDto = gameServices.findByID(id);

		gameDto = linkingService.addHateoasLinksToClass(servletRequest, "games", gameDto);

		Response<String, GameDto> response = new Response<>("Game found with given id!", gameDto);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PatchMapping("/{id}")
	@Operation(summary = "Update a game by id endpoint")
	@ResponseStatus(HttpStatus.OK)
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses({
			@ApiResponse(responseCode = "400", description = "Bad Request, the user did not send all required data", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "401", description = "Unauthorized, the user is not logged in or does not have access permition", content = @Content(mediaType = "application/json"))
	})
	public ResponseEntity<Response<String, GameDto>> updateGame(@PathVariable(value = "id", required = true) UUID id,
			@RequestBody OptionalGameDto request, HttpServletRequest servletRequest) {
		GameDto gameDto = gameServices.updateById(id, request);

		gameDto = linkingService.addHateoasLinksToClass(servletRequest, "games", gameDto);

		Response<String, GameDto> response = new Response<>("Game updated successfully!", gameDto);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a game by id endpoint")
	@ResponseStatus(HttpStatus.OK)
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses({
			@ApiResponse(responseCode = "400", description = "Bad Request, the user did not send all required data", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "401", description = "Unauthorized, the user is not logged in or does not have access permition", content = @Content(mediaType = "application/json"))
	})
	public ResponseEntity<HashMap<String, String>> deleteGame(@PathVariable(value = "id", required = true) UUID id,
			HttpServletRequest request) {
		gameServices.deleteById(id);

		HashMap<String, String> response = new HashMap<>();

		response.put("message", "Game deleted successfully!");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/uploadPhoto/{id}")
	@Operation(summary = "Upload a game photo by id endpoint")
	@ResponseStatus(HttpStatus.CREATED)
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses({
			@ApiResponse(responseCode = "400", description = "Bad Request, the user did not send all required data", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "401", description = "Unauthorized, the user is not logged in or does not have access permition", content = @Content(mediaType = "application/json"))
	})
	public ResponseEntity<Response<String, GameDto>> uploadGamePhoto(
			@PathVariable(value = "id", required = true) UUID id,
			@RequestParam(value = "file", required = true) MultipartFile file, HttpServletRequest servletRequest) {
		GameDto gameDto = gameServices.uploadPhoto(id, file);

		gameDto = linkingService.addHateoasLinksToClass(servletRequest, "games", gameDto);

		Response<String, GameDto> response = new Response<>("Game photo uploaded successfully!", gameDto);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/downloadPhoto/{id}")
	@ResponseBody
	@Operation(summary = "Download a game photo by id endpoint")
	@ResponseStatus(HttpStatus.OK)
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses({
			@ApiResponse(responseCode = "400", description = "Bad Request, the user did not send all required data", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "401", description = "Unauthorized, the user is not logged in or does not have access permition", content = @Content(mediaType = "application/json"))
	})
	public ResponseEntity<InputStreamResource> getImageDynamicType(@PathVariable("id") UUID id) {
		InputStreamResource image = gameServices.downloadPhotoByUser(id);

		return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
	}

	@GetMapping(value = "/export/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	@Operation(summary = "Export all games in the database to excel endpoint")
	@ResponseStatus(HttpStatus.OK)
	@SecurityRequirement(name = "JWT Authentication")
	@ApiResponses({
			@ApiResponse(responseCode = "400", description = "Bad Request, the user did not send all required data", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "401", description = "Unauthorized, the user is not logged in or does not have access permition", content = @Content(mediaType = "application/json"))
	})
	public void exportToExcel(HttpServletResponse response) {
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=games_" + currentDateTime + ".xlsx";
		response.setHeader(headerKey, headerValue);

		gameServices.exportToExcel(response);
	}
}
