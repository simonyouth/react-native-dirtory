package com.example.choosedir;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zyx on 2018/3/3.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>{
    private List<String> data;
    private Context context;
    private int layoutId;
    private Event event;
    private ArrayList<String> selectData;
    int mSelectPosition = -1;

    public FileAdapter(List<String> data, Context context, int layoutId) {
        this.data = new ArrayList<>();
        this.selectData = new ArrayList<>();
        this.data.clear();
        this.data.addAll(data);
        this.context = context;
        this.layoutId = layoutId;
    }

    public void setData(List<String> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void addData(String content) {
        this.data.add(content);
        notifyDataSetChanged();
    }

    public String getDataByPostion(int postion) {
        if (data.size() <= postion)
            return "";
        return data.get(postion);
    }
    public List<String> getAllData() {
        return data;
    }
    public void clearAllData() {
        this.data.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String path = data.get(position);
        if(mSelectPosition ==position){
            if (selectData.contains(path)) {//如果集合中包含了该元素则直接返回
                selectData.clear();
                holder.checkBox.setChecked(false);
            }
            else//否则添加
            {
                selectData.clear();
                selectData.add(path);
                holder.checkBox.setChecked(true);
            }

        }else {
            holder.checkBox.setChecked(false);
        }


        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectPosition =position ;
                notifyDataSetChanged();
            }

        });
        holder.tvFileDir.setText(path.substring(path.lastIndexOf("/") + 1));
        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.checkBox.isChecked()) //获取checkBox的选中状态来判断是否能进入下一个文件夹
                {
                    event.enterNextDir(data.get(position));
                } else {
                    Toast.makeText(context, context.getString(R.string.filechoose_already), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<String> getSelectData() {
        return selectData;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFileDir;
        private ConstraintLayout rootLayout;
        private RadioButton checkBox;

        public ViewHolder(View view) {
            super(view);
            tvFileDir = (TextView) view.findViewById(R.id.tvFileDir);
            rootLayout = (ConstraintLayout) view.findViewById(R.id.rootLayout);
            checkBox = (RadioButton) view.findViewById(R.id.checkBox);
        }
    }

    /**
     * 在主界面中需要进行的操作
     */
    public interface Event {
        void enterNextDir(String path);//进入下一个文件夹
    }

}

