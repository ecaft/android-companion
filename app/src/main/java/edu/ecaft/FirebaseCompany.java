package edu.ecaft;

/**
 * Created by Ashley on 1/5/2017.
 */
public class FirebaseCompany {
    public String id;
    public String description;
    public String jobtitles;
    public String jobtypes;
    public String location;
    public String majors;
    public String name;
    public String optcpt;
    public String sponsor;
    public String website;
    public String freshmen;

    public FirebaseCompany() {}

    public FirebaseCompany(String id, String description, String freshmen,
                           String jobtitles, String jobtypes, String location,
                           String majors, String name, String optcpt,
                           String sponsor, String website) {
        this.id = id;
        this.description = description;
        this.jobtitles = jobtitles;
        this.jobtypes = jobtypes;
        this.location = location;
        this.majors = majors;
        this.name = name;
        this.optcpt = optcpt;
        this.sponsor = sponsor;
        this.optcpt = optcpt;
        this.sponsor = sponsor;
        this.website = website;
        this.freshmen = freshmen;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJobtitles() {
        return jobtitles;
    }

    public void setJobtitles(String jobtitles) {
        this.jobtitles = jobtitles;
    }

    public String getJobtypes() {
        return jobtypes;
    }

    public void setJobtypes(String jobtypes) {
        this.jobtypes = jobtypes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMajors() {
        return majors;
    }

    public void setMajors(String majors) {
        this.majors = majors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOptcpt() {
        return !(optcpt.equals("0"));
    }

    public void setOptcpt(String optcpt) {
        this.optcpt = optcpt;
    }

    public boolean isSponsor() {
        return !(sponsor.equals("0"));
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFreshmen() {
        return freshmen;
    }

    public void setFreshmen(String freshmen) {
        this.freshmen = freshmen;
    }
}
