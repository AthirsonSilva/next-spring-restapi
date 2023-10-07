package com.api.nextspring.services.impl;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.api.nextspring.dto.GenreDto;
import com.api.nextspring.dto.optionals.OptionalGenreDto;
import com.api.nextspring.entity.GenreEntity;
import com.api.nextspring.enums.EntityOptions;
import com.api.nextspring.exceptions.RestApiException;
import com.api.nextspring.repositories.GenreRepository;
import com.api.nextspring.services.GenreService;
import com.api.nextspring.utils.ExcelUtils;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
	private final GenreRepository genreRepository;
	private final ModelMapper modelMapper;
	private final ExcelUtils excelUtils;

	public List<GenreDto> findAll(Integer page, Integer size, String sort, String direction) {
		Pageable pageable = PageRequest
				.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));

		List<GenreEntity> genreEntities = genreRepository.findAll(pageable).toList();

		if (genreEntities.isEmpty())
			throw new RestApiException(HttpStatus.NOT_FOUND, "No genres found!");

		return genreEntities.stream().map(
				genreEntity -> modelMapper.map(genreEntity, GenreDto.class)).toList();
	}

	public GenreDto findByID(UUID id) {
		GenreEntity genreEntity = genreRepository.findById(id).orElseThrow(
				() -> new RestApiException(HttpStatus.BAD_REQUEST, "Genre not found!"));

		return modelMapper.map(genreEntity, GenreDto.class);
	}

	public GenreDto create(GenreDto request) {
		if (genreRepository.existsByName(request.getName()))
			throw new RestApiException(HttpStatus.BAD_REQUEST, "Genre already exists!");

		GenreEntity genreEntity = GenreEntity
				.builder()
				.name(request.getName())
				.description(request.getDescription())
				.build();

		return modelMapper.map(genreRepository.save(genreEntity), GenreDto.class);
	}

	public void deleteByID(UUID id) {
		GenreEntity genreEntity = genreRepository.findById(id).orElseThrow(
				() -> new RestApiException(HttpStatus.BAD_REQUEST, "Genre not found!"));

		genreRepository.delete(genreEntity);
	}

	public GenreDto updateByID(UUID id, OptionalGenreDto request) {
		GenreEntity genreEntity = genreRepository.findById(id).orElseThrow(
				() -> new RestApiException(HttpStatus.BAD_REQUEST, "Genre not found!"));

		if (request.getName() != null)
			genreEntity.setName(request.getName());

		if (request.getDescription() != null)
			genreEntity.setDescription(request.getDescription());

		return modelMapper.map(genreRepository.save(genreEntity), GenreDto.class);
	}

	public List<GenreDto> searchByKeyword(String query, Integer page, Integer size, String sort, String direction) {
		Pageable pageable = PageRequest
				.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));

		List<GenreEntity> genreEntities = genreRepository.searchGenreEntities(query, pageable).toList();

		if (genreEntities.isEmpty())
			throw new RestApiException(HttpStatus.NOT_FOUND, "No genres found with given keyword!");

		return genreEntities.stream().map(
				genreEntity -> modelMapper.map(genreEntity, GenreDto.class)).toList();
	}

	@Override
	public void exportToExcel(HttpServletResponse response) {
		List<GenreEntity> entityList = genreRepository.findAll();

		try {
			excelUtils.export(response, entityList, EntityOptions.GENRE);
		} catch (IllegalArgumentException e) {
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Error occurred while exporting data to Excel file: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new RestApiException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Error occurred while exporting data to Excel file: " + e.getMessage());
		}
	}
}
