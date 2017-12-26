package edu.ashleyxue.ecaft;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import android.widget.ExpandableListView;


/**
 * Created by Laura Lin on 11/29/17.
 */

public class FilterFragment extends Fragment {
    View rootView;
    ExpandableListView lv;
    private String[] labels;
    private HashMap<String, String[]> options;

    public FilterFragment(){}


    public HashMap<String, String[]> getData(){
        HashMap<String, String[]> filterOptions = new HashMap<String, String[]>();
        String[] majors =  {
                "Aerospace Engineering",
                "Atmospheric Science",
                "Biological Engineering",
                "Biomedical Engineering",
                "Biological and Environmental",
                "Chemical Engineering",
                "Civil Engineering",
                "Computer Science",
                "Electrical and Computer Engineering",
                "Engineering Management",
                "Engineering Physics",
                "Environmental Engineering",
                "Information Science",
                "Materials Science and Engineering",
                "Mechanical Engineering",
                "Operations Research and Information Engineering",
                "Systems Engineering"};

        String [] jobOptions = { "Co-op", "Full-time", "Internship", "Other"};

        String [] other = {"Sponsorship required"};

        filterOptions.put("Majors", majors);
        filterOptions.put("Open positions", jobOptions);
        filterOptions.put("Other", other);

        return filterOptions;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        options = getData();
        labels = options.keySet().toArray(new String[0]);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        lv = (ExpandableListView) view.findViewById(R.id.expandableListView);

        lv.setAdapter(new ExpandableListAdapter(labels, options));

    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final LayoutInflater inf;
        private Context context;
        private HashMap<String, String[]> options;
        private String[] labels;

        public ExpandableListAdapter(String[] labels, HashMap<String, String[]> options){
            this.options = options;
            this.labels = labels;
            inf =  getActivity().getLayoutInflater();
        }

        @Override
        public int getGroupCount(){
            return labels.length;
        }

        @Override
        public int getChildrenCount(int groupPosition){
            return options.get(labels[groupPosition]).length;
        }

        @Override
        public Object getGroup(int groupPosition){
            return labels[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition){
            return options.get(labels[groupPosition])[childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            final String expandedListText = (String) getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.filter_item, null);
            }
            TextView expandedListTextView = (TextView) convertView.findViewById(R.id.item_text);
            expandedListTextView.setText(expandedListText);
            return convertView;
        }

        @Override
        public View getGroupView(int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);
            if (convertView == null) {
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.filter_group, null);
            }
            TextView listTitleTextView = (TextView) convertView
                    .findViewById(R.id.listTitle);
            listTitleTextView.setTypeface(null, Typeface.BOLD);
            listTitleTextView.setText(listTitle);
            return convertView;
        }

    }
}
