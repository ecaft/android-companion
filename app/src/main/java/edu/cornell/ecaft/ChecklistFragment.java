package edu.cornell.ecaft;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import edu.cornell.ecaft.DatabaseSchema.CompanyTable;

/**
 * Created by Ashley on 11/8/2015.
 */
public class ChecklistFragment extends Fragment {

    private static final String TAG = "ECaFT";

    private RecyclerView companyRecylerView;
    private CompanyAdapter companyAdapter;

    private SQLiteDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        database = MainActivity.mDatabase;

        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(R.layout.checklist_fragment, container, false);

        companyRecylerView = (RecyclerView) v.findViewById(R.id.checklist_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        updateUI();

        return v;
    }

    private void updateUI() {

        List<Company> companies = makeSavedList();
        companyAdapter = new CompanyAdapter(companies);
        companyRecylerView.setAdapter(companyAdapter);
    }


    private List<Company> makeSavedList() {
        List<Company> compiledList = new ArrayList<>();

        Cursor c = database.query(CompanyTable.NAME,
                null, null, null, null, null, null);

        try {
            c.moveToFirst();

            while (!c.isAfterLast()) {

                ParseObject po = ParseApplication.getPOByID(c.getString(c.getColumnIndex(CompanyTable.Cols.UUID)));

                Company com = new Company(po.getObjectId(),
                        po.getString(ParseApplication.COMPANY_NAME),
                        (ArrayList<String>) po.get(ParseApplication.COMPANY_MAJORS),
                        po.getParseFile(ParseApplication.COMPANY_LOGO)
                );
                compiledList.add(com);
                c.moveToNext();
            }

        } finally {
            c.close();
        }


        return compiledList;
    }

    /**
     * Private classes
     */
    private class CompanyHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mCompanyRL;
        public TextView mCompanyName;
        public ParseImageView mCompanyLogo;
        public CheckBox mCompanySave;
        public Company currentCompany;

        public CompanyHolder(View itemView) {
            super(itemView);

            mCompanyRL = (RelativeLayout) itemView.findViewById(R.id.checklist_cardview);
            mCompanyRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle myBundle = new Bundle();
                    myBundle.putString(ParseApplication.COMPANY_OBJECT_ID, currentCompany.objectID);
                    myBundle.putString(ParseApplication.COMPANY_NAME, currentCompany.name);
                    myBundle.putStringArrayList(ParseApplication.COMPANY_MAJORS, currentCompany.majors);

                    Fragment fragment = new CompanyDetailsFragment();
                    fragment.setArguments(myBundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
                            .addToBackStack(null).commit();
                }
            });

            mCompanyName = (TextView) itemView.findViewById(R.id.company_checklist_name);

            mCompanyLogo = (ParseImageView) itemView.findViewById(R.id.company_checklist_logo);

            mCompanySave = (CheckBox) itemView.findViewById(R.id.check_company);
            mCompanySave.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Toast.makeText(getContext(), R.string.visited, Toast.LENGTH_SHORT).show();

                }
            });
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
            View view = layoutInflater.inflate(R.layout.checklist_fragment_cardview, parent, false);
            return new CompanyHolder(view);
        }

        @Override
        public void onBindViewHolder(CompanyHolder holder, int position) {
            Company currentCompany = companies.get(position);
            holder.currentCompany = currentCompany;
            holder.mCompanyName.setText(currentCompany.name);
            holder.mCompanyLogo.setParseFile(currentCompany.logo);
            holder.mCompanyLogo.loadInBackground();
            Log.d(TAG, "Recycler made for position " + position);
        }

        @Override
        public int getItemCount() {
            return companies.size();
        }
    }

    private class Company {
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


}
