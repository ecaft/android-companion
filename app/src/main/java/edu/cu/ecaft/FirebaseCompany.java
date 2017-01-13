package edu.cu.ecaft;

import com.google.firebase.database.PropertyName;

/**
 * Created by Ashley on 1/5/2017.
 */
public class FirebaseCompany {
    public String id;
    public String information;
    public String location;
    public String majors;
    public String name;
    public String openings;
    public String website;

    public FirebaseCompany() {}

    public FirebaseCompany(String id, String name, String information, String
            location, String majors, String openings, String website) {
        this.id = id;
        this.name = name;
        this.information = information;
        this.location = location;
        this.majors = majors;
        this.openings = openings;
        this.website = website;
    }

    public String getId() {
        return id;
    }
    public void setId() {this.id= id;}

    @PropertyName("name")
    public String getName() {
        return name;
    }
    public void setName() {this.name = name;}

    public String getInformation() {
        return information;
    }
    public void setInformation() {this.information= information;}
    public String getLocation() {
        return location;
    }
    public void setLocation() {this.location= location;}
    public String getMajors() {
        return majors;
    }
    public void setMajors() {this.majors=majors;}
    public String getOpenings() {
        return openings;
    }
    public void setOpenings() {this.openings=openings;}
    public String getWebsite() {
        return website;
    }

    public String toString(){
        return id + " " + name + " " + information + " " + location + " " + majors + " " + openings + " " + website;
    }
}
