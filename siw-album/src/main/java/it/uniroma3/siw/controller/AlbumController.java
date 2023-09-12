package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.AlbumRepository;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.validator.AlbumValidator;
import jakarta.validation.Valid;

@Controller
public class AlbumController {
	
	@Autowired AlbumValidator albumValidator;
	@Autowired AlbumRepository albumRepository;
	@Autowired ArtistRepository artistRepository;
	
	//restituisce una pagina contenente la form per inserire i dati di un nuovo album
	@GetMapping("/formNewAlbum")
	public String formNewAlbum(Model model) {
		model.addAttribute("album", new Album());
		return "formNewAlbum.html";
	}
	
	//se il film esiste mostra una pagina di errore, altrimenti salva il film e mostra una pagina che lo contiene
	@PostMapping("/albums")
	public String newAlbum(@Valid @ModelAttribute("album") Album album, BindingResult bindingResult, Model model) {
		this.albumValidator.validate(album, bindingResult);
		if(!bindingResult.hasErrors()) {
			this.albumRepository.save(album);
			model.addAttribute("album", album);
			return "album.html";
		}
		else {
			model.addAttribute("messaggioErrore", "Questo album esiste già");
			return "formNewAlbum.html";
		}
	}
	
	//restituisce una pagina con i dati del film avente l'id corrispondente
	@GetMapping("/albums/{id}")
	public String getAlbum(@PathVariable("id") Long id, Model model) {
		model.addAttribute("album", this.albumRepository.findById(id).get());
		return "album.html";
	}
	
	//mostra tutti i film
	@GetMapping("/albums")
	public String showAlbums(Model model) {
		model.addAttribute("albums", this.albumRepository.findAll());
		return "albums.html";
	}
	
	//restituisce una pagina che permette di cercare i film in base all'anno
	@GetMapping("/formSearchAlbums")
	public String formSearchAlbums(Model model) {
		return "formSearchAlbums.html";
	}

	//porta alla pagina in cui selezionare il film da modificare
	@GetMapping("/manageAlbums")
	public String manageAlbums(Model model) {
		model.addAttribute("albums", this.albumRepository.findAll());
		return "manageAlbums.html";
	}
	
	//in questa pagina si può scegliere quale modifica fare
	@GetMapping("/formUpdateAlbum/{id}")
	public String formUpdateAlbum(@PathVariable("id") Long idAlbum, Model model) {
		model.addAttribute("album", this.albumRepository.findById(idAlbum).get());
		return "formUpdateAlbum.html";
	}
	
	@GetMapping("/addArtistToAlbum/{idAlbum}")
	public String chooseArtistToModify(@PathVariable("idAlbum") Long id, Model model) {
		Album album=this.albumRepository.findById(id).get();
		List<Artist> artists=this.artistRepository.findByAlbumsWrittenContaining(album);
		List<Artist> allArtists=this.artistRepository.findByAlbumsWrittenNotContaining(album);
		model.addAttribute("artists", artists);
		model.addAttribute("others", allArtists);
		model.addAttribute("album", album);
		return "artistsToAdd.html";
	}
	
	@PostMapping("/modifyArtist/{idArtist}/{idAlbum}")
	public String editAutor(@PathVariable("idArtist") Long idArtist, @PathVariable("idAlbum") Long idAlbum, Model model) {
		Album album=this.albumRepository.findById(idAlbum).get();
		List<Artist> artist=album.getArtist();
		Artist newOne=this.artistRepository.findById(idArtist).get();
		List<Album> written=newOne.getAlbumsWritten();
		if(artist.contains(newOne)) {
			artist.remove(newOne);
			written.remove(album);
		}
		else {
			artist.add(newOne);
			written.add(album);
		}
		this.albumRepository.save(album);
		this.artistRepository.save(newOne);
		model.addAttribute("album", album);
		model.addAttribute("artists", this.artistRepository.findByAlbumsWrittenContaining(album));
		model.addAttribute("others", this.artistRepository.findByAlbumsWrittenContaining(album));
		return "artistsToAdd.html";
	}
	
}
