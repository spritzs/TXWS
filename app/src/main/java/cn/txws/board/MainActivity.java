package cn.txws.board;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.robotpen.model.DevicePoint;
import cn.robotpen.model.entity.note.NoteEntity;
import cn.robotpen.model.symbol.DeviceType;
import cn.robotpen.pen.callback.RobotPenActivity;
import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.model.RobotDevice;
import cn.robotpen.utils.log.CLog;
import cn.robotpen.views.module.TrailsManageModule;
import cn.robotpen.views.widget.WhiteBoardView;
import cn.robotpen.views.widget.WhiteBoardView.WhiteBoardInterface;
import cn.txws.board.adapter.GridViewAdapter;
import cn.txws.board.common.ResUtils;
import cn.txws.board.connect.BleConnectActivity;
import cn.txws.board.database.action.DeleteBlocksAction;
import cn.txws.board.database.action.UpdateBlockAction;
import cn.txws.board.database.binding.Binding;
import cn.txws.board.database.binding.BindingBase;
import cn.txws.board.database.data.BlockLoaderData;
import cn.txws.board.show.BoardPopupMenu;
import cn.txws.board.show.NewRecordBoardActivity;
import cn.txws.board.show.RecordBoardActivity;
import cn.txws.board.util.AppUtil;
import cn.txws.board.util.ThreadUtil;

