package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.R;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

public class DrawerAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> arrayList = new ArrayList<>();
    LayoutInflater layoutInflater;
    private int lastAdded;

    public DrawerAdapter(Context context, ArrayList<String> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_setting, parent, false);
            holder = new ViewHolder();

            holder.txt = convertView.findViewById(R.id.txt);
            holder.ll_main = convertView.findViewById(R.id.ll_main);
            holder.rl_back = convertView.findViewById(R.id.rl_back);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (Constant.NAV_SELECTED_POS == position) {
            holder.rl_back.setBackground(context.getResources().getDrawable(R.drawable.drwer_design));
            holder.txt.setTextColor(context.getResources().getColor(R.color.purple_200));
        } else {
            holder.rl_back.setBackground(null);
            holder.txt.setTextColor(context.getResources().getColor(R.color.white));
        }

        holder.txt.setText(arrayList.get(position));

        return convertView;
    }

    class ViewHolder {
        TextView txt;
        LinearLayout ll_main;
        RelativeLayout rl_back;
    }
}


