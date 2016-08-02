package com.tw.go.task.fortify;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Comparator;

public class FortifySortBasedOnUploadDate implements Comparator<JSONObject>
{
    @Override
    public int compare(JSONObject lhs, JSONObject rhs)
    {
        try
        {
            if(lhs.getString("uploadDate").compareTo(rhs.getString("uploadDate")) == -1)
            {
                return -1;
            }

            if(lhs.getString("uploadDate").compareTo(rhs.getString("uploadDate")) == 0)
            {
                return 0;
            }

            if(lhs.getString("uploadDate").compareTo(rhs.getString("uploadDate")) == 1)
            {
                return 1;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return 0;
    }
}
