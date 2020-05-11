

public class Actor {
	private String stageName;

//	private String lastName;
//
//	private String firstName;

	private String birthyear;

	public Actor() {

	}

	public Actor(String stageName, String lastName, String firstName, String birthyear) {
		this.stageName = stageName;
//		this.lastName = lastName;
//		this.firstName = firstName;
		this.birthyear  = birthyear;
	}

	public String getStageName() {
		return stageName;
	}
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

//	public String getFirstName() {
//		return firstName;
//	}
//	public void setFirstName(String firstName) {
//		this.firstName = firstName;
//	}
//
//	public String getLastName() {
//		return lastName;
//	}
//	public void setLastName(String lastName) {
//		this.lastName = lastName;
//	}

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

//		if (getFirstName() != null && getLastName() != null) {
//			sb.append("Name:" + getFirstName() + " " + getLastName());
//		} else {
//			sb.append("Name: null");
//		}
//
//		sb.append(", ");

		if (getBirthyear() != null) {
			sb.append("Birthyear:" + getBirthyear());
		} else {
			sb.append("Birthyear: null");
		}

		sb.append(".");
		
		return sb.toString();
	}
}
