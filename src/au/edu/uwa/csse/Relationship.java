package au.edu.uwa.csse;

import java.util.Date;

public class Relationship {
	public Developer d1;
	public Developer d2;
	public double weight;
	public Date date;
	
	public Relationship(Developer d1, Developer d2, double weight, Date date)
	{
		this.d1 = d1;
		this.d2 = d2;
		this.weight = weight;
		this.date = date;
	}

}
