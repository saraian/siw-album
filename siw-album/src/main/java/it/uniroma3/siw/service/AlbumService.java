package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.repository.AlbumRepository;

@Service
public class AlbumService {
	@Autowired AlbumRepository albumRepository;
	
	public Album findAlbumById(Long id) {
		Album album=this.albumRepository.findById(id).get();
		return album;
	}
}
