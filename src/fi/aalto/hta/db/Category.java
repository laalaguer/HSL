package fi.aalto.hta.db;

public class Category {
	public int _id;
	public String title;
	public int numberOfEntries;
	
	public Category(String title){
		this.title = title;
	}
	public Category(){
		this.title = "Not Available";
	}
	public void setTitle(String title){
		this.title = title;
	}
	public String getTitle(){
		return this.title;
	}
	public int getId(){
		return this._id;
	}
	public int getNumberOfEntries(){
		return this.numberOfEntries;
	}
}
