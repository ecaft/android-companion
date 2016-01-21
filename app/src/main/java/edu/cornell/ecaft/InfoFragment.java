package edu.cornell.ecaft;

import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(R.layout.info_fragment, container, false);

        companyRecylerView = (RecyclerView) v.findViewById(R.id.info_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        getActivity().setTitle("List Of Companies");

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        List<Company> companies = makeCompanyList(ParseApplication.getCompanyPOS());
        companyAdapter = new CompanyAdapter(companies);
        companyRecylerView.setAdapter(companyAdapter);
    }

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
        public ImageButton mCompanySave;
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

                    Intent i = new Intent(getActivity(), CompanyDetailsActivity.class);
                    i.putExtras(myBundle);
                    startActivity(i);

             /**       Fragment fragment = new CompanyDetailsFragment();
                    fragment.setArguments(myBundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment)
                            .addToBackStack(null).commit(); */
                }
            });

            mCompanyName = (TextView) itemView.findViewById(R.id.company_name);

            mCompanyLogo = (ParseImageView) itemView.findViewById(R.id.company_logo);

            mCompanySave = (ImageButton) itemView.findViewById(R.id.save_company);
            mCompanySave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MainActivity.isInDatabase(currentCompany.name)) { //Change to remove icon
                        Toast.makeText(getContext(), R.string.star, Toast.LENGTH_SHORT).show();
                        mCompanySave.setImageResource(R.mipmap.ic_remove_circle_outline_black_24dp);
                        MainActivity.addRow(currentCompany.objectID, currentCompany.name);
                    } else {
                        Toast.makeText(getContext(), R.string.unstar, Toast.LENGTH_SHORT).show();
                        mCompanySave.setImageResource(R.mipmap.ic_add_circle_outline_black_24dp);
                        MainActivity.deleteRow(currentCompany.objectID);
                    }
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

            if (!MainActivity.isInDatabase(currentCompany.name)) { //Change to remove icon
                holder.mCompanySave.setImageResource(R.mipmap.ic_add_circle_outline_black_24dp);
            } else {
                holder.mCompanySave.setImageResource(R.mipmap.ic_remove_circle_outline_black_24dp);
            }

            Log.d(TAG, "Recycler made for position " + position);
        }

        @Override
        public int getItemCount() {
            return companies.size();
        }
    }
}
