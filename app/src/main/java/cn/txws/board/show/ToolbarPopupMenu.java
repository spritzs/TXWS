package cn.txws.board.show;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import cn.txws.board.R;

/**
 * Created by zqs on 2018/2/27 0027.
 */

public class ToolbarPopupMenu extends PopupWindow{
//    View popLayout;
    ListView mListView;
    Context mContext;
    OnItemClickListener mListener;

    View mPopView;

    public ToolbarPopupMenu(Context context,Integer[] reses) {
        super(context);
        mContext=context;
        init(reses);
    }

    private void init(Integer[] reses) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mPopView = inflater.inflate(R.layout.toolbar_pop_layout, null);
        setContentView(mPopView);
        setOutsideTouchable(true);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(mContext.getResources().getDimensionPixelOffset(R.dimen.toolbar_pop));
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        this.setFocusable(true);
//        popLayout=mPopView.findViewById(R.id.id_pop_layout);
        mListView= (ListView) mPopView.findViewById(R.id.list);
        PopAdapter adapter=new PopAdapter(mContext,0,reses);
        mListView.setAdapter(adapter);
    }

    public void showPop(View parent){
        int[] location = new int[2];
        //获取在整个屏幕内的绝对坐标
        parent.getLocationOnScreen(location);
        int left = location[0] + parent.getWidth()/2 - mContext.getResources().getDimensionPixelOffset(R.dimen.toolbar_pop)/2;
        int top = location[1] + parent.getHeight() ;
        this.showAtLocation(parent, 0, left, top);
    }


    public class PopAdapter extends ArrayAdapter<Integer>{
        public Integer[] images;
        LayoutInflater inflater;
        public PopAdapter(@NonNull Context context, @LayoutRes int resource,Integer[] array) {
            super(context, resource,array);
            images=array;
            inflater=LayoutInflater.from(context);
        }

        class ViewHolder{
            public ImageView img;
            public ViewHolder(View v){
                img= (ImageView) v.findViewById(R.id.image);
                v.setTag(this);
            }
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if(convertView!=null){
                holder= (ViewHolder) convertView.getTag();
            }else{
                convertView=inflater.inflate(R.layout.toolbar_pop_item,null);
                holder=new ViewHolder(convertView);
            }
            holder.img.setImageResource(getItem(position));
            if(mListener!=null){
                holder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemClick(position);
                    }
                });
            }
            return convertView;
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int postion);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }
}
