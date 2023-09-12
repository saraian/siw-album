package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.validator.ArtistValidator;

@Controller
public class ArtistController {
	
	@Autowired ArtistRepository artistRepository;
	@Autowired ArtistValidator artistValidator;
	
	@GetMapping("/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "formNewArtist.html";
	}
	
	@PostMapping("/artists")
	public String newArtist(@ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model) {
		artistValidator.validate(artist, bindingResult);
		if(!bindingResult.hasErrors()) {
			this.artistRepository.save(artist);
			model.addAttribute("artist", artist);
			return "artist.html";
		}
		else {
			model.addAttribute("messaggioErrore", "Questo artista esiste già");
			return "formNewArtist.html";
		}
	}
	
	//mostra tutti gli artisti
	@GetMapping("/artists")
	public String showArtists(Model model) {
		model.addAttribute("artists", this.artistRepository.findAll());
		return "artists.html";
	}
	
	//restituisce una pagina con i dati del film avente l'id corrispondente
	@GetMapping("/artists/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistRepository.findById(id).get());
		return "artist.html";
	}	
	
	@GetMapping("/manageArtists")
	public String manageArtists(Long id, Model model) {
		model.addAttribute("artists", this.artistRepository.findAll());
		return "manageArtists.html";
	}
	
	@GetMapping("/formUpdateArtist/{id}")
	public String formUpdateArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistRepository.findById(id).get());
		return "formUpdateArtist.html";
	}
	
	@PostMapping("/deleteArtist/{id}")
	public String deleteProduct(@PathVariable("id") Long idArtist, Model model) {
		this.artistRepository.deleteById(idArtist);
		model.addAttribute("artists", this.artistRepository.findAll());
		return "manageArtists.html";
	}
	
	@GetMapping("/updateArtistInfo/{idArtist}")
	public String updateProduct(@PathVariable("idArtist") Long idArtist, Model model) {
		Artist artist=this.artistRepository.findById(idArtist).get();
		model.addAttribute("artist", artist);
		return "formUpdateInfo.html";
	}
	
	@PostMapping("/updateArtistInfo/{idArtist}") 
	public String updateArtistInfo(@ModelAttribute("artist") Artist artist, @PathVariable("idArtist") Long idArtist, Model model) {
		Artist found=this.artistRepository.findById(idArtist).get();
		if(!artist.getArtName().isBlank()&&!(artist.getUrlImage().isEmpty())) {
			found.setAlbumsWritten(artist.getAlbumsWritten());
			found.setArtName(found.getArtName());
			found.setDescription(artist.getDescription());
			found.setUrlImage(artist.getUrlImage());
			this.artistRepository.save(found);
			model.addAttribute("artist",found);
			return "artist.html";
		}
		model.addAttribute("messaggioErrore", "Assicurati di aggiungere sia un nome che un'immagine validi");
		return "formUpdateInfo.html";
	}
	
	@GetMapping("/findArtistDiscography")
	public String artistForDiscography(Model model) {
		model.addAttribute("artists", this.artistRepository.findAll());
		return "discographyForArtists.html";
	}
}
