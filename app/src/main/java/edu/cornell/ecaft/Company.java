package edu.cornell.ecaft;

import com.parse.ParseFile;

import java.util.ArrayList;

/**
 * Created by Ashley on 1/19/2016.
 */
public class Company {
    public String objectID;
    public String name;
    public ArrayList<String> majors;
    public ParseFile logo;

    public Company(String objectID, String name, ArrayList<String> majors, ParseFile logo) {
        this.objectID = objectID;
        this.name = name;
        this.majors = majors;
        this.logo = logo;
    }

}
