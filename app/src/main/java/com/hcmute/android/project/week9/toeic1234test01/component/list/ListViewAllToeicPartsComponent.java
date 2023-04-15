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
import com.hcmute.android.project.week9.toeic1234test01.model.ToeicPart;

import java.util.List;

public class ListViewAllToeicPartsComponent extends ListView {
    private final Context rootContext;
    private OnToeicPartItemClicked onToeicPartItemClicked;

    public ListViewAllToeicPartsComponent(Context context) {
        super(context);
        this.rootContext = context;
        this.initComponent();
    }

    public ListViewAllToeicPartsComponent(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.rootContext = context;
        this.initComponent();
    }

    private void initComponent() {
        this.setBackgroundColor(this.rootContext.getColor(R.color.light_gray));
        this.setDivider(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setOnToeicPartItemClicked(OnToeicPartItemClicked onToeicPartItemClicked) {
        this.onToeicPartItemClicked = onToeicPartItemClicked;
    }

    public void renderListToeicParts(List<ToeicPart> toeicParts) {
        this.setAdapter(new ListViewAllToeicPartsComponent.InnerAdapter(toeicParts));
    }

    private class InnerAdapter extends BaseAdapter {
        private final List<ToeicPart> toeicFullTestList;

        public InnerAdapter(
                List<ToeicPart> toeicFullTests
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

        private String getDescriptionByPartId(Integer partId) {
            switch (partId) {
                case 1: return "Mô tả hình ảnh";
                case 2: return "Hỏi - đáp";
                case 3: return "Đoạn hội thoại";
                case 4: return "Bài độc thoại";
                default: throw new RuntimeException("Unknown part id" + partId);
            }
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)rootContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.component_list_view_toeic_part_item, null);
            ToeicPart toeicPart = this.toeicFullTestList.get(position);

            TextView txtTitle = view.findViewById(R.id.component_list_view_toeic_part_item_title);
            txtTitle.setText("Part " + toeicPart.getPartId());

            TextView txtType = view.findViewById(R.id.component_list_view_toeic_part_item_type);
            txtType.setText("Part " + toeicPart.getPartId());

            TextView txtDescription = view.findViewById(R.id.component_list_view_toeic_part_item_description);
            txtDescription.setText(this.getDescriptionByPartId(toeicPart.getPartId()));

            view.setOnClickListener(v -> {
                if (onToeicPartItemClicked != null) {
                    onToeicPartItemClicked.onClick(toeicPart);
                }
            });

            return view;
        }
    }

    public interface OnToeicPartItemClicked extends OnListViewItemClicked<ToeicPart> {

    }
}
