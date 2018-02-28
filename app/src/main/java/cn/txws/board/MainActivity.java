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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
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
import cn.txws.board.show.RecordBoardActivity;
import cn.txws.board.show.WhiteBoardActivity;
import cn.txws.board.show.WhiteBoardWithMethodActivity;
import cn.txws.board.util.AppUtil;

public class MainActivity extends RobotPenActivity implements BlockLoaderData.BlockLoaderDataListener, WhiteBoardInterface {

    @BindView(R.id.activity_main)
    RelativeLayout activityMain;
    @BindView(R.id.grid_recycler)
    RecyclerView mGridView;
//    @BindView(R.id.whiteBoardView)
//    WhiteBoardView mWhiteBoardView;
    @BindView(R.id.text)
    TextView nullText;

    public final static String EXTRA_BLOCKID = "EXTRA_BLOCKID";
    public final static String EXTRA_ISNEW = "extra_isnew";
    public final static String ACTION_DELBOARD = "action_delboard";
    DelBroadcastReceiver mDelBroadcastReceiver;
    IntentFilter mIntentFilter;
    TrailsManageModule mTrailsManageModule;


    final Binding<BlockLoaderData> mListBinding = BindingBase
            .createBinding(this);

    GridViewAdapter mGridAdapter;

    boolean isDel = false;

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
//        mWhiteBoardView.setIsTouchWrite(false);//默认用手输入
//        mWhiteBoardView.setDaoSession(MyApplication.getInstance().getDaoSession());
        mTrailsManageModule=new TrailsManageModule(this,MyApplication.getInstance().getDaoSession());

        this.mTrailsManageModule.setTitle(getNewNoteName()).setIsHorizontal(getIsHorizontal()).setDeviceType(getDeviceType()).setUserId(getCurrUserId()).setup(getNoteKey()).initBlock(null).asyncSave(true);
//        mWhiteBoardView.setLoadIgnorePhoto(false);
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
        showEmpty(true);
        init();
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

    public void showRenameDialog(final String blockid) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.rename);    //设置对话框标题
        final EditText edit = new EditText(MainActivity.this);
        builder.setView(edit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (edit.getText().toString() != null) {
                    UpdateBlockAction.updateBlock(edit.getText().toString(),blockid,null,System.currentTimeMillis());
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
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
            Log.e("=======onBlockUpdated======", "cursor:" + cursor.getCount());
        } else {
            Log.e("=======onBlockUpdated======", "cursor=NULL");
            showEmpty(true);
        }

    }

    protected class DelBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            deleteBlock(intent.getStringExtra(MainActivity.EXTRA_BLOCKID));
//            mWhiteBoardView.delCurrBlock();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        mWhiteBoardView.initDrawArea();
        registerReceiver(mDelBroadcastReceiver, mIntentFilter);
        Log.e("=======onResume============", "mTrail" + mTrailsManageModule.getBlockCount());
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

    private void init() {
        getSupportActionBar().setDisplayShowCustomEnabled(true); //Enable自定义的View
        View customBar = LayoutInflater.from(this).inflate(R.layout.toolbar_layout, null);
        customBar.findViewById(R.id.toolbar_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSettingsActivity();
            }
        });
        customBar.findViewById(R.id.toolbar_bluetooth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectBlueTooth();
            }
        });
        customBar.findViewById(R.id.toolbar_sync).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncOffLine();
            }
        });
        getSupportActionBar().setCustomView(customBar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoNewRecordBoardActivity();
            }
        });

        mDelBroadcastReceiver = new DelBroadcastReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ACTION_DELBOARD);

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

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void deleteBlock(String delblock) {
//        mGridAdapter.notifyItemRemoved(position);
        DeleteBlocksAction.deleteBlock(delblock);
        mTrailsManageModule.delBlock(delblock);
//        mGridAdapter.notifyItemChanged(position);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    GridViewAdapter.OnItemClickListner itemClickListener = new GridViewAdapter.OnItemClickListner() {
        @Override
        public void onItemClickListner(View v, String blockid) {
            gotoRecordBoardActivity(blockid);
        }
    };

    public void gotoNewRecordBoardActivity() {
//        String blockid = mWhiteBoardView.insertBlock(mWhiteBoardView.getBlockCount());

        String blockid =mTrailsManageModule.appendBlock(mTrailsManageModule.getEndBlock(),(long)mTrailsManageModule.getBlockCount());
        if (blockid == null) {
            Log.e("=======gotoNewRecordBoardActivity============", "blockid=null");
            return;
        }
        Intent intent = new Intent(MainActivity.this, RecordBoardActivity.class);
        intent.putExtra(EXTRA_BLOCKID, blockid);
        intent.putExtra(EXTRA_ISNEW, true);
        startActivityForResult(intent, 0);
    }

    public void gotoRecordBoardActivity(String blockid) {
        if (blockid == null) {
            Log.e("=======gotoRecordBoardActivity============", "blockid=null");
            return;
        }
        Intent intent = new Intent(MainActivity.this, RecordBoardActivity.class);
        intent.putExtra(EXTRA_BLOCKID, blockid);
        intent.putExtra(EXTRA_ISNEW, false);
        startActivityForResult(intent, 0);
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
//        switch (boardEvent) {
//            case BOARD_AREA_COMPLETE: //白板区域加载完成
//                mWhiteBoardView.beginBlock();
//                break;
//            case ERROR_DEVICE_TYPE: //检测到连接设备更换
//                break;
//            case ERROR_SCENE_TYPE: //横竖屏更换
//                break;
//            case TRAILS_COMPLETE:
//
//                break;
//        }
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
//        checkDeviceConn();
    }

//    public void checkDeviceConn() {
//        if (getPenServiceBinder() != null) {
//            try {
//                RobotDevice device = getPenServiceBinder().getConnectedDevice();
//                if (device != null) {
//                    DeviceType type = DeviceType.toDeviceType(device.getDeviceVersion());
//                    mWhiteBoardView.setIsTouchWrite(false);
//                    //判断当前设备与笔记设备是否一致
//                    if (mWhiteBoardView.getFrameSizeObject().getDeviceType() != type) {
//                        mDeDeviceType = type;
//                        mNoteKey = NoteEntity.KEY_NOTEKEY_TMP + "_" + mDeDeviceType.name();
//                    }
//                }
//                else {
//                    mWhiteBoardView.setIsTouchWrite(false);
//                }
//            } catch (RemoteException e) {
//                e.printStackTrace();
//            }
//        }
//        else {
//            mWhiteBoardView.setIsTouchWrite(false);
//        }
//        //都需要刷新白板
//        mWhiteBoardView.initDrawArea();
//    }


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
//        switch (i) {
//            case RemoteState.STATE_CONNECTED:
//                break;
//            case RemoteState.STATE_DEVICE_INFO: //当出现设备切换时获取到新设备信息后执行的
//                mWhiteBoardView.setIsTouchWrite(false);// 设备连接成功，改为用笔输入
//                checkDeviceConn();
//                break;
//            case RemoteState.STATE_DISCONNECTED://设备断开
//                mWhiteBoardView.setIsTouchWrite(false);// 设备断开，允许用手输入
//                break;
//        }
    }

}