public class MainActivity extends RobotPenActivity implements BlockLoaderData.BlockLoaderDataListener, WhiteBoardInterface {

    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.grid_recycler)
    RecyclerView mGridView;
    @BindView(R.id.text)
    TextView nullText;
    TextView allText;
    ImageView backSettingImg,bluetoothImg,syncImg;
    View bottomLayout;
    FloatingActionButton mFab;
    @BindView(R.id.bottom_delete)
    TextView bottomDelete;
    @BindView(R.id.bottom_share)
    TextView bottomShare;
    @BindView(R.id.bottom_merge)
    TextView bottomMerge;
    RobotDevice mRobotDevice;//连接上的设备

    public final static String EXTRA_BLOCKID = "EXTRA_BLOCKID";
    public final static String ACTION_DELBOARD = "action_delboard";
    public static String EXTRA_ISNEW="extra_isnew";
    DelBroadcastReceiver mDelBroadcastReceiver;
    IntentFilter mIntentFilter;
    TrailsManageModule mTrailsManageModule;


    final Binding<BlockLoaderData> mListBinding = BindingBase
            .createBinding(this);

    GridViewAdapter mGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建文件夹
        ResUtils.isDirectory(ResUtils.DIR_NAME_BUFFER);
        ResUtils.isDirectory(ResUtils.DIR_NAME_DATA);
        ResUtils.isDirectory(ResUtils.DIR_NAME_PHOTO);
        ResUtils.isDirectory(ResUtils.DIR_NAME_VIDEO);
        ButterKnife.bind(this);
        mTrailsManageModule=new TrailsManageModule(this,MyApplication.getInstance().getDaoSession());
        this.mTrailsManageModule.setTitle(getNewNoteName()).setIsHorizontal(getIsHorizontal()).setDeviceType(getDeviceType()).setUserId(getCurrUserId()).setup(getNoteKey()).initBlock(null).asyncSave(true);
        init();
    }

    private void init() {
        GridLayoutManager mgr = new GridLayoutManager(this, 2);
        mGridView.setLayoutManager(mgr);
        mGridView.setItemAnimator(new DefaultItemAnimator());

        mListBinding.bind(new BlockLoaderData(this, this));
        mListBinding.getData().init(getLoaderManager(), mListBinding);
        mGridAdapter = new GridViewAdapter(this, null);
        mGridView.setAdapter(mGridAdapter);
        mGridAdapter.setOnMoreClickListener(new GridViewAdapter.OnMoreClickListener() {
            @Override
            public void onClick(View v, int position, String blockid) {
                showMoreDialog(v, position, blockid);
            }

        });
        mGridAdapter.setOnItemClickListner(itemClickListener);

        mGridAdapter.setOnLongClickListener(new GridViewAdapter.OnLongClickListener() {
            @Override
            public void onLongClick() {
                if(mGridAdapter!=null&&!mGridAdapter.getIsSelectorMode()) {
                    setSelectorMode(true);
                }
            }
        });

        showEmpty(true);


        getSupportActionBar().setDisplayShowCustomEnabled(true); //Enable自定义的View
        View customBar = LayoutInflater.from(this).inflate(R.layout.toolbar_layout, null);
        backSettingImg= (ImageView) customBar.findViewById(R.id.toolbar_settings);
        backSettingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mGridAdapter!=null&&mGridAdapter.getIsSelectorMode()){
                    setSelectorMode(false);
                }else{
                    gotoSettingsActivity();
                }
            }
        });
        bluetoothImg= (ImageView) customBar.findViewById(R.id.toolbar_bluetooth);
        bluetoothImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectBlueTooth();
            }
        });
        syncImg= (ImageView) customBar.findViewById(R.id.toolbar_sync);
        syncImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncOffLine();
            }
        });
        allText= (TextView) customBar.findViewById(R.id.toolbar_allpick);
        allText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGridAdapter.allPickOrUnPick(allText);
                refreshBottomLayout();
            }
        });
        getSupportActionBar().setCustomView(customBar);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNewRecordBoardActivity();
            }
        });
        bottomLayout=findViewById(R.id.bottom_layout);

        mDelBroadcastReceiver = new DelBroadcastReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_DELBOARD);
        setupActionBar();
    }

    public void setSelectorMode(boolean selectMode){
        mGridAdapter.setSelectorMode(selectMode);
        setupActionBar();
    }


    public void setupActionBar(){
        if(mGridAdapter!=null&&mGridAdapter.getIsSelectorMode()){
            backSettingImg.setImageResource(R.drawable.toolbar_back_black);
            allText.setVisibility(View.VISIBLE);
            bluetoothImg.setVisibility(View.INVISIBLE);
            syncImg.setVisibility(View.INVISIBLE);
            mFab.setVisibility(View.INVISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
        }else{
            backSettingImg.setImageResource(R.drawable.ic_toolbar_quicksettings);
            allText.setVisibility(View.INVISIBLE);
            bluetoothImg.setVisibility(View.VISIBLE);
            mFab.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.GONE);
            syncImg.setVisibility(View.VISIBLE);
        }
    }


    private void refreshBottomLayout() {
        List<String> mList=mGridAdapter.getSelectorList();
        if(mList.size()==0){
            bottomDelete.setClickable(false);
            bottomDelete.setEnabled(false);
            bottomShare.setClickable(false);
            bottomShare.setEnabled(false);
            bottomMerge.setClickable(false);
            bottomMerge.setEnabled(false);
        }else{
            bottomDelete.setClickable(true);
            bottomDelete.setEnabled(true);
            bottomShare.setClickable(true);
            bottomShare.setEnabled(true);
            bottomMerge.setClickable(true);
            bottomMerge.setEnabled(true);
        }
        if(mList.size()==mGridAdapter.getItemCount()){
            allText.setText(R.string.unallpick);
        }else{
            allText.setText(R.string.allpick);
        }
    }

    @OnClick({R.id.bottom_delete,R.id.bottom_share,R.id.bottom_merge})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.bottom_delete:
                showDeleteDialog();
                break;
            case R.id.bottom_share:
                AppUtil.sharePictruesString(this,mGridAdapter.getSelectorPictureList());
                setSelectorMode(false);
                break;
            case R.id.bottom_merge:
                setSelectorMode(false);
                break;
        }
    }

    public void showMoreDialog(View moreView, final int position, final String blockid) {
        BoardPopupMenu popupMenu = new BoardPopupMenu(this);
        popupMenu.setOnItemClickListener(new BoardPopupMenu.OnItemClickListener() {
            @Override
            public void share() {
                String savePath = AppUtil.SAVEDIR + blockid + ".jpg";
                AppUtil.sharePictrue(MainActivity.this, savePath);
            }

            @Override
            public void rename() {
                showRenameDialog(blockid);
            }

            @Override
            public void delete() {
                deleteBlock(blockid);
            }
        });
        popupMenu.showPopupWindow(moreView);
    }


    public void showDeleteDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.delete_title);    //设置对话框标题
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] blocks=null;
                mGridAdapter.getSelectorList().toArray(blocks);
                deleteBlocks(blocks);
                setSelectorMode(false);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create();  //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }

    public void showRenameDialog(final String blockid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.rename);    //设置对话框标题
        final EditText edit = new EditText(MainActivity.this);
        builder.setView(edit);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edit.getText().toString() != null) {
                    UpdateBlockAction.updateBlock(edit.getText().toString(),blockid,null,System.currentTimeMillis());
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(true);    //设置按钮是否可以按返回键取消,false则不可以取消
        AlertDialog dialog = builder.create();  //创建对话框
        dialog.setCanceledOnTouchOutside(true); //设置弹出框失去焦点是否隐藏,即点击屏蔽其它地方是否隐藏
        dialog.show();
    }


    public void showEmpty(boolean show) {
        nullText.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        mGridView.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
    }


    @Override
    public void onBlockUpdated(BlockLoaderData data, Cursor cursor) {
        mListBinding.ensureBound(data);
        if (cursor != null) {
            mGridAdapter.swapCursor(cursor);
            showEmpty(cursor.getCount() == 0);
        } else {
            showEmpty(true);
        }

    }

    protected class DelBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            deleteBlock(intent.getStringExtra(MainActivity.EXTRA_BLOCKID));
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mDelBroadcastReceiver, mIntentFilter);
        checkDeviceConn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mDelBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        try {
            super.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mListBinding.unbind();
//        if (mWhiteBoardView != null) {
//            mWhiteBoardView.dispose();
//            mWhiteBoardView = null;
//        }
        if(mTrailsManageModule!=null){
            mTrailsManageModule.dispose();
            mTrailsManageModule=null;
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
//        checkSDPermission();
    }


    public void connectBlueTooth() {
        Intent intent = new Intent(MainActivity.this, BleConnectActivity.class);
        startActivity(intent);
    }

    public void gotoSettingsActivity() {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void syncOffLine() {
        if(mRobotDevice!=null) {
            checkStorageNoteNum(mRobotDevice);//同步笔记
        }
    }

    @Override
    public void onSyncProgress(String key, int total, int progress) {
        super.onSyncProgress(key, total, progress);
    }


    @Override
    public void onOffLineNoteSyncFinished(String json, byte[] data) {
        super.onOffLineNoteSyncFinished(json, data);

        if (mRobotDevice!=null&&data != null && data.length >= 5) {
            int num = 0, step = 1;
            List<DevicePoint> points = new ArrayList<>();
            DeviceType type = DeviceType.toDeviceType(mRobotDevice.getDeviceVersion());
            DevicePoint point;
            for (int i = 0; i <= data.length - 5; i += step) {
                try {
                    point = new DevicePoint(type, data, i);
                    step = 5;//5字节为一个点数据
                } catch (Exception e) {
                    step = 1;//查找下一个有效字节
                    e.printStackTrace();
                    continue;
                }
                if (point.isLeave()) {
                    //结束点
                    num++;
                    Log.v("Sync", String.format("第%d笔,共%d个点", num, points.size()));
                    AppUtil.putDevicePoints(points);
                    points.clear();
                } else {
                    points.add(point);
                }
            }


            ThreadUtil.getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    syncImg.clearAnimation();
                    Toast.makeText(MainActivity.this, "共计同步了 " + AppUtil.getDevicePoints().size() + " 笔数据", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 检查存储笔记数
     */
    private void checkStorageNoteNum(RobotDevice device) {
        int num = device.getOfflineNoteNum();
        if (num > 0) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("提示");
            alert.setMessage("共有" + num + "条数据可以同步！");
            alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        syncImg.clearAnimation();
                        Animation animation= AnimationUtils.loadAnimation(MainActivity.this,R.anim.rotate_anim);
                        syncImg.startAnimation(animation);
                        getPenServiceBinder().startSyncOffLineNote();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            });
            alert.setNegativeButton("取消", null);
            alert.show();
        }else{
            Toast.makeText(this, "已是最新数据", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        if(mGridAdapter!=null&&mGridAdapter.getIsSelectorMode()){
            setSelectorMode(false);
        }else{
            super.onBackPressed();
        }
    }

    public void deleteBlocks(String[] delblocks) {
        DeleteBlocksAction.deleteBlocks(delblocks);
        for(int i=0;i<delblocks.length;i++) {
            mTrailsManageModule.delBlock(delblocks[i]);
        }
    }

    public void deleteBlock(String delblock) {
        DeleteBlocksAction.deleteBlock(delblock);
        try {
            mTrailsManageModule.delBlock(delblock);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }



    GridViewAdapter.OnItemClickListner itemClickListener = new GridViewAdapter.OnItemClickListner() {
        @Override
        public void onItemClickListner(View v, String blockid) {
            if(mGridAdapter!=null&&mGridAdapter.getIsSelectorMode()){
                refreshBottomLayout();
            }else{
                gotoRecordBoardActivity(blockid);
            }
        }
    };

    public void gotoNewRecordBoardActivity() {
        String blockid =mTrailsManageModule.appendBlock(mTrailsManageModule.getEndBlock(),(long)mTrailsManageModule.getBlockCount());
        if (blockid == null) {
            Log.e("=======gotoNewRecordBoardActivity============", "blockid=null");
            return;
        }
        Intent intent = new Intent(MainActivity.this, RecordBoardActivity.class);
        intent.putExtra(EXTRA_BLOCKID,blockid);
        intent.putExtra(EXTRA_ISNEW,true);
        startActivityForResult(intent, 0);
    }

    public void gotoRecordBoardActivity(String blockid) {
        if (blockid == null) {
            Log.e("=======gotoRecordBoardActivity============", "blockid=null");
            return;
        }
        Intent intent = new Intent(MainActivity.this, RecordBoardActivity.class);
        intent.putExtra(EXTRA_BLOCKID,blockid);
        intent.putExtra(EXTRA_ISNEW,false);
        startActivityForResult(intent,0);
    }


    DeviceType mDeDeviceType = DeviceType.P1;//默认连接设备为P1 当与连接设备有冲突时则需要进行切换
    int isRubber;//是否是橡皮
    float mPenWeight = 2;//笔粗细
    int mPenColor = Color.BLACK;//笔颜色
    String mNoteKey = NoteEntity.KEY_NOTEKEY_TMP;


    @Override
    public DeviceType getDeviceType() {
        return mDeDeviceType;
    }

    @Override
    public float getPenWeight() {
        return mPenWeight;
    }

    @Override
    public int getPenColor() {
        return mPenColor;
    }

    @Override
    public float getIsRubber() {
        return isRubber;
    }

    @Override
    public boolean getIsPressure() {
        return true;
    }

    @Override
    public boolean getIsHorizontal() {
        return isScreenLanscape();
    }

    public boolean isScreenLanscape() {
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            return true;//横屏
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            return false;//竖屏
        }
        return false;
    }

    @Override
    public long getCurrUserId() {
        return 0;
    }

    @Override
    public String getNoteKey() {
        return mNoteKey;
    }

    @Override
    public String getNewNoteName() { //修改右下角页码名称
        return "123";
    }

    @Override
    public boolean onEvent(WhiteBoardView.BoardEvent boardEvent, Object o) {
        return true;
    }

    @Override
    public boolean onMessage(String s, Object o) {
        return false;
    }

    @Override
    public void onPageInfoUpdated(String s) {

    }

    /**
     * 笔服务连接状态回调
     * 成功不成功都调用
     *
     * @param name
     * @param service
     */
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        super.onServiceConnected(name, service);
        checkDeviceConn();
    }

    public void checkDeviceConn() {
        if (getPenServiceBinder() != null) {
            RobotDevice robotDevice = null;
            try {
                robotDevice = getPenServiceBinder().getConnectedDevice();
                if (null != robotDevice) {
                    mRobotDevice = robotDevice;
                    syncImg.setEnabled(true);
                }else{
                    mRobotDevice=null;
                    syncImg.setEnabled(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onPenServiceError(String s) {

    }


    @Override
    public void onPenPositionChanged(int deviceType, int x, int y, int presure, byte state) {
        // state  00 离开 0x10悬空 0x11按下
        super.onPenPositionChanged(deviceType, x, y, presure, state);
    }


    // 上报笔记页码信息： currentPage 当前页码， totalPage 总页码。
    @Override
    public void onPageInfo(int currentPage, int totalPage) {

    }

    // 上报插入页信息： pageNumber 当前页码， category 当前页码所属的笔记。
    @Override
    public void onPageNumberAndCategory(int pageNumber, int category) {
        CLog.d("插入页码：" + pageNumber + " 插入的页码类别：" + category);
    }

    @Override
    public void onSupportPenPressureCheck(boolean flag) {

    }

    @Override
    public void onCheckPressureing() {

    }

    @Override
    public void onCheckPressurePen() {

    }

    @Override
    public void onCheckPressureFinish(int flag) {

    }

    @Override
    public void onCheckModuleUpdate() {

    }

    @Override
    public void onCheckModuleUpdateFinish(byte[] data) {

    }

    @Override
    public void onStateChanged(int i, String s) {
        Log.e("========onStateChanged==========","onStateChanged"+s);
    }

}
