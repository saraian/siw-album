package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Review;

@Service
public class ReviewService {

	public List<Review> reviewsByCredentials(Credentials credentials, List<Review> reviews) {
		List<Review> userReviews=new ArrayList<Review>();
		Iterator<Review> iterator=reviews.iterator();
		while(iterator.hasNext()) {
			Review review=iterator.next();
			if(review.getCredentials().equals(credentials))
				userReviews.add(review);
		}
		return userReviews;
	}
	
	public List<Review> reviewsNotByCredentials(Credentials credentials, List<Review> reviews) {
		List<Review> otherReviews=new ArrayList<Review>();
		Iterator<Review> iterator=reviews.iterator();
		while(iterator.hasNext()) {
			Review review=iterator.next();
			if(!review.getCredentials().equals(credentials))
				otherReviews.add(review);
		}
		return otherReviews;
	}
}
