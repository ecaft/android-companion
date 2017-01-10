package edu.cu.ecaft;

/**
 * Created by Ashley on 1/5/2017.
 */
public class FirebaseCompany {
    public String id;
    public String name;
    public String information;
    public String location;
    public String majors;
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

    public String getName() {
        return name;
    }

    public String getInformation() {
        return information;
    }

    public String getLocation() {
        return location;
    }

    public String getMajors() {
        return majors;
    }

    public String getOpenings() {
        return openings;
    }

    public String getWebsite() {
        return website;
    }
}
