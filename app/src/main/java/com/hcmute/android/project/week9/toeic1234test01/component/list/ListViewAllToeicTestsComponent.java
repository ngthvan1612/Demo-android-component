package com.hcmute.android.project.week9.toeic1234test01.component.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hcmute.android.project.week9.toeic1234test01.R;
import com.hcmute.android.project.week9.toeic1234test01.component.event.OnListViewItemClicked;
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicFullTest;

import java.util.List;

public class ListViewAllToeicTestsComponent extends ListView {
    private final Context rootContext;
    private OnToeicTestClicked onToeicTestClicked;

    public ListViewAllToeicTestsComponent(Context context) {
        super(context);
        this.rootContext = context;
        this.initComponent();
    }

    public ListViewAllToeicTestsComponent(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.rootContext = context;
        this.initComponent();
    }

    private void initComponent() {
        this.setBackgroundColor(this.rootContext.getColor(R.color.light_gray));
        this.setDivider(new ColorDrawable(Color.TRANSPARENT));
    }

    public void renderListToeicTests(List<ToeicFullTest> toeicFullTests) {
        this.setAdapter(new InnerAdapter(toeicFullTests));
    }

    public void setOnListViewItemClicked(OnToeicTestClicked onToeicTestClicked) {
        this.onToeicTestClicked = onToeicTestClicked;
    }

    private class InnerAdapter extends BaseAdapter {
        private final List<ToeicFullTest> toeicFullTestList;

        public InnerAdapter(
                List<ToeicFullTest> toeicFullTests
        ) {
            this.toeicFullTestList = toeicFullTests;
        }

        @Override
        public int getCount() {
            return this.toeicFullTestList.size();
        }

        @Override
        public Object getItem(int position) {
            return this.toeicFullTestList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)rootContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.component_list_view_toeic_test_item, null);
            ToeicFullTest toeicFullTest = this.toeicFullTestList.get(position);

            TextView txtTitle = view.findViewById(R.id.component_list_view_toeic_test_title);
            txtTitle.setText(toeicFullTest.getFullName());

            view.setOnClickListener(v -> {
                if (onToeicTestClicked != null) {
                    onToeicTestClicked.onClick(toeicFullTest);
                }
            });

            return view;
        }
    }

    public interface OnToeicTestClicked extends OnListViewItemClicked<ToeicFullTest> {

    }
}
