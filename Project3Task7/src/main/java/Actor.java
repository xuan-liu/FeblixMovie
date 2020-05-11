

public class Actor {
	private String stageName;

	private String birthyear;

	public Actor() {

	}

	public Actor(String stageName, String lastName, String firstName, String birthyear) {
		this.stageName = stageName;
		this.birthyear  = birthyear;
	}

	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getBirthyear() {
		return birthyear;
	}
	public void setBirthyear(String birthyear) {
		this.birthyear = birthyear;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getStageName() != null) {
			sb.append("StageName:" + getStageName());
		} else {
			sb.append("StageName: null");
		}

		sb.append(", ");

		if (getBirthyear() != null) {
			sb.append("Birthyear:" + getBirthyear());
		} else {
			sb.append("Birthyear: null");
		}

		sb.append(".");
		
		return sb.toString();
	}
}
