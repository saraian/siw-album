package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.List;

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

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.AlbumRepository;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.service.AlbumService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.validator.AlbumValidator;
import jakarta.validation.Valid;

@Controller
public class AlbumController {
	
	@Autowired AlbumValidator albumValidator;
	@Autowired AlbumService albumService;
	@Autowired AlbumRepository albumRepository;
	@Autowired ArtistRepository artistRepository;
	@Autowired CredentialsService credentialsService;
	@Autowired ReviewService reviewService;
	
	@GetMapping("/formNewAlbum")
	public String formNewAlbum(Model model) {
		model.addAttribute("album", new Album());
		return "formNewAlbum.html";
	}
	
	@PostMapping("/albums")
	public RedirectView newAlbum(@Valid @ModelAttribute("album") Album album, @RequestParam("image") MultipartFile multipartFile,
	BindingResult bindingResult, Model model) throws IOException {
		this.albumValidator.validate(album, bindingResult);
		if(!bindingResult.hasErrors()) { 
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			album.setCover(fileName);
			this.albumRepository.save(album);
			model.addAttribute("album", album);
			String uploadDir="album-covers/"+album.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			return new RedirectView("/albums", true);
		}
		else {
			model.addAttribute("messaggioErrore", "Questo album esiste già");
			return new RedirectView("/formNewAlbum", true);
		}
	}
	
	@GetMapping("/albums/{id}")
	public String getAlbum(@PathVariable("id") Long id, Model model) {
		Credentials credentials=this.credentialsService.findCredentials();
		Album album=this.albumService.findAlbumById(id);
		model.addAttribute("album", album);
		model.addAttribute("userReviews", this.reviewService.reviewsByCredentials(credentials, album.getReviews()));
		model.addAttribute("reviews", this.reviewService.reviewsNotByCredentials(credentials, album.getReviews()));
		return "album.html";
	}
	
	@GetMapping("/albums")
	public String showAlbums(Model model) {
		model.addAttribute("albums", this.albumRepository.findAll());
		return "albums.html";
	}
	
	@GetMapping("/formSearchAlbums")
	public String formSearchAlbums(Model model) {
		return "formSearchAlbums.html";
	}

	@GetMapping("/manageAlbums")
	public String manageAlbums(Model model) {
		model.addAttribute("albums", this.albumRepository.findAll());
		return "manageAlbums.html";
	}
	
	@GetMapping("/formUpdateAlbum/{id}")
	public String formUpdateAlbum(@PathVariable("id") Long idAlbum, Model model) {
		model.addAttribute("album", this.albumService.findAlbumById(idAlbum));
		return "formUpdateAlbum.html";
	}
	
	@GetMapping("/addArtistToAlbum/{idAlbum}")
	public String chooseArtistToModify(@PathVariable("idAlbum") Long id, Model model) {
		Album album=this.albumService.findAlbumById(id);
		List<Artist> artists=this.artistRepository.findByAlbumsWrittenContaining(album);
		List<Artist> allArtists=this.artistRepository.findByAlbumsWrittenNotContaining(album);
		model.addAttribute("artists", artists);
		model.addAttribute("others", allArtists);
		model.addAttribute("album", album);
		return "artistsToAdd.html";
	}
	
	@PostMapping("/modifyArtist/{idArtist}/{idAlbum}")
	public String editAutor(@PathVariable("idArtist") Long idArtist, @PathVariable("idAlbum") Long idAlbum, Model model) {
		Album album=this.albumService.findAlbumById(idAlbum);
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
		model.addAttribute("others", this.artistRepository.findByAlbumsWrittenNotContaining(album));
		return "artistsToAdd.html";
	}
	
	@GetMapping("/updateAlbumInfo/{idAlbum}")
	public String updateProduct(@PathVariable("idAlbum") Long idAlbum, Model model) {
		Album album=this.albumService.findAlbumById(idAlbum);
		Album newAlbum=new Album();
		newAlbum.setTitle(album.getTitle());
		newAlbum.setYear(album.getYear());
		model.addAttribute("album", album);
		model.addAttribute("newAlbum", newAlbum);
		return "formUpdateInfoAlbum.html";
	}
	
	@PostMapping("/updateAlbumInfo/{idAlbum}") 
	public String updateProductName(@ModelAttribute("newAlbum") Album album, @PathVariable("idAlbum") Long idAlbum, Model model) {
		Album found=this.albumService.findAlbumById(idAlbum);
		Credentials credentials=credentialsService.findCredentials();
		if(!album.getTitle().isBlank()) {
			found.setTitle(album.getTitle());
			found.setYear(album.getYear());
			this.albumRepository.save(found);
			model.addAttribute("album",found);
			model.addAttribute("userReviews", this.reviewService.reviewsByCredentials(credentials, found.getReviews()));
			model.addAttribute("reviews", this.reviewService.reviewsNotByCredentials(credentials, found.getReviews()));
			return "album.html";
		}
		model.addAttribute("messaggioErrore", "Assicurati che tutti i campi siano validi");
		return "formUpdateInfoAlbum.html";
	}
	
	@GetMapping("/artistDisco/{id}")
	public String artistForDisc(@PathVariable("id") Long id, Model model) {
		Artist artist=this.artistRepository.findById(id).get();
		List<Album> discography=this.albumRepository.findByArtistContainingOrderByYear(artist);
		model.addAttribute("albums", discography);
		return "discoList.html";
	}
	
}
