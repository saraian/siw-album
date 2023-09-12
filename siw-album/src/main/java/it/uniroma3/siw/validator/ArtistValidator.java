package it.uniroma3.siw.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import org.springframework.validation.Validator;

@Component
public class ArtistValidator implements Validator {

	@Autowired ArtistRepository artistRepository;
	
	@Override
	public void validate(Object object, Errors errors) {
		Artist artist=(Artist) object;
		if(artist.getArtName()!=null&&artistRepository.existsByArtName(artist.getArtName())) {
			errors.reject("artist.duplicate");
		}
	}

	@Override
	public boolean supports(Class<?> aClass) {
		return Artist.class.equals(aClass);
	}
}
