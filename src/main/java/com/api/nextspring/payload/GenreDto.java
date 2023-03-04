package com.api.nextspring.payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class GenreDto {
	@NotEmpty(message = "Name is required")
	@Length(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
	private String name;
	@NotEmpty(message = "Description is required")
	@Length(min = 3, message = "Description must be at least 3 characters")
	private String description;
}
