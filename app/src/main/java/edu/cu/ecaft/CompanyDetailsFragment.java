package edu.cu.ecaft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

/**
 * Created by Ashley on 1/16/2016.
 */
public class CompanyDetailsFragment extends Fragment {

    private TextView companyName;
    private TextView companyMajors;
    private TextView companyLocation;
    private TextView companyOpenings;
    private ImageView companyLogo;
    private TextView companyInfo;

    private String companyTable;
    private String objectID;
    private String name;
    private String majors;
    private String info;
    private String jobtitles;
    private String jobtypes;
    private String website;
    private boolean optcpt;
    private boolean sponsor;
    private StorageReference storageRef = FirebaseApplication
            .getStorageRef();
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.company_details_fragment, container, false);
        Bundle args = getArguments();
        objectID = args.getString(FirebaseApplication.COMPANY_ID);
        name = args.getString(FirebaseApplication.COMPANY_NAME);
        companyTable = args.getString(FirebaseApplication.COMPANY_TABLE);
        majors = args.getString(FirebaseApplication.COMPANY_MAJORS);
        jobtitles = args.getString(FirebaseApplication.COMPANY_JOBTITLES);
        info = args.getString(FirebaseApplication.COMPANY_INFO);
        jobtypes = args.getString(FirebaseApplication.COMPANY_JOBTYPES);
        website = args.getString(FirebaseApplication.COMPANY_WEBSITE);
        optcpt = args.getBoolean(FirebaseApplication.COMPANY_OPTCPT);
        sponsor = args.getBoolean(FirebaseApplication.COMPANY_SPONSOR);

//        Log.d("details", info);
        // logo = FirebaseApplication.getLogoByID(objectID);

        companyName = (TextView) v.findViewById(R.id.company_details_name);
        companyName.setText(name);

        companyMajors = (TextView) v.findViewById(R.id.company_details_majors);
        companyMajors.setText(majors);

        companyLocation = (TextView) v.findViewById(R.id
                .company_details_location);
        companyLocation.setText("Table " + companyTable);

        companyOpenings = (TextView) v.findViewById(R.id.company_details_positions);
        companyOpenings.setText(jobtitles);

        companyInfo = (TextView) v.findViewById(R.id.company_details_information);
        companyInfo.setText(info);

        companyLogo = (ImageView) v.findViewById(R.id.company_details_logo);

        StorageReference path = storageRef.child("logos/" +
                objectID + ".png");

        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(path)
                .into(companyLogo);

        getActivity().setTitle(name);

//        String majorText = "";
//        Object[] majorList = majors.toArray();
//
//        majorList = alphabetize(majorList);
//
//        for (Object s : majorList) {
//            majorText = majorText + s.toString() + "\n";
//        }
//        companyMajors.setText(majorText);

        return v;
    }

    public Object[] alphabetize(Object[] array) {
        for (int x = 0; x < array.length - 1; x++) {
            int smallest = findSmallest(array, x);
            Object temp = array[x];
            array[x] = array[smallest];
            array[smallest] = temp;
        }
        return array;
    }

    public int findSmallest(Object[] array, int i) {
        int temp = i;
        for (int x = i; x < array.length - 1; x++) {
            if (array[temp].toString().compareTo(array[x + 1].toString()) > 0)
                temp = x + 1;
        }
        return temp;
    }
}
