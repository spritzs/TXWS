package cn.txws.board.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.txws.board.R;


/**
 * Created by Administrator on 2018/1/26 0026.
 */

public class ColorGridViewAdapter extends ArrayAdapter<Integer> {
    private LayoutInflater mInflater;
    int mSelecterItem=0;
    int mSelecterColor= Color.BLACK;

    public ColorGridViewAdapter(@NonNull Context context, @NonNull Integer[] objects) {
        super(context, 0, objects);
        mInflater=LayoutInflater.from(context);
    }

    public void setSelecterItem(int selecterItem){
        this.mSelecterItem=selecterItem;
        mSelecterColor=colorSum.get(mSelecterItem);
        notifyDataSetChanged();
    }

    public int getSelecterColor(){
        return mSelecterColor;
    }
    public int getSelecterItem(){
        return mSelecterItem;
    }
    public void setSelecterColor(int selecterColor){
        mSelecterColor=selecterColor;
        mSelecterItem=colorSum.indexOf(mSelecterColor);
        notifyDataSetChanged();
    }
    List<Integer> colorSum=new ArrayList<Integer>();
    {
        colorSum.add(0xFF000000);
        colorSum.add(0xFF3b68b9);
        colorSum.add(0xFFf6923b);
        colorSum.add(0xFF27bcb8);
        colorSum.add(0xFF96d0a7);
        colorSum.add(0xFFe1eec3);
        colorSum.add(0xFF763aab);
        colorSum.add(0xFFf04f54);
        colorSum.add(0xFFf48a94);
        colorSum.add(0xFFf7d91e);
        colorSum.add(0xFFeee565);
        colorSum.add(0xFFFFFFFF);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView!=null){
            vh=(ViewHolder)convertView.getTag();
        }else{
            convertView=mInflater.inflate(R.layout.color_img,null,false);
            vh=new ViewHolder(convertView);
        }
        vh.img.setImageResource(getItem(position));
        vh.img.setSelected(position==mSelecterItem);
        return convertView;
    }

    class ViewHolder {
        ImageView img;
        public ViewHolder(View v){
            img=(ImageView)v.findViewById(R.id.img);
            v.setTag(this);
        }
    }

}
