package com.buptfarmer.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.buptfarmer.example.widget.IndexableExpandableListView;
import com.woozzu.android.indexablelistview.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ExpandableListViewActivity extends Activity {
    private static class IndexableExpandableListAdapter extends BaseExpandableListAdapter implements SectionIndexer {

        private ArrayList<String> mGroupList;
        private ArrayList<String> mChildList;
        private LayoutInflater mInlater;
        private IndexableExpandableListView mListView;
        public IndexableExpandableListAdapter(Activity activity, IndexableExpandableListView listView,ArrayList<String> groupList, ArrayList<String> childList) {
            mGroupList = groupList;
            mChildList = childList;
            mInlater = activity.getLayoutInflater();
            mListView = listView;
            Log.d("ccc", "init size:" + mGroupList.size());
        }

        @Override
        public Object[] getSections() {
            Log.d("ccc", "getSections size:" + mGroupList.size());
            String[] sections = new String[mGroupList.size()];
            for (int index = 0; index < mGroupList.size(); index++) {
                sections[index] = mGroupList.get(index);
            }
            return sections;
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            Log.d("ccc", "getPositionForSection sectionIndex:" + sectionIndex);
//            mListView.setSelectedChild(sectionIndex, 1, true);
//            mListView.setSelection(sectionIndex);
//            mListView.setSelectedGroup(sectionIndex);
//            long pos = mListView.getSelectedPosition();
//            int pos = mListView.getSelectedItemPosition();
            long packedPosition = mListView.getPackedPositionForGroup(sectionIndex);
//            Log.d("ccc", "getPositionForSection packedPosition:" + packedPosition);
            int flatPosition = mListView.getFlatListPosition(packedPosition);
//            Log.d("ccc", "getPositionForSection flatPosition:" + flatPosition);
            long pos = mListView.getExpandableListPosition(flatPosition);
//            Log.d("ccc", "getPositionForSection pos:" + pos);
//            mListView.setSelection();
            return flatPosition;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }


        @Override
        public int getGroupCount() {
            return mGroupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mChildList.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mGroupList.get(groupPosition);
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
                convertView = mInlater.inflate(R.layout.expand_list_group_layout, null);
            }
            TextView groupTitle = (TextView) convertView.findViewById(R.id.group_title);
            groupTitle.setText(mGroupList.get(groupPosition));
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInlater.inflate(R.layout.expand_list_group_layout, null);
            }
            TextView groupTitle = (TextView) convertView.findViewById(R.id.group_title);
            groupTitle.setText(mChildList.get(childPosition));
            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    private IndexableExpandableListView mListView;
    private IndexableExpandableListAdapter mAdapter;
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
        mListView = (IndexableExpandableListView) findViewById(R.id.expand_list_view);
        mAdapter = new IndexableExpandableListAdapter(this,mListView, mGroutList, mChildList);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
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
