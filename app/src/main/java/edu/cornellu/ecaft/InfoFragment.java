package edu.cornellu.ecaft;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ashley on 11/8/2015.
 */
public class InfoFragment extends Fragment{

    private static final String TAG = "ECaFT";
    private static final String SAVED_LAYOUT_MANAGER = "Layout Manager";

    private RecyclerView.LayoutManager layoutManagerSavedState;
    private RecyclerView companyRecylerView;
    private CompanyAdapter companyAdapter;
    private View v;
    private List<Company> companies;

    public InfoFragment() {
        companies = makeCompanyList(ParseApplication.getCompanyPOS());
        companyAdapter = new CompanyAdapter(companies);
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View v = inflater.inflate(R.layout.info_fragment, container, false);

        companyRecylerView = (RecyclerView) v.findViewById(R.id
                .info_recycler_view);
        companyRecylerView.setLayoutManager(new LinearLayoutManager
                (getActivity()));
        companyRecylerView.setHasFixedSize(true);
        companyRecylerView.setAdapter(companyAdapter);
        //ct.execute();

        //    updateUI();

        getActivity().setTitle("List Of Companies");
        setHasOptionsMenu(true);
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_LAYOUT_MANAGER, companyRecylerView.getLayoutManager()
                .onSaveInstanceState());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState
                    .getParcelable(SAVED_LAYOUT_MANAGER);
            companyRecylerView.getLayoutManager().onRestoreInstanceState
                    (savedRecyclerLayoutState);
        }
    }

    private void updateUI() {
// To dismiss the dialog
        //   List<Company> companies = makeCompanyList(ParseApplication
        //           .getCompanyPOS());

        if (companyAdapter == null) {
            companies = makeCompanyList(ParseApplication.getCompanyPOS());
            companyAdapter = new CompanyAdapter(companies);
            companyRecylerView.setAdapter(companyAdapter);
        } else
            companyAdapter.notifyDataSetChanged();
    }

    private List<Company> makeCompanyList(List<ParseObject> companies) {
        List<Company> compiledList = new ArrayList<>();
        for (ParseObject po : companies) {
            Company c = new Company(po.getObjectId(),
                    po.getString(ParseApplication.COMPANY_NAME),
                    po.getString(ParseApplication.COMPANY_TABLE),
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
        public TextView mCompanyLocation;
        public ParseImageView mCompanyLogo;
        public ImageButton mCompanySave;
        public Company currentCompany;


        public CompanyHolder(View itemView) {
            super(itemView);
            mCompanyRL = (RelativeLayout) itemView.findViewById(R.id.info_cardview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle myBundle = new Bundle();
                    myBundle.putString(ParseApplication.COMPANY_OBJECT_ID, currentCompany.objectID);
                    myBundle.putString(ParseApplication.COMPANY_NAME, currentCompany.name);
                    myBundle.putStringArrayList(ParseApplication.COMPANY_MAJORS, currentCompany.majors);
                    myBundle.putString(ParseApplication.COMPANY_TABLE,
                            currentCompany.location);

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

            mCompanyLocation = (TextView) itemView.findViewById(R.id
                    .company_table);

            mCompanyLogo = (ParseImageView) itemView.findViewById(R.id.company_logo);

            mCompanySave = (ImageButton) itemView.findViewById(R.id.save_company);
            mCompanySave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MainActivity.isInDatabase(currentCompany.name)) { //Change to remove icon
                        Toast.makeText(getContext(), R.string.star, Toast.LENGTH_SHORT).show();
                        mCompanySave.setImageResource(R.drawable.ic_unfavorite);
                        MainActivity.addRow(currentCompany.objectID,
                                currentCompany.name);
                    } else {
                        Toast.makeText(getContext(), R.string.unstar, Toast.LENGTH_SHORT).show();
                        mCompanySave.setImageResource(R.drawable.ic_favorite);
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
            View view = layoutInflater.inflate(R.layout.info_fragment_cardview,
                    parent, false);
            return new CompanyHolder(view);
        }

        @Override
        public void onBindViewHolder(CompanyHolder holder, int position) {
            Company currentCompany = companies.get(position);
            holder.currentCompany = currentCompany;
            holder.mCompanyName.setText(currentCompany.name);
            holder.mCompanyLocation.setText("Table " + currentCompany
                    .location);
            holder.mCompanyLogo.setParseFile(currentCompany.logo);
            holder.mCompanyLogo.loadInBackground();

            if (!MainActivity.isInDatabase(currentCompany.name)) { //Change to remove icon
                holder.mCompanySave.setImageResource(R.drawable.ic_favorite);
            } else {
                holder.mCompanySave.setImageResource(R.drawable.ic_unfavorite);
            }

            Log.d(TAG, "Recycler made for position " + position);
        }

        @Override
        public int getItemCount() {
            return companies.size();
        }
    }

    /**  public class CollectTasks extends AsyncTask<String, Void,
     * List<Company>> {

     protected List<Company> doInBackground(String... strings) {
     List<Company> companies = new ArrayList<>();
     List<ParseObject> list = ParseApplication.getCompanyPOS();


     for (ParseObject po : list) {
     Company c = new Company(po.getObjectId(),
     po.getString(ParseApplication.COMPANY_NAME),
     (ArrayList<String>) po.get(ParseApplication.COMPANY_MAJORS),
     po.getParseFile(ParseApplication.COMPANY_LOGO)
     );
     companies.add(c);
     }
     return companies;
     }

     protected void onPostExecute(List<Company> list) {
     companyRecylerView = (RecyclerView) v.findViewById(R.id
     .info_recycler_view);
     companyRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));
     companyRecylerView.setHasFixedSize(true);
     companyRecylerView.setAdapter(new CompanyAdapter(list));

     }
     }
     */
}
