package com.leon.lamti.cc.home;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leon.lamti.cc.R;
import com.leon.lamti.cc.games_activities.ShapesCopyActivity;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CustomViewHolder> {
    
    private List<String> menuList;
    private Context mContext;
    private int systemChoice;

    public RecyclerAdapter(Context context, List<String> menuList, int systemChoice ) {

        this.menuList = menuList;
        this.mContext = context;
        this.systemChoice = systemChoice;

        /*if ( systemChoice == 1) {

            menuList.remove(0);
            menuList.remove(1);
            menuList.remove(1);
            menuList.remove(3);
            menuList.remove(3);
            menuList.remove(3);
            menuList.remove(3);
            menuList.remove(3);
        }*/
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder,final int i) {

        //Setting text view title
        customViewHolder.menuItem.setText( menuList.get(i) );
        //customViewHolder.imageView.setImageResource(R.mipmap.ic_ac_lt);

        customViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mContext, "" +  menuList.get(i).toString(), Toast.LENGTH_SHORT).show();
                if ( menuList.get(i).toString().equals(mContext.getResources().getString(R.string.shapes_copy)) ) {
                    Intent intent = new Intent(mContext, ShapesCopyActivity.class);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != menuList ? menuList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView menuItem;
        CardView cardView;
        ImageView imageView;

        public CustomViewHolder(View view) {
            super(view);

            menuItem = (TextView) view.findViewById(R.id.menuItemNameTV);
            cardView = (CardView) view.findViewById(R.id.cardView);
            imageView = (ImageView) view.findViewById(R.id.imageView100);
        }

        public TextView getMenuItem() {
            return menuItem;
        }

        public void setMenuItem(TextView menuItem) {
            this.menuItem = menuItem;
        }
    }
}