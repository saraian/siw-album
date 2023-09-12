package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Album;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.AlbumRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ReviewService;

@Controller
public class ReviewController {
	
	@Autowired CredentialsService credentialsService;
	@Autowired ReviewService reviewService;
	@Autowired AlbumRepository albumRepository;
	@Autowired ReviewRepository reviewRepository;
	
	@GetMapping("/addReview/{idAlbum}")
	public String addReviewPage(@PathVariable("idAlbum") Long id, Model model) {
		Album album=this.albumRepository.findById(id).get();
		UserDetails userDetails=(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials=credentialsService.getCredentials(userDetails.getUsername());
		model.addAttribute("album", album);
		model.addAttribute("newReview", new Review());
		model.addAttribute("userReviews", this.reviewService.reviewsByCredentials(credentials, album.getReviews()));
		model.addAttribute("reviews", this.reviewService.reviewsNotByCredentials(credentials, album.getReviews()));
		return "albumAddReview.html";
	}
	
	@PostMapping("/addingReview/{idAlbum}")
	public String addComment(@ModelAttribute("newReview") Review newReview, @PathVariable("idAlbum") Long id, Model model) {
		Album album=this.albumRepository.findById(id).get();
		UserDetails userDetails=(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials=credentialsService.getCredentials(userDetails.getUsername());
		newReview.setCredentials(credentials);
		album.getReviews().add(newReview);
		this.reviewRepository.save(newReview);
		this.albumRepository.save(album);
		model.addAttribute("product", album);
		model.addAttribute("userReviews", this.reviewService.reviewsByCredentials(credentials, album.getReviews()));
		model.addAttribute("reviews", this.reviewService.reviewsNotByCredentials(credentials, album.getReviews()));
		return "album.html";
	}
	
	@GetMapping("/updateReview/{idAlbum}/{idReview}")
	public String updateCommentPage(@PathVariable("idAlbum") Long idA, @PathVariable("idReview") Long idR, Model model) {
		Album album=this.albumRepository.findById(idA).get();
		Review review=this.reviewRepository.findById(idR).get();
		model.addAttribute("upReview", review);
		model.addAttribute("album", album);
		return "albumUpdateReview.html";
	}
	
	@PostMapping("/updateReview/{idAlbum}/{idReview}")
	public String saveUpdate(@ModelAttribute("upReview") Review review, @PathVariable("idAlbum") Long id,
			@PathVariable("idReview") Long idR, Model model) {
		Album album=this.albumRepository.findById(id).get();
		UserDetails userDetails=(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials=credentialsService.getCredentials(userDetails.getUsername());
		if(!review.getTitle().isBlank()&&!review.getText().isBlank()) {
			Review found=this.reviewRepository.findById(idR).get();
			found.setTitle(review.getTitle());
			found.setText(review.getText());
			found.setVote(review.getVote());
			this.reviewRepository.save(found);
			model.addAttribute("album", album);
			model.addAttribute("userReviews", this.reviewService.reviewsByCredentials(credentials, album.getReviews()));
			model.addAttribute("reviews", this.reviewService.reviewsNotByCredentials(credentials, album.getReviews()));
			return "album.html";
		}
		model.addAttribute("messaggioErrore", "Compila tutti i campi della recensione");
		model.addAttribute("album", album);
		model.addAttribute("userReviews", this.reviewService.reviewsByCredentials(credentials, album.getReviews()));
		model.addAttribute("reviews", this.reviewService.reviewsNotByCredentials(credentials, album.getReviews()));
		return "productUpdateComment.html";
	}
}