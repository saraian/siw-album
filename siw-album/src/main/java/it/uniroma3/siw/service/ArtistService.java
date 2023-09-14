package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;

@Service
public class ArtistService {
	@Autowired ArtistRepository artistRepository;
	
	public Artist findArtistById(Long id) {
		Artist artist=this.artistRepository.findById(id).get();
		return artist;
	}
}
