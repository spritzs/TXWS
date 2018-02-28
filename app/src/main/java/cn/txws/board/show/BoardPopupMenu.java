package cn.txws.board.show;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.txws.board.R;
import cn.txws.board.util.DefaultAnimationHandler;

/**
 * Created by Administrator on 2018/2/27 0027.
 */

public class BoardPopupMenu extends PopupWindow implements View.OnClickListener{
    View shareView,renameView,deleteView,popLayout;
    Context mContext;
    OnItemClickListener mListener;

    View mPopView;
    List<View> mListView=new ArrayList<View>();

    public BoardPopupMenu(Context context) {
        super(context);
        mContext=context;
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mPopView = inflater.inflate(R.layout.popmenu_layout, null);
        setContentView(mPopView);
        setOutsideTouchable(true);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //mContext.getResources().getDimensionPixelOffset(R.dimen.popmenu_size)
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        this.setFocusable(true);
        popLayout=mPopView.findViewById(R.id.id_pop_layout);
        shareView=mPopView.findViewById(R.id.menu_share);
        renameView=mPopView.findViewById(R.id.menu_rename);
        deleteView=mPopView.findViewById(R.id.menu_delete);
        shareView.setOnClickListener(this);
        renameView.setOnClickListener(this);
        deleteView.setOnClickListener(this);
        popLayout.setOnClickListener(this);
        mListView.add(shareView);
        mListView.add(renameView);
        mListView.add(deleteView);

    }

    @Override
    public void dismiss() {
        DefaultAnimationHandler defaultAnimationHandler=new DefaultAnimationHandler(mListView);
        defaultAnimationHandler.animateMenuClosing(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                BoardPopupMenu.super.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                BoardPopupMenu.super.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void showPopupWindow(View parent) {
        if(this.isShowing()){
            dismiss();
        }else {
            int[] location = new int[2];
            //获取在整个屏幕内的绝对坐标
            parent.getLocationOnScreen(location);
            int left = location[0] + parent.getWidth() - mContext.getResources().getDimensionPixelOffset(R.dimen.popmenu_width);
            int top = location[1] + parent.getHeight() - mContext.getResources().getDimensionPixelOffset(R.dimen.popmenu_height);
            this.showAtLocation(parent, 0, left, top);
            DefaultAnimationHandler defaultAnimationHandler=new DefaultAnimationHandler(mListView);
            defaultAnimationHandler.animateMenuOpening();
        }
    }

    public interface OnItemClickListener{
        void share();
        void rename();
        void delete();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener=listener;
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_share:
                if(mListener!=null){
                    mListener.share();
                }
                dismiss();
                break;
            case R.id.menu_rename:
                if(mListener!=null){
                    mListener.rename();
                }
                dismiss();
                break;
            case R.id.menu_delete:
                if(mListener!=null){
                    mListener.delete();
                }
                dismiss();
                break;
            case R.id.id_pop_layout:
                dismiss();
                break;
            default:
                break;
        }
    }

}
