package edu.lauralin.ecaft;

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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import android.widget.ExpandableListView;


/**
 * Created by Laura Lin on 11/29/17.
 */

public class FilterFragment extends Fragment {
    View rootView;
    ExpandableListView lv;
    private ArrayList<String> labels;
    private HashMap<String, List<String>> options;
    static HashMap<Integer, boolean[] > mChildCheckStates = InfoFragment.prevFilterOptions;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    static HashMap<Integer, List<String>> filterOptions = new HashMap<Integer, List<String>>();
    public FilterFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ArrayList<String> majors = new ArrayList<>(Arrays.asList(
                "All Majors",
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
                "Systems Engineering"));

        ArrayList<String> jobOptions = new ArrayList<>(Arrays.asList(
                "All Types", "Co-op", "Full-time", "Internship", "Other"));

        ArrayList<String> other =  new ArrayList<>(Arrays.asList(
                "Sponsorship Required"));
        options = new HashMap<String, List<String>>();
        options.put("Majors", majors);
        options.put("Open Positions", jobOptions);
        options.put("Other", other);

        labels =  new ArrayList<>(Arrays.asList(
                "Majors", "Open Positions", "Other"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle("Filter");
        return inflater.inflate(R.layout.filter_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        lv = (ExpandableListView) view.findViewById(R.id.expandableListView);

        listAdapter = new ExpandableListAdapter(labels, options);

        lv.setAdapter(listAdapter);

        lv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        labels.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });
        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                CheckBox cb = (CheckBox) v.findViewById(R.id.item_check_box);

                if( cb != null )
                    cb.toggle();

                if (cb.isChecked()) {
                    // TODO Auto-generated method stub
                    Toast.makeText(
                            getActivity().getApplicationContext(),
                            labels.get(groupPosition)
                                    + " : "
                                    + options.get(
                                    labels.get(groupPosition)).get(
                                    childPosition), Toast.LENGTH_SHORT)
                            .show();
                }
                return false;
            }
        });
        Button filterButton = (Button) view.findViewById(R.id.submitFilter);

        filterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                filterOptions = new HashMap<Integer, List<String>>();
                for(int mGroupPosition =0; mGroupPosition < listAdapter.getGroupCount(); mGroupPosition++)
                {
                    List<String> mGroupOptions = listAdapter.getCheckedItemsInGroup(mGroupPosition);
                    filterOptions.put(mGroupPosition, mGroupOptions);
                }
                getFragmentManager().popBackStack();
            }
        });
        Button closeButton = (Button) view.findViewById(R.id.closeFilter);

        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        Button clearButton = (Button) view.findViewById(R.id.clearFilter);

        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                filterOptions = new HashMap<Integer, List<String>>();
                mChildCheckStates.clear();
                boolean majors[] = new boolean[18];
                boolean jobs[] = new boolean[5];
                majors[0] = true;
                jobs[0] = true;
                mChildCheckStates.put(0, majors);
                mChildCheckStates.put(1, jobs);
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    public final class GroupViewHolder {

        TextView mGroupText;
    }

    public final class ChildViewHolder {

        TextView mChildText;
        CheckBox mCheckBox;
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private final LayoutInflater inf;
        private Context mContext;
        private HashMap<String, List<String>> moptions;
        private List<String>  mlabels;
        private ChildViewHolder childViewHolder;
        private GroupViewHolder groupViewHolder;
        private String groupText;
        private String childText;

        public ExpandableListAdapter( List<String> labels, HashMap<String, List<String>> options){
            this.moptions = options;
            this.mlabels = labels;
            mChildCheckStates = InfoFragment.prevFilterOptions;
            if (mChildCheckStates.isEmpty()){
                boolean majors[] = new boolean[18];
                boolean jobs[] = new boolean[5];
                majors[0] = true;
                jobs[0] = true;
                mChildCheckStates.put(0, majors);
                mChildCheckStates.put(1, jobs);
            }
            this.inf =  getActivity().getLayoutInflater();
        }

        public List<String> getGroupItems (int mGroupPosition){
            if(mGroupPosition == 0)
                return new ArrayList<>(Arrays.asList(
                        "All Majors",
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
                        "Systems Engineering"));
            else if (mGroupPosition == 1)
                return new ArrayList<>(Arrays.asList(
                        "All Types", "Co-op", "Full-time", "Internship", "Other"));
            return new ArrayList<>(Arrays.asList(
                    "Sponsorship Required"));
        }

        public  List<String> getCheckedItemsInGroup(int mGroupPosition){
            boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
            List<String> list = getGroupItems(mGroupPosition);
            List<String> wanted = new ArrayList <String>();
            if(getChecked != null) {
                for (int j = 0; j < getChecked.length; ++j) {
                    if (getChecked[j] == true)
                        wanted.add(list.get(j));
                }
            }
            return wanted;
        }

        @Override
        public int getGroupCount(){
            return mlabels.size();
        }

        @Override
        public int getChildrenCount(int groupPosition){
            return moptions.get(mlabels.get(groupPosition)).size();
        }

        @Override
        public String getGroup(int groupPosition){
            return mlabels.get(groupPosition);
        }

        @Override
        public String getChild(int groupPosition, int childPosition){
            return moptions.get(mlabels.get(groupPosition)).get(childPosition);
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
            final int mGroupPosition = groupPosition;
            final int mChildPosition = childPosition;
            childText = getChild(mGroupPosition, mChildPosition);

            if (convertView == null) {
                LayoutInflater inflater =  getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.filter_item, null);
                childViewHolder = new ChildViewHolder();
                childViewHolder.mChildText = (TextView) convertView
                        .findViewById(R.id.item_text);
                childViewHolder.mCheckBox = (CheckBox) convertView
                        .findViewById(R.id.item_check_box);
                convertView.setTag(R.layout.filter_item, childViewHolder);
            } else {
                childViewHolder = (ChildViewHolder) convertView
                        .getTag(R.layout.filter_item);
            }
            childViewHolder.mChildText.setText(childText);
            childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

            if (mChildCheckStates.containsKey(mGroupPosition)) {
                boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);
            } else {
                boolean getChecked[] = new boolean[getChildrenCount(mGroupPosition)];
                mChildCheckStates.put(mGroupPosition, getChecked);
                childViewHolder.mCheckBox.setChecked(false);
            }
            childViewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    boolean getChecked[] = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    if (childText.contains("Sponsorship")){
                        mChildCheckStates.put(mGroupPosition, getChecked);
                    }
                    else if (isChecked && (!childText.contains("All Types"))){
                        getChecked[0] = false;
                        mChildCheckStates.put(mGroupPosition, getChecked);
                        notifyDataSetChanged();

                    }
                    else if (isChecked && (!childText.contains("All Majors"))){
                        getChecked[0] = false;
                        mChildCheckStates.put(mGroupPosition, getChecked);
                        notifyDataSetChanged();

                    }

                }
            });

            return convertView;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            groupText = getGroup(groupPosition);

            if (convertView == null) {

                LayoutInflater inflater =  getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.filter_group, null);

                groupViewHolder = new GroupViewHolder();

                groupViewHolder.mGroupText = (TextView) convertView.findViewById(R.id.listTitle);

                convertView.setTag(groupViewHolder);
            } else {

                groupViewHolder = (GroupViewHolder) convertView.getTag();
            }

            groupViewHolder.mGroupText.setText(groupText);

            return convertView;
        }

    }
}
