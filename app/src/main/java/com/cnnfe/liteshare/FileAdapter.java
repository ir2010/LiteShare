package com.cnnfe.liteshare;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.MyViewHolder>
{
    private ArrayList<String> iconsList = new ArrayList<>();
    private ArrayList<String> fnameList = new ArrayList<>();
    private Context mContext;

    public FileAdapter(ArrayList<String> iconsList, ArrayList<String> fnameList, Context mContext) {
        this.iconsList = iconsList;
        this.fnameList = fnameList;
        this.mContext = mContext;
    }

    //recycles the viewholder
    @Override
    public FileAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent,false);

        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //holder.icon.setImageIcon(Icon.createWithContentUri(iconsList.get(position)));
        holder.icon.setText(iconsList.get(position));
        holder.name.setText(fnameList.get(position));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "ok", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView icon;
        public  TextView name;
        RelativeLayout relativeLayout;
        public MyViewHolder(View v)
        {
            super(v);
            icon = v.findViewById(R.id.icon);
            name = v.findViewById(R.id.name);
            relativeLayout = v.findViewById(R.id.relativeLayout);
        }
    }

    @Override
    public int getItemCount() {
        return  fnameList.size();
    }
}
