package edu.cornell.ecaft;

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
        majors = args.getStringArrayList(ParseApplication.COMPANY_MAJORS);
        logo = ParseApplication.getLogoByID(objectID);

        companyLogo = (ParseImageView) v.findViewById(R.id.company_details_logo);
        companyLogo.setParseFile(logo);
        companyLogo.loadInBackground();

        companyName = (TextView) v.findViewById(R.id.company_details_name);
        companyName.setText(name);

        companyMajors = (TextView) v.findViewById(R.id.company_details_majors);
        String majorText = "";

        for (String s : majors) {
            majorText = majorText + s + "\n";
        }
        companyMajors.setText(majorText);

        return v;
    }
}
