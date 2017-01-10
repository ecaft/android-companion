package edu.cu.ecaft;

import android.app.Application;
import android.app.Dialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashley on 1/12/2016.
 */
public class FirebaseApplication extends Application {

    public static final String TAG = "Firebase";

    /**
     * Parse Class Names
     */
    public static final String PARSE_COMPANY_CLASS = "Companies";

    /**
     * Private Class Lists
     */
    private static List<ParseObject> companyPOS = new ArrayList<>();

    /**
     * Firebase Fields
     */
    public static final String COMPANY_NAME = "name";
    public static final String COMPANY_OBJECT_ID = "id";
    public static final String COMPANY_LOGO = "Logo";
    public static final String COMPANY_MAJORS = "majors";
    public static final String COMPANY_TABLE = "location";
    public static final String COMPANY_OPENINGS = "openings";
    public static final String COMPANY_INFO = "information";
    public static final String COMPANY_WEBSITE = "website";

    private static ParseObject temp;

    private static DatabaseReference databaseReference;
    private static FirebaseStorage storage;
    private static StorageReference storageRef;
    private static List<FirebaseCompany> companies;


    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        // Get database/storage reference and initialize everything
        companies = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("companies");
        databaseReference.addValueEventListener(new ValueEventListener
                () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String names = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    names += snapshot.getKey();
                    Log.d("test", snapshot.getValue().toString());
                    FirebaseCompany fc = snapshot.getValue(FirebaseCompany
                            .class);
                    companies.add(fc);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("test", "oncancel");
            }
        });

        // TODO: Set up storage methods
        storage = FirebaseStorage.getInstance();
        // gs://ecaft-4a6e7.appspot.com
        // Create a storage reference from our app
        storageRef = storage.getReferenceFromUrl("gs://ecaft-4a6e7.appspot.com");
        StorageReference child = storageRef.child("logos/1stdibs.png");
        // File test = child.g;
        Log.d(TAG, child.getName());

    }

    public static List<FirebaseCompany> getCompanies() {
        return companies;
    }

    public static DatabaseReference getFirebaseDatabase() {
        return databaseReference;
    }

    public static StorageReference getStorageRef(String child) {
        StorageReference path = storageRef.child("logos/" + child + ".jpg");
        return path;
    }

     // /** PARSE METHODS (deprecated)

    public static List<ParseObject> getCompanyPOS() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMPANY_CLASS);
       query.setLimit(200);
        query.orderByAscending(COMPANY_NAME);
        try {
            companyPOS = query.find();
        } catch (ParseException e) {
            Log.d(TAG, "Something went wrong");
        }

        return companyPOS;
    }

    public static ParseFile getLogoByID(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMPANY_CLASS);
        query.include(FirebaseApplication.COMPANY_OBJECT_ID);
        try {
            temp = query.get(id);
        } catch (ParseException e) {
            Log.d(TAG, "Something went wrong");
        }
        return temp.getParseFile(FirebaseApplication.COMPANY_LOGO);
    }

    public static ParseObject getPOByID(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMPANY_CLASS);
        query.include(FirebaseApplication.COMPANY_OBJECT_ID);
        try {
            temp = query.get(id);
        } catch (ParseException e) {
            Log.d(TAG, "Something went wrong");
        }
        return temp;
    }


    public static ParseObject getPOByName(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMPANY_CLASS);
        query.include(FirebaseApplication.COMPANY_NAME);
        try {
            temp = query.get(id);
        } catch (ParseException e) {
            Log.d(TAG, "Something went wrong");
        }
        return temp;
    }

    // */

/** UNUSED PARSE METHODS
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
            companyLogos.add(po.getParseFile(COMPANY_LOGO));
        }
        return companyLogos;
    }
 */
}
