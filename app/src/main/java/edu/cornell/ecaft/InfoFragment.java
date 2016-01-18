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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import edu.cornell.ecaft.DatabaseSchema.CompanyTable;

/**
 * Created by Ashley on 11/8/2015.
 */
public class InfoFragment extends Fragment {

    private static final String TAG = "ECaFT";

    private RecyclerView companyRecylerView;
    private CompanyAdapter companyAdapter;

    private SQLiteDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        database = MainActivity.getDb();


        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(R.layout.info_fragment, container, false);

        companyRecylerView = (RecyclerView) v.findViewById(R.id.info_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        updateUI();

        return v;
    }

    private void updateUI() {

        // List<String> companyNames = ParseApplication.getCompanyNames();
        //List<ArrayList<String>> companyMajors = ParseApplication.getCompanyMajors();
        //List<ParseFile> companyLogos = ParseApplication.getCompanyLogos();

        // List<Company> companies = makeCompanyList(companyNames, companyMajors, companyLogos);
        List<Company> companies = makeCompanyList(ParseApplication.getCompanyPOS());
        companyAdapter = new CompanyAdapter(companies);
        companyRecylerView.setAdapter(companyAdapter);
    }

    /**
     * private List<Company> makeCompanyList(List<String> cNames, List<ArrayList<String>> cMajors, List<ParseFile> cLogos) {
     * List<Company> compiledList = new ArrayList<>();
     * for(int x = 0; x < cNames.size(); x++) {
     * Company c = new Company(cNames.get(x), cMajors.get(x), cLogos.get(x));
     * compiledList.add(c);
     * }
     * return compiledList;
     * }
     */
    private List<Company> makeCompanyList(List<ParseObject> companies) {
        List<Company> compiledList = new ArrayList<>();
        for (ParseObject po : companies) {
            Company c = new Company(po.getObjectId(),
                    po.getString(ParseApplication.COMPANY_NAME),
                    (ArrayList<String>) po.get(ParseApplication.COMPANY_MAJORS),
                    po.getParseFile(ParseApplication.COMPANY_LOGO)
            );
            compiledList.add(c);
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

            mCompanyRL = (RelativeLayout) itemView.findViewById(R.id.info_cardview);
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

            mCompanyName = (TextView) itemView.findViewById(R.id.company_name);

            mCompanyLogo = (ParseImageView) itemView.findViewById(R.id.company_logo);

            mCompanySave = (CheckBox) itemView.findViewById(R.id.save_company);
            mCompanySave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCompanySave.isChecked() && !isInDatabase(currentCompany)) {
                        Toast.makeText(getContext(), R.string.star, Toast.LENGTH_SHORT).show();
                        ContentValues values = new ContentValues();
                        values.put(CompanyTable.Cols.UUID, currentCompany.objectID);
                        values.put(CompanyTable.Cols.COMPANY_NAME, currentCompany.name);
                        values.put(CompanyTable.Cols.VISITED, 0);
                        database.insert(CompanyTable.NAME, null, values);
                    } else {
                        Toast.makeText(getContext(), R.string.unstar, Toast.LENGTH_SHORT).show();
                        MainActivity.deleteRow(currentCompany.objectID);
                    }
                }
            });

        }

        public String objectID(Company com) {
            Cursor c = database.query(CompanyTable.NAME, null, null, null, null, null, null);

            String id = "";
            try {

                c.moveToFirst();
                while (!c.isAfterLast()) {
                    if (com.name.equals(c.getString(c.getColumnIndex(CompanyTable.Cols.COMPANY_NAME)))) {
                        id = c.getString(c.getColumnIndex(CompanyTable.Cols.KEY_ID));
                        break;
                    }
                    c.moveToNext();
                }

            } finally {
                c.close();
            }
            return id;
        }

        public boolean isInDatabase(Company com) {
            Cursor c = database.query(CompanyTable.NAME, null, null, null, null, null, null);
            boolean inside = false;
            try {
                c.moveToFirst();

                while (!c.isAfterLast()) {
                    if (com.name.equals(c.getString(c.getColumnIndex(CompanyTable.Cols.COMPANY_NAME)))) {
                        inside = true;
                        break;
                    }
                    c.moveToNext();
                }

            } finally {
                c.close();
            }
            return inside;
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
            Company currentCompany = companies.get(position);
            holder.currentCompany = currentCompany;
            holder.mCompanyName.setText(currentCompany.name);
            holder.mCompanyLogo.setParseFile(currentCompany.logo);
            holder.mCompanyLogo.loadInBackground();

            boolean setChecked = false;

            Cursor c = database.query(CompanyTable.NAME, null, null, null, null, null, null);
            try {
                c.moveToFirst();

                while (!c.isAfterLast()) {
                    if (currentCompany.objectID.equals(c.getString(c.getColumnIndex(CompanyTable.Cols.UUID)))) {
                        setChecked = true;
                        break;
                    }
                    c.moveToNext();
                }

            } finally {
                c.close();
            }
            holder.mCompanySave.setChecked(setChecked);
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
