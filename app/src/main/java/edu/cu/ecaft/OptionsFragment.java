package edu.cu.ecaft;

/**
 * Created by laura on 1/24/2017.
 */
import android.app.DialogFragment;
import android.os.Bundle;
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
    String[] options = {"Alatvel", "Apple"};
    //Array of something checkboxes???

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.options, null);

        getDialog().setTitle("Filter by Majors");

        list = (ListView) rootView.findViewById(R.id.options);
        filter = (Button) rootView.findViewById(R.id.filter);

        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, options);
        list.setAdapter(adapter);

        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return rootView;
    }

}
