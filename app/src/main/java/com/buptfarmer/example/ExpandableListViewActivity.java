package com.buptfarmer.example;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.woozzu.android.indexablelistview.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExpandableListViewActivity extends Activity {
    private ExpandableListView mListView;
    private BaseExpandableListAdapter mAdapter;
    private ArrayList<String> mGroutList;
    private ArrayList<String> mChildList;
    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expandable_list_view);
        mInflater = LayoutInflater.from(this);
        mGroutList = new ArrayList<>();
        mChildList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int index = 0; index < 30; index++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, index);
            String value = sdf.format(cal.getTime());
            mGroutList.add(value);
        }
        for (int index = 0; index < 10; index++) {
            mChildList.add("child value:" + index);
        }
        mListView = (ExpandableListView) findViewById(R.id.expand_list_view);
        mAdapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return mGroutList.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return mChildList.size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return mGroutList.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return mChildList.get(childPosition);
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
                return false;
            }

            @Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.expand_list_group_layout, null);
                }
                TextView groupTitle = (TextView) convertView.findViewById(R.id.group_title);
                groupTitle.setText(mGroutList.get(groupPosition));
                return convertView;
            }

            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.expand_list_group_layout, null);
                }
                TextView groupTitle = (TextView) convertView.findViewById(R.id.group_title);
                groupTitle.setText(mChildList.get(childPosition));
                return convertView;
            }

            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                return false;
            }

        };
        mListView.setAdapter(mAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_expandable_list_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
