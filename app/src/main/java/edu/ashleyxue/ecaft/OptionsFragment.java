package edu.ashleyxue.ecaft;

/**
 * Created by laura on 1/24/2017.
 */

import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class OptionsFragment extends DialogFragment{

    Button filter;
    ListView list;
    ArrayAdapter<String> adapter;
    String[] options = {"Aerospace Engineering",
            "Atmospheric Science",
            "Biological Engineering",
            "Biomedical Engineering",
            "Biological and Environmental ",
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
    //Array of something checkboxes???

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.options, null);

        getDialog().setTitle("Filter by Majors");

        list = (ListView) rootView.findViewById(R.id.options);
        filter = (Button) rootView.findViewById(R.id.filter);

        adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);

        Log.d("RA MEETING AT 9:30", "ISSAIAH");

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return rootView;
    }

}
