package edu.cu.ecaft;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.List;


/**
 * Created by Ashley on 11/8/2015.
 */
public class InfoFragment extends Fragment{

    private static final String TAG = "ECaFT";
    private static final String SAVED_LAYOUT_MANAGER = "Layout Manager";
    private StorageReference storageRef = FirebaseApplication
            .getStorageRef();

    private RecyclerView.LayoutManager layoutManagerSavedState;
    private RecyclerView companyRecylerView;
    private CompanyAdapter companyAdapter;
    private View v;
    private List<FirebaseCompany> companies;

    public InfoFragment() {
//        int resultCode = GoogleApiAvailability.getInstance()
//                .isGooglePlayServicesAvailable(getActivity());
//
//        if (resultCode == ConnectionResult.SUCCESS){
//            Log.d("ecaft", "isGooglePlayServicesAvailable SUCCESS");
//        } else {
//            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),
//                    resultCode, 1).show();
//        }
        companies = FirebaseApplication.getCompanies();
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

        if (companyAdapter == null) {
            companies = FirebaseApplication.getCompanies();
            companyAdapter = new CompanyAdapter(companies);
            companyRecylerView.setAdapter(companyAdapter);
        } else
            companyAdapter.notifyDataSetChanged();
    }

    /**
     * Private classes
     */
    private class CompanyHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mCompanyRL;
        public TextView mCompanyName;
        public TextView mCompanyLocation;
        public ImageView mCompanyLogo;
        public ImageButton mCompanySave;
        public FirebaseCompany currentCompany;


        public CompanyHolder(View itemView) {
            super(itemView);
            mCompanyRL = (RelativeLayout) itemView.findViewById(R.id.info_cardview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Bundle myBundle = new Bundle();
                    myBundle.putString(FirebaseApplication.COMPANY_ID,
                            currentCompany.id);
                    myBundle.putString(FirebaseApplication.COMPANY_NAME,
                            currentCompany.name);
                    myBundle.putString(FirebaseApplication.COMPANY_MAJORS,
                            currentCompany.majors);
                    myBundle.putString(FirebaseApplication.COMPANY_TABLE,
                            currentCompany.location);
                    myBundle.putString(FirebaseApplication.COMPANY_JOBTITLES,
                            currentCompany.jobtitles);
                    myBundle.putString(FirebaseApplication.COMPANY_JOBTYPES,
                            currentCompany.jobtypes);
                    myBundle.putString(FirebaseApplication.COMPANY_INFO,
                            currentCompany.information);
                    myBundle.putString(FirebaseApplication.COMPANY_WEBSITE,
                            currentCompany.website);
                    myBundle.putBoolean(FirebaseApplication.COMPANY_OPTCPT,
                            currentCompany.optcpt);
                    myBundle.putBoolean(FirebaseApplication.COMPANY_SPONSOR,
                            currentCompany.sponsor);

                    Intent i = new Intent(getActivity(), CompanyDetailsActivity.class);
                    i.putExtras(myBundle);
                    startActivity(i);
                }
            });

            mCompanyName = (TextView) itemView.findViewById(R.id.company_name);

            mCompanyLocation = (TextView) itemView.findViewById(R.id
                    .company_table);

            mCompanyLogo = (ImageView) itemView.findViewById(R.id.company_logo);

            mCompanySave = (ImageButton) itemView.findViewById(R.id.save_company);
            mCompanySave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!MainActivity.isInDatabase(currentCompany.name)) { //Change to remove icon
                        // TODO: Snackbar instead of toast?
                        Toast.makeText(getContext(), R.string.star, Toast.LENGTH_SHORT).show();
                        // TODO: lol ic unfav and fav are swapped names
                        mCompanySave.setImageResource(R.drawable.ic_unfavorite);
                        MainActivity.addRow(currentCompany.id,
                                currentCompany.name);
                    } else {
                        Toast.makeText(getContext(), R.string.unstar, Toast.LENGTH_SHORT).show();
                        mCompanySave.setImageResource(R.drawable.ic_favorite);
                        MainActivity.deleteRow(currentCompany.id);
                    }
                }
            });

        }

    }

    private class CompanyAdapter extends RecyclerView.Adapter<CompanyHolder> {
        public List<FirebaseCompany> companies;

        public CompanyAdapter(List<FirebaseCompany> companies) {
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
            FirebaseCompany currentCompany = companies.get(position);
            holder.currentCompany = currentCompany;
            holder.mCompanyName.setText(currentCompany.name);
            holder.mCompanyLocation.setText("Table " + currentCompany
                    .location);

            StorageReference path = storageRef.child("logos/" +
                    currentCompany.getId() + ".png");

            // Log.d("Firebase", path.toString() + ": " +  path.hashCode() +
            // "");

            Glide.with(getContext())
                    .using(new FirebaseImageLoader())
                    .load(path)
                    .into(holder.mCompanyLogo);

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
     List<ParseObject> list = FirebaseApplication.getCompanyPOS();


     for (ParseObject po : list) {
     Company c = new Company(po.getObjectId(),
     po.getString(FirebaseApplication.COMPANY_NAME),
     (ArrayList<String>) po.get(FirebaseApplication.COMPANY_MAJORS),
     po.getParseFile(FirebaseApplication.COMPANY_LOGO)
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
