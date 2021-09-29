package com.fengdi.voiceintellect.app.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.fengdi.voiceintellect.R;

import org.heiyiren.app.app.callback.MyOnItemClickListener;

import java.util.ArrayList;

@SuppressLint("NewApi")
/**
 * 下拉列表框控件
 * @author caizhiming
 *
 */
public class MySpinner extends LinearLayout {

    private TextView editText;
    private PopupWindow popupWindow = null;
    private String defaultText = "";
    private boolean needHint;
    private float textSize;
    private ArrayList<String> dataList = new ArrayList<String>();
    private MyOnItemClickListener<String> onItemClickListener;

    public void setOnItemClickListener(MyOnItemClickListener<String> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MySpinner(Context context) {
        this(context, null);
    }

    public MySpinner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySpinner);
        defaultText = typedArray.getString(R.styleable.MySpinner_defaultText);
        needHint = typedArray.getBoolean(R.styleable.MySpinner_needHint, false);
        textSize = typedArray.getDimension(R.styleable.MySpinner_textSize, sp2px(12));

        initView();
    }


    protected int sp2px(float sp) {
        final float scale = this.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    public void initView() {
        String infServie = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) getContext().getSystemService(infServie);
        View view = layoutInflater.inflate(R.layout.dropdownlist_view, this, true);
        editText = findViewById(R.id.text);
        editText.getPaint().setFakeBoldText(true);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
        editText.setText(defaultText);
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (popupWindow == null) {
                    showPopWindow();
                } else {
                    closePopWindow();
                }
            }
        });
    }

    /**
     * 打开下拉列表弹窗
     */
    private void showPopWindow() {
        // 加载popupWindow的布局文件
        String infServie = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater layoutInflater;
        layoutInflater = (LayoutInflater) getContext().getSystemService(infServie);
        View contentView = layoutInflater.inflate(R.layout.dropdownlist_popupwindow, null, false);
        ListView listView = (ListView) contentView.findViewById(R.id.listView);

        listView.setAdapter(new XCDropDownListAdapter(getContext(), dataList));
        popupWindow = new PopupWindow(contentView, getMeasuredWidth(), LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(this);
    }

    /**
     * 关闭下拉列表弹窗
     */
    private void closePopWindow() {
        popupWindow.dismiss();
        popupWindow = null;
    }

    /**
     * 设置数据
     *
     * @param list
     */
    public void setItemsData(ArrayList<String> list) {
        dataList = list;
        if (!needHint) {
            editText.setText(list.get(0));
        }
    }

    /**
     * 数据适配器
     *
     * @author caizhiming
     */
    class XCDropDownListAdapter extends BaseAdapter {

        Context mContext;
        ArrayList<String> mData;
        LayoutInflater inflater;

        public XCDropDownListAdapter(Context ctx, ArrayList<String> data) {
            mContext = ctx;
            mData = data;
            inflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            // 自定义视图
            ListItemView listItemView = null;
            if (convertView == null) {
                // 获取list_item布局文件的视图
                convertView = inflater.inflate(R.layout.item_dropdown_list, null);

                listItemView = new ListItemView();
                // 获取控件对象
                listItemView.tv = (TextView) convertView
                        .findViewById(R.id.tvName);

                listItemView.tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);


                listItemView.layout = (LinearLayout) convertView.findViewById(R.id.layout_container);
                // 设置控件集到convertView
                convertView.setTag(listItemView);
            } else {
                listItemView = (ListItemView) convertView.getTag();
            }

            // 设置数据
            listItemView.tv.setText(mData.get(position).toString());
            final String text = mData.get(position).toString();
            listItemView.layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, text, position);
                    }
                    editText.setText(text);
                    closePopWindow();
                }
            });
            return convertView;
        }

    }

    private static class ListItemView {
        TextView tv;
        LinearLayout layout;
    }

}
