package edu.ashleyxue.ecaft;

/**
 * Created by Ashley on 1/5/2017.
 */
public class FirebaseCompany {
    public String id;
    public String information;
    public String jobtitles;
    public String jobtypes;
    public String location;
    public String majors;
    public String name;
    public String optcpt;
    public String sponsor;
    //public boolean optcpt;
    //public boolean sponsor;
    public String website;

    public FirebaseCompany() {}

    /*public FirebaseCompany(String id, String information, String jobtitles,
                           String jobtypes, String location, String majors,
                           String name, boolean optcpt, boolean sponsor, String
                           website) {
        this.id = id;
        this.information = information;
        this.jobtitles = jobtitles;
        this.jobtypes = jobtypes;
        this.location = location;
        this.majors = majors;
        this.name = name;
        this.optcpt = optcpt;
        this.sponsor = sponsor;
        this.website = website;
    }*/

    public FirebaseCompany(String id, String information, String jobtitles,
                           String jobtypes, String location, String majors,
                           String name, String optcpt, String sponsor, String
                                   website) {
        this.id = id;
        this.information = information;
        this.jobtitles = jobtitles;
        this.jobtypes = jobtypes;
        this.location = location;
        this.majors = majors;
        this.name = name;
        /*
        if(Integer.parseInt(optcpt)==0)
            this.optcpt=false;
        else
            this.optcpt=true;
        if(Integer.parseInt(sponsor)==0)
            this.sponsor=false;
        else
            this.sponsor=true;
            */
        /*this.optcpt = !optcpt.equals("0");
        this.sponsor = !sponsor.equals("0");*/
        this.optcpt = optcpt;
        this.sponsor = sponsor;
        this.website = website;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation1(String information) {
        this.information = information;
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
/*
    public boolean isOptcpt() {
        return optcpt;
    }

    /*public void setOptcpt1(boolean optcpt) { this.optcpt = optcpt; }
*/
/*
    public void setOptcpt(String optcpt) {
        this.optcpt = optcpt.equals("0");
    }

    public boolean isSponsor() {
        return sponsor;
    }
*/
/*
    public void setSponsor1(boolean sponsor) {
        this.sponsor = sponsor;
    }
*/

/*
    public void setSponsor(String sponsor) {
        this.sponsor = sponsor.equals("0");
    }
    */



    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }


}
