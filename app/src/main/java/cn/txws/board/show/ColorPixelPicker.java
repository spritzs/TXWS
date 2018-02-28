package cn.txws.board.show;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import cn.txws.board.R;

/**
 * Created by Administrator on 2018/2/1 0001.
 */

public class ColorPixelPicker extends AlertDialog {
    protected ColorPixelPicker(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view=LayoutInflater.from(getContext()).inflate(R.layout.colorpixel_dialog,null);
        setView(view);
    }
}
