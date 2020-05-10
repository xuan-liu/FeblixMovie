import java.util.ArrayList;
import java.util.List;

public class Movie {
	private String id;

	private String title;

	private String year;

	private String director;

//	private String genres;
	private List<String> genres;

	public Movie() {
		this.genres = new ArrayList<>();
	}

	public Movie(String id, String title, String year, String director, List<String> genres) {
		this.id = id;
		this.title = title;
		this.year  = year;
		this.director = director;
		this.genres = genres;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}

	public String getDirector() {
		return director;
	}
	public void setDirector(String director) {
		this.director = director;
	}

//	public String getGenres() {
//		return genres;
//	}
//	public void setGenres(String genres) {
//		this.genres = genres;
//	}

	public List<String> getGenres() {
		return genres;
	}
	public void addGenre(String genre) {
		this.genres.add(genre);
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Id:" + getId());
		sb.append(", ");
		sb.append("Title:" + getTitle());
		sb.append(", ");
		sb.append("Year:" + getYear());
		sb.append(", ");
		sb.append("Director:" + getDirector());
		sb.append(", ");
		sb.append("Genres:" + getGenres());
		sb.append(".");
		
		return sb.toString();
	}
}
