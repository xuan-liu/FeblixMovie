

public class Cast {
	private String movieId;

	private String stageName;

	public Cast() {

	}

	public Cast(String movieId, String stageName) {
		this.movieId = movieId;
		this.stageName = stageName;
	}

	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getMovieId() {
		return movieId;
	}
	public void setMovieId(String movieId) {
		this.movieId = movieId;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("MovieId:" + getMovieId());
		sb.append(", ");
		sb.append("StageName:" + getStageName());
		sb.append(".");
		
		return sb.toString();
	}
}
