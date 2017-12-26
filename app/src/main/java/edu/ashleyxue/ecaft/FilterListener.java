package edu.ashleyxue.ecaft;

import java.util.HashMap;

/**
 * Created by robotbf on 12/26/17.
 */

public interface FilterListener {
    public void onFilterChanged(HashMap<String, String []> choices);

    public void expandGroupEvent(int groupPosition, boolean isExpanded);
}
