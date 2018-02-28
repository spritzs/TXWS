package cn.txws.board.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.robotpen.views.widget.WhiteBoardView;
import cn.txws.board.R;
import cn.txws.board.database.data.BlockItemData;


/**
 * Created by Administrator on 2018/1/26 0026.
 */

public class GridViewAdapter extends CursorRecyclerAdapter<GridViewAdapter.ViewHolder>{
    private LayoutInflater mInflater;
    private OnMoreClickListener mOnMoreClickListener;
    private OnItemClickListner onItemClickListner;//单击事件
    private Bitmap mBitmap;

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
                if(onItemClickListner!=null){
                    onItemClickListner.onItemClickListner(v,data.getBlockID());
                }
            }
        });

        Glide.with(mContext).load(data.getImage()).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.drawable.note_bg_noshade)
                .placeholder(R.drawable.note_bg_noshade).skipMemoryCache(true).into(holder.img);
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
        ImageView more;
        public ViewHolder(View v){
            super(v);
            main=v;
            title=(TextView)v.findViewById(R.id.title);
            img=(ImageView)v.findViewById(R.id.img);
            more=(ImageView)v.findViewById(R.id.ic_more);
        }
    }

    public void setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

    public interface OnItemClickListner {
        void onItemClickListner(View v,String blockid);
    }

    public interface OnMoreClickListener{
        void onClick(View v,int position,String blockid);
    }
}
