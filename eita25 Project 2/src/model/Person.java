package model;

public class Person {
	private int id;
	private String name;
	private String occupation;
	private String division;
	private String password;
  private int pin;
	private String salt;
	private int failedAttempts;

	public Person(String[] strings) {
		this.id = Integer.parseInt(strings[0]);
		this.name = strings[1];
		this.occupation = strings[2];
		this.division = strings[3];
		this.password = strings[4];
    this.pin = Integer.parseInt(strings[5]);
		this.salt = strings[6];
	}

	public void updatePerson(String[] strings) {
		this.id = Integer.parseInt(strings[0]);
		this.name = strings[1];
		this.occupation = strings[2];
		this.division = strings[3];
	}

	public int resetFail() {
		int temp = failedAttempts;
		failedAttempts = 0;
		return temp;
	}

	public void incFail() {
		failedAttempts++;
	}

	public String getPassword() {
		return password;
	}

	public String getSalt() {
		return salt;
	}

  public int getPin() {
    return pin;
  }

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}

	public String getOccupation() {
		return occupation;
	}

	public String getDivision() {
		return division;
	}

	public String toString() {
		return this.id + " " + this.occupation + " " + this.division + " " + this.name + " " + this.password;
	}
}
