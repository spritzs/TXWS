package cn.txws.board.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.robotpen.views.widget.WhiteBoardView;
import cn.txws.board.R;
import cn.txws.board.database.data.BlockItemData;


/**
 * Created by zqs on 2018/1/26 0026.
 * 主界面所有画板界面adpter
 */

public class GridViewAdapter extends CursorRecyclerAdapter<GridViewAdapter.ViewHolder>{
    private LayoutInflater mInflater;
    private OnMoreClickListener mOnMoreClickListener;
    private OnItemClickListner onItemClickListner;//单击事件
    private OnLongClickListener onLongClickListner;//单击事件

    private boolean isSelectorMode=false;
    private List<String> selectorList=new ArrayList<String>();
    private List<String> selectorPictureList=new ArrayList<String>();

    public void setSelectorMode(boolean selectorMode){
        isSelectorMode=selectorMode;
        if(!selectorMode){
            selectorList.clear();
            selectorPictureList.clear();
        }
        notifyDataSetChanged();
    }

    public List<String> getSelectorList(){
        return selectorList;
    }

    public List<String> getSelectorPictureList(){
        return selectorPictureList;
    }


    public void allPickOrUnPick(TextView text){
        if(getItemCount()==selectorList.size()){
            selectorList.clear();
            selectorPictureList.clear();
            text.setText(R.string.allpick);
        }else{
            for(int i=0;i<getItemCount();i++){
                final BlockItemData data=new BlockItemData();
                data.bind((Cursor) getItem(i));
                String block=data.getBlockID();
                String path=data.getImage();
                if(!selectorList.contains(block)){
                    selectorList.add(block);
                    selectorPictureList.add(path);
                }
            }
            text.setText(R.string.unallpick);
        }
        notifyDataSetChanged();
    }


    public boolean getIsSelectorMode(){
        return isSelectorMode;
    }

    public GridViewAdapter(Context context, Cursor cursor) {
        super(context,cursor,0);
        mInflater=LayoutInflater.from(context);
    }
    @Override
    public void bindViewHolder(final ViewHolder holder, Context context, final Cursor cursor) {
        final BlockItemData data=new BlockItemData();
        data.bind(cursor);
        holder.title.setText(data.getName());
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnMoreClickListener!=null){
                    mOnMoreClickListener.onClick(holder.more,cursor.getPosition(),data.getBlockID());
                }
            }
        });
        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelectorMode){
                    if(holder.checkbox.isChecked()){
                        holder.checkbox.setChecked(false);
                        selectorList.remove(data.getBlockID());
                        selectorPictureList.remove(data.getImage());
                    }else{
                        holder.checkbox.setChecked(true);
                        selectorList.add(data.getBlockID());
                        selectorPictureList.add(data.getImage());
                    }
                }
                if(onItemClickListner!=null){
                    onItemClickListner.onItemClickListner(v,data.getBlockID());
                }
            }
        });

        holder.main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(onLongClickListner!=null){
                    if(!isSelectorMode){
                        selectorList.add(data.getBlockID());
                        selectorPictureList.add(data.getImage());
                    }
                    onLongClickListner.onLongClick();
                }
                return true;
            }
        });

        holder.checkbox.setVisibility(isSelectorMode?View.VISIBLE:View.INVISIBLE);
        if(isSelectorMode){
            holder.checkbox.setChecked(selectorList.contains(data.getBlockID()));
        }
        Glide.with(mContext).load(data.getImage()).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(R.drawable.note_bg_noshade).skipMemoryCache(true).into(holder.img);

    }

    @Override
    public ViewHolder createViewHolder(Context context, ViewGroup parent, int viewType) {
        View v=mInflater.inflate(R.layout.gridview_item,null);
        final ViewHolder holder=new ViewHolder(v);
        return holder;
    }


    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        this.mOnMoreClickListener = onMoreClickListener;
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        View main;
        TextView title;
        ImageView img;
        CheckBox checkbox;
        ImageView more;
        public ViewHolder(View v){
            super(v);
            main=v;
            title=(TextView)v.findViewById(R.id.title);
            img=(ImageView)v.findViewById(R.id.img);
            checkbox=(CheckBox)v.findViewById(R.id.checkbox);
            more=(ImageView)v.findViewById(R.id.ic_more);
        }
    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListner) {
        this.onLongClickListner = onLongClickListner;
    }

    public interface OnItemClickListner {
        void onItemClickListner(View v,String blockid);
    }

    public interface OnMoreClickListener{
        void onClick(View v,int position,String blockid);
    }
    public interface OnLongClickListener{
        void onLongClick();
    }
}
