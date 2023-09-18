package it.uniroma3.siw.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.validator.ArtistValidator;

@Controller
public class ArtistController {
	
	@Autowired ArtistRepository artistRepository;
	@Autowired ArtistValidator artistValidator;
	@Autowired ArtistService artistService;
	
	@GetMapping("/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "formNewArtist.html";
	}
	
	@PostMapping("/artists")
	public RedirectView newArtist(@ModelAttribute("artist") Artist artist, @RequestParam("image") MultipartFile multipartFile,
			BindingResult bindingResult, Model model) throws IOException {
		artistValidator.validate(artist, bindingResult);
		if(!bindingResult.hasErrors()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			artist.setUrlImage(fileName);
			this.artistRepository.save(artist);
			model.addAttribute("artist", artist);
			String uploadDir="album-covers/"+artist.hashCode();
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			return new RedirectView("/artists", true);
		}
		else {
			model.addAttribute("messaggioErrore", "Questo artista esiste già");
			return new RedirectView("/formNewArtist", true);
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
		model.addAttribute("artist", this.artistService.findArtistById(id));
		return "artist.html";
	}	
	
	@GetMapping("/manageArtists")
	public String manageArtists(Long id, Model model) {
		model.addAttribute("artists", this.artistRepository.findAll());
		return "manageArtists.html";
	}
	
	@GetMapping("/formUpdateArtist/{id}")
	public String formUpdateArtist(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artist", this.artistService.findArtistById(id));
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
		Artist artist=this.artistService.findArtistById(idArtist);
		Artist newArtist=new Artist();
		newArtist.setArtName(artist.getArtName());
		newArtist.setDescription(artist.getDescription());
		model.addAttribute("artist", artist);
		model.addAttribute("newArtist", newArtist);
		return "formUpdateInfo.html";
	}
	
	@PostMapping("/updateArtistInfo/{idArtist}") 
	public String updateArtistInfo(@ModelAttribute("newArtist") Artist artist, 
			BindingResult bindingResult, @PathVariable("idArtist") Long idArtist, Model model) {
		this.artistValidator.validate(artist, bindingResult);
		Artist found=this.artistService.findArtistById(idArtist);
		if(!bindingResult.hasErrors()) {
			found.setAlbumsWritten(artist.getAlbumsWritten());
			found.setArtName(artist.getArtName());
			found.setDescription(artist.getDescription());
			this.artistRepository.save(found);
			model.addAttribute("artist",found);
			return "artist.html";
		}
		model.addAttribute("messaggioErrore", "Assicurati di aggiungere campi validi");
		return "formUpdateInfo.html";
	}
	
	@GetMapping("/findArtistDiscography")
	public String artistForDiscography(Model model) {
		model.addAttribute("artists", this.artistRepository.findAll());
		return "discographyForArtists.html";
	}
}
