package edu.cornellu.ecaft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;

import java.util.ArrayList;

/**
 * Created by Ashley on 1/16/2016.
 */
public class CompanyDetailsFragment extends Fragment {

    private ParseImageView companyLogo;
    private TextView companyName;
    private TextView companyMajors;
    private TextView companyLocation;
    private String companyTable;
    private String objectID;
    private String name;
    private ArrayList<String> majors;
    private ParseFile logo;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.company_details_fragment, container, false);
        Bundle args = getArguments();
        objectID = args.getString(ParseApplication.COMPANY_OBJECT_ID);
        name = args.getString(ParseApplication.COMPANY_NAME);
        companyTable = args.getString(ParseApplication.COMPANY_TABLE);
        majors = args.getStringArrayList(ParseApplication.COMPANY_MAJORS);
        logo = ParseApplication.getLogoByID(objectID);

        companyLogo = (ParseImageView) v.findViewById(R.id.company_details_logo);
        companyLogo.setParseFile(logo);
        companyLogo.loadInBackground();

        companyName = (TextView) v.findViewById(R.id.company_details_name);
        companyName.setText(name);

        companyMajors = (TextView) v.findViewById(R.id.company_details_majors);

        companyLocation = (TextView) v.findViewById(R.id
                .company_details_location);
        companyLocation.setText(companyTable);

        getActivity().setTitle("Company Details");

        String majorText = "";
        Object[] majorList = majors.toArray();

        majorList = alphabetize(majorList);

        for (Object s : majorList) {
            majorText = majorText + s.toString() + "\n";
        }
        companyMajors.setText(majorText);

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
