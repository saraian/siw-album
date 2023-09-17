package it.uniroma3.siw.model;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Album {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	@NotBlank
	private String title;
	@NotNull
	@Min(1700)
	@Max(2023)
	private Integer year;
	@Column(nullable = true, length = 64)
	private String cover;
	@ManyToMany(mappedBy="albumsWritten")
	private List<Artist> artist;
	@OneToMany
	private List<Review> reviews;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String urlImage) {
		this.cover = urlImage;
	}
	public List<Artist> getArtist() {
		return artist;
	}
	public void setArtist(List<Artist> artist) {
		this.artist = artist;
	}
	public List<Review> getReviews() {
		return reviews;
	}
	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	@Transient
    public String getCoverImagePath() {
        if (cover == null || id == null) return null;
         
        return "/album-covers/" + id + "/" + cover;
    }
	@Override
	public int hashCode() {
		return Objects.hash(id, title, cover, year);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Album other=(Album) obj;
		return Objects.equals(title, other.title) && Objects.equals(year, other.year);
	}
}
