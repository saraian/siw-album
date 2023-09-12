package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.repository.AlbumRepository;
import org.springframework.validation.Validator;

@Component
public class AlbumValidator implements Validator {

	@Autowired AlbumRepository movieRepository;
	
	@Override
	public void validate(Object object, Errors errors) {
		Album album=(Album) object;
		if(album.getTitle()!=null&&album.getYear()!=null&&movieRepository.existsByTitleAndYear(album.getTitle(), album.getYear())) {
			errors.reject("album.duplicate");
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Album.class.equals(aClass);
	}
}
