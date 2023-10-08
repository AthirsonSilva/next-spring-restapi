package com.api.nextspring.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;
import org.springframework.hateoas.RepresentationModel;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenreDto extends RepresentationModel<GenreDto> {
	private UUID id;

	@NotEmpty(message = "Name is required")
	@Length(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	@Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	private String name;

	@NotEmpty(message = "Description is required")
	@Length(min = 3, message = "Description must be at least 3 characters")
	@Size(min = 3, message = "Description must be at least 3 characters")
	private String description;

	private List<GameDto> games;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
