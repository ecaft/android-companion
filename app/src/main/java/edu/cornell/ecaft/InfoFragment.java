package edu.cornell.ecaft;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashley on 11/8/2015.
 */
public class InfoFragment extends Fragment {

    private static final String TAG = "ECaFT";

    private RecyclerView companyRecylerView;
    private CompanyAdapter companyAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        ParseApplication.getCompanyPOS(); //Fetch the company data from Parse

        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(R.layout.info_fragment, container, false);

        companyRecylerView = (RecyclerView) v.findViewById(R.id.info_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    private void updateUI() {

        List<String> companyNames = ParseApplication.getCompanyNames();
        List<ArrayList<String>> companyMajors = ParseApplication.getCompanyMajors();
        List<ParseFile> companyLogos = ParseApplication.getCompanyLogos();

        List<Company> companies = makeCompanyList(companyNames, companyMajors, companyLogos);
        companyAdapter = new CompanyAdapter(companies);
        companyRecylerView.setAdapter(companyAdapter);
    }

    private List<Company> makeCompanyList(List<String> cNames, List<ArrayList<String>> cMajors, List<ParseFile> cLogos) {
        List<Company> compiledList = new ArrayList<>();
        for(int x = 0; x < cNames.size(); x++) {
            Company c = new Company(cNames.get(x), cMajors.get(x), cLogos.get(x));
            compiledList.add(c);
        }
        return compiledList;
    }

    /**
     * Private classes
     */
    private class CompanyHolder extends RecyclerView.ViewHolder {
        public TextView mCompanyName;
        public ParseImageView mCompanyLogo;

        public CompanyHolder(View itemView) {
            super(itemView);

            mCompanyName = (TextView) itemView.findViewById(R.id.company_name);
            mCompanyLogo = (ParseImageView) itemView.findViewById(R.id.company_logo);
        }
    }

    private class CompanyAdapter extends RecyclerView.Adapter<CompanyHolder> {
        public List<Company> companies;

        public CompanyAdapter(List<Company> companies) {
            this.companies = companies;
        }

        @Override
        public CompanyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.info_fragment_cardview, parent, false);
            return new CompanyHolder(view);
        }

        @Override
        public void onBindViewHolder(CompanyHolder holder, int position) {
            Company company = companies.get(position);
            holder.mCompanyName.setText(company.name);
            holder.mCompanyLogo.setParseFile(company.logo);
            holder.mCompanyLogo.loadInBackground();
            Log.d(TAG, "Recycler made for position " + position);
        }

        @Override
        public int getItemCount() {
            return companies.size();
        }
    }

    private class Company {
        public String name;
        public ArrayList<String> majors;
        public ParseFile logo;

        public Company(String name, ArrayList<String> majors, ParseFile logo) {
            this.name = name;
            this.majors = majors;
            this.logo = logo;
        }
    }
}
