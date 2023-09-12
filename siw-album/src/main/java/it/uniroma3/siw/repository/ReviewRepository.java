package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import it.uniroma3.siw.model.Review;

@Repository
public interface ReviewRepository extends CrudRepository<Review, Long> {
	public List<Review> findByVote(int vote);
}
