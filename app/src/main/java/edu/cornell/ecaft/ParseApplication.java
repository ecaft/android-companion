package edu.cornell.ecaft;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashley on 1/12/2016.
 */
public class ParseApplication extends Application {

    public static final String TAG = "Parse";

    /**
     * Parse Class Names
     */
    public static final String PARSE_COMPANY_CLASS = "Companies";

    /**
     * Private Class Lists
     */
    private static List<ParseObject> companyPOS = new ArrayList<>();

    /**
     * Parse Company Constants
     */
    public static final String COMPANY_NAME = "Name";
    public static final String COMPANY_OBJECT_ID = "objectId";
    public static final String COMPANY_LOGO = "Logo";
    public static final String COMPANY_MAJORS = "Majors";

    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this);
        Log.d(TAG, "initialized");

    }

    public static void getCompanyPOS() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMPANY_CLASS);
        query.orderByAscending(COMPANY_NAME);
        try {
            companyPOS = query.find();
        } catch (ParseException e) {
            Log.d(TAG, "Something went wrong");
        }
    }

    public static List<String> getCompanyNames() {
        List<String> companyNames = new ArrayList<>();

        for (ParseObject po : companyPOS) {
            companyNames.add(po.getString(COMPANY_NAME));
        }

        return companyNames;
    }

    public static List<ArrayList<String>> getCompanyMajors() {
        List<ArrayList<String>> companyMajors = new ArrayList<>();

        for (ParseObject po : companyPOS) {
            companyMajors.add((ArrayList<String>) po.get(COMPANY_MAJORS));
        }

        return companyMajors;
    }

    public static List<ParseFile> getCompanyLogos() {
        List<ParseFile> companyLogos = new ArrayList<>();

        for(ParseObject po: companyPOS) {
            ParseFile test = po.getParseFile(COMPANY_LOGO);
            companyLogos.add(po.getParseFile(COMPANY_LOGO));
        }
        return companyLogos;
    }
}
