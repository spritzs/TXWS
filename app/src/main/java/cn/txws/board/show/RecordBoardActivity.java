package cn.txws.board.show;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.robotpen.model.entity.SettingEntity;
import cn.robotpen.model.entity.note.NoteEntity;
import cn.robotpen.model.entity.note.TrailsEntity;
import cn.robotpen.model.symbol.DeviceType;
import cn.robotpen.model.symbol.RecordState;
import cn.robotpen.pen.callback.RobotPenActivity;
import cn.robotpen.pen.model.RemoteState;
import cn.robotpen.pen.model.RobotDevice;
import cn.robotpen.record.widget.RecordBoardView;
import cn.robotpen.utils.FileUtils;
import cn.robotpen.utils.log.CLog;
import cn.robotpen.views.module.NoteManageModule;
import cn.robotpen.views.module.TrailsManageModule;
import cn.robotpen.views.widget.WhiteBoardView;
import cn.txws.board.MainActivity;
import cn.txws.board.MyApplication;
import cn.txws.board.R;
import cn.txws.board.adapter.ColorGridViewAdapter;
import cn.txws.board.common.ResUtils;
import cn.txws.board.database.action.InsertNewOrUpdateBlockAction;
import cn.txws.board.util.AppUtil;

public class RecordBoardActivity extends RobotPenActivity
        implements WhiteBoardView.WhiteBoardInterface,
        RecordBoardView.RecordBoardInterface,View.OnClickListener{

    DeviceType mDeDeviceType = DeviceType.P1;//默认连接设备为P1 当与连接设备有冲突时则需要进行切换
    float isRubber = 0;//是否是橡皮
    ProgressDialog mProgressDialog;
    SettingEntity mSettingEntity;
    Handler mHandler;
    float mPenWeight = 2;//默认笔宽度是2像素
    int mPenColor = Color.BLACK;//默认为黑色
    String mNoteKey = NoteEntity.KEY_NOTEKEY_TMP;//默认为临时笔记
    static final int SELECT_PICTURE = 1001;
    static final int SELECT_BG = 1002;
    Uri mInsertPhotoUri = null;
    Uri mBgUri = null;
    int butFlag = 0;
    List<TrailsEntity> mTrailsEntitys;
    TrailsManageModule mTrailsManageModule;

    @BindView(R.id.recordBoardView)
    RecordBoardView recordBoardView;
    @BindView(R.id.viewWindow)
    RelativeLayout viewWindow;
    @BindView(R.id.more_tool_parentlayout)
    View mToolParentLayout;
    @BindView(R.id.more_tool_layout)
    View mToolLayout;
    @BindView(R.id.tool_recorder_record)
    ImageView mToolRecordPlay;
    @BindView(R.id.tool_comfirm)
    ImageView mToolFirm;
    @BindView(R.id.tool_cancel)
    ImageView mToolCancel;
    @BindView(R.id.tool_bg)
    ImageView mToolBackground;
    @BindView(R.id.tool_handorpen)
    ImageView mToolHandOrPen;

    @BindView(R.id.toolbar_color_px)
    ImageView mToolColorPicker;
    @BindView(R.id.toolbar_pen)
    ImageView mPen;

    ImageView mToolInsertImage;

    NoteManageModule mNoteManageModule;
    String mCurrentID;
    boolean isNew=false,isEdit=false,hasBg=false,isTrailEdit=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_board);
        ButterKnife.bind(this);
        mHandler = new Handler();
        recordBoardView.setIsTouchWrite(true);
        recordBoardView.setDaoSession(MyApplication.getInstance().getDaoSession());
        mNoteManageModule = new NoteManageModule(this, MyApplication.getInstance().getDaoSession());
        mTrailsManageModule = new TrailsManageModule(this, MyApplication.getInstance().getDaoSession());
        mTrailsManageModule.setTitle(getNewNoteName()).setIsHorizontal(getIsHorizontal()).setDeviceType(getDeviceType()).setUserId(getCurrUserId()).setup(getNoteKey()).initBlock(null).asyncSave(true);


        recordBoardView.setLoadIgnorePhoto(false);
        recordBoardView.setDataSaveDir(ResUtils.getSavePath(ResUtils.DIR_NAME_DATA));
        recordBoardView.setIsTouchSmooth(true);
        isNew=getIntent().getBooleanExtra(MainActivity.EXTRA_ISNEW,false);
        mCurrentID=getIntent().getStringExtra(MainActivity.EXTRA_BLOCKID);
        Log.e("===master======",""+mCurrentID);

        init();
    }

    public void init(){
        mToolFirm.setClickable(false);
        mToolFirm.setEnabled(false);
        mToolCancel.setClickable(false);
        mToolCancel.setEnabled(false);
        initActionBar();
    }
    public void initActionBar(){
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        View customBar= LayoutInflater.from(this).inflate(R.layout.toolbar_edit_layout,null);
        customBar.findViewById(R.id.toolbar_back).setOnClickListener(this);
        customBar.findViewById(R.id.toolbar_share).setOnClickListener(this);
        customBar.findViewById(R.id.tool_clean).setOnClickListener(this);
        customBar.findViewById(R.id.toolbar_clean).setOnClickListener(this);
        customBar.findViewById(R.id.toolbar_last).setOnClickListener(this);
        customBar.findViewById(R.id.toolbar_next).setOnClickListener(this);
        customBar.findViewById(R.id.toolbar_more).setOnClickListener(this);
        mToolInsertImage= (ImageView) customBar.findViewById(R.id.tool_insert);
        mToolInsertImage.setOnClickListener(this);
        getSupportActionBar().setCustomView(customBar);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    protected void onResume() {
        super.onResume();
        recordBoardView.initDrawArea();
        checkIntentInsertPhoto();
        if(Build.VERSION.SDK_INT >= 23 &&  ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != 0) {
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)this, "android.permission.RECORD_AUDIO")) {
                Toast.makeText(this, cn.robotpen.record.R.string.robotpen_permission_request, Toast.LENGTH_SHORT).show();
            }

            ActivityCompat.requestPermissions((Activity)this, new String[]{"android.permission.RECORD_AUDIO"}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doInDatabase();
        if (recordBoardView != null) {
            recordBoardView.dispose();
            recordBoardView = null;
        }
        if (mTrailsManageModule != null) {
            mTrailsManageModule.dispose();
            mTrailsManageModule = null;
        }
    }


    public void doInDatabase(){
        Log.e("=====doInDatabase====","doInDatabase");
        if(isNew){
            if(!isEdit){
                Log.e("=====doInDatabase====","del new");
                Intent intent=new Intent(MainActivity.ACTION_DELBOARD);
                intent.putExtra(MainActivity.EXTRA_BLOCKID,mCurrentID);
                sendBroadcast(intent);
                return;
            }
        }
        if(isEdit){
            String savePath=null;
            if(ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)this, "android.permission.WRITE_EXTERNAL_STORAGE")) {
                    Toast.makeText(this, cn.robotpen.record.R.string.robotpen_permission_request, 0).show();
                }

                ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 16);
            } else {
                savePath = AppUtil.SAVEDIR + mCurrentID + ".jpg";
                if (FileUtils.saveBitmap(loadBitmapFromView(recordBoardView), savePath)) {
                    Log.e("======saveBitmap============","保存成功");
                }
            }
            InsertNewOrUpdateBlockAction.InsertNewOrUpdateBlock("随笔"+String.valueOf(mTrailsManageModule.getBlockCount()-1),mCurrentID,savePath,System.currentTimeMillis());

//            Log.e("=====doInDatabase====","isEdit"+mCurrentID+","+savePath);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mInsertPhotoUri = null;
            mBgUri = null;
            if (requestCode == SELECT_PICTURE && data != null){
                mInsertPhotoUri = data.getData();
            }
            if (requestCode == SELECT_BG && data != null) {
                mBgUri = data.getData();
            }
        }
    }

    /**
     * 当服务服务连接成功后进行
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
            try {
                RobotDevice device = getPenServiceBinder().getConnectedDevice();
                if (device != null) {
                    recordBoardView.setIsTouchWrite(false);
                    DeviceType type = DeviceType.toDeviceType(device.getDeviceVersion());
                    //判断当前设备与笔记设备是否一致
                    if (recordBoardView.getFrameSizeObject().getDeviceType() != type) {
                        mDeDeviceType = type;
                        mNoteKey = NoteEntity.KEY_NOTEKEY_TMP ;
                    }
                }else {
                    recordBoardView.setIsTouchWrite(true);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }else {
            recordBoardView.setIsTouchWrite(true);
        }
        //都需要刷新白板
        recordBoardView.initDrawArea();

    }



    /**
     * 检查是否有Intent传入需要插入的图片
     */
    public void checkIntentInsertPhoto() {
        //检查是否有需要插入的图片uri
        mToolBackground.setImageResource(mBgUri!=null?R.drawable.tool_delete_bg_layer:R.drawable.tool_change_bg_layer);
        mToolInsertImage.setImageResource(mInsertPhotoUri!=null?R.drawable.tool_comfirm_layer:R.drawable.tool_insert_image_layer);
        if (null != mInsertPhotoUri) {
            recordBoardView.insertPhoto(getRealFilePath(RecordBoardActivity.this,mInsertPhotoUri));
            recordBoardView.startPhotoEdit(true); //插入图片后，设置图片可以编辑状态
            mInsertPhotoUri = null;
        }
        if (null != mBgUri) {
            recordBoardView.setBgPhoto(mBgUri);
            hasBg=true;
            mBgUri = null;
        }else{
            recordBoardView.setBackgroundResource(R.drawable.note_bg_noshade);
        }
        mToolHandOrPen.setImageResource(recordBoardView.isTouchWrite()?R.drawable.tool_usehand_layer:R.drawable.tool_usepen_layer);
    }

    @OnClick({R.id.toolbar_pen,R.id.toolbar_color_px,R.id.tool_bg,R.id.tool_handorpen,R.id.tool_recorder_record,R.id.tool_comfirm,R.id.tool_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_back:
                finish();
                break;
            case R.id.toolbar_share:
                String savePath = AppUtil.SAVEDIR + mCurrentID + ".jpg";
                if (FileUtils.saveBitmap(loadBitmapFromView(recordBoardView), savePath)) {
                    AppUtil.sharePictrue(this,savePath);
                }
                break;
            case R.id.toolbar_pen:
                List<TrailsEntity> list=mTrailsManageModule.getTrails(mCurrentID);
                recordBoardView.loadTrails(list,true);
                break;
            case R.id.toolbar_color_px:
                showColorPixelDialog();
                isRubber=0;
                break;
            case R.id.toolbar_clean:
                showCleanDialog();
                break;
            case R.id.toolbar_last:
                isEdit=true;
                if(isTrailEdit){
                    mTrailsEntitys=mTrailsManageModule.getTrails(mCurrentID);
                    isTrailEdit=false;
                }
                recordBoardView.backTrail();
                break;
            case R.id.toolbar_next:
                isEdit=true;
                int num=mTrailsManageModule.getTrails(mCurrentID).size();
                if(mTrailsEntitys!=null&&num<mTrailsEntitys.size()){
                    recordBoardView.saveTrailsEntity(mTrailsEntitys.get(num));
                    recordBoardView.loadTrails();
                }
                break;
            case R.id.toolbar_more:
                animToolLayout(!toolOpening);
                break;

            case R.id.tool_clean:
                recordBoardView.cleanScreen();
                recordBoardView.startPhotoEdit(false);// 退出图片编辑模式，否则此时点击图平铺会崩溃
                break;
            case R.id.tool_insert:
                if(recordBoardView.getIsPhotoEdit()){
                    recordBoardView.startPhotoEdit(false);
                    mToolInsertImage.setImageResource(R.drawable.tool_insert_image_layer);
                    isEdit=true;
                }else{
                    Intent intent3 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent3, "选择图片"), SELECT_PICTURE);
                }

                break;
            case R.id.tool_bg:
                if(hasBg){
                    mBgUri = null;
                    hasBg=false;
                    mToolBackground.setImageResource(R.drawable.tool_change_bg_layer);
                    recordBoardView.setBgPhoto(null);
                }else{
                    Intent intent4 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent4, "选择背景"), SELECT_BG);
                    isEdit=true;
                }
                break;
            case R.id.tool_handorpen:
                recordBoardView.setIsTouchWrite(!recordBoardView.isTouchWrite());
                mToolHandOrPen.setImageResource(recordBoardView.isTouchWrite()?R.drawable.tool_usehand_layer:R.drawable.tool_usepen_layer);
                break;
            case R.id.tool_recorder_record:
                recordBoardView.setSaveVideoDir(ResUtils.getSavePath(ResUtils.DIR_NAME_VIDEO));//设置存储路径
                if (butFlag == 0) { // 点击开始录制按钮
                    butFlag = 1;// 可以暂停
                    mToolRecordPlay.setImageResource(R.drawable.tool_pause_layer);
                    mToolFirm.setClickable(true);
                    mToolFirm.setEnabled(true);
                    mToolCancel.setClickable(true);
                    mToolCancel.setEnabled(true);
                    recordBoardView.startRecord();
                } else if (butFlag == 1) {// 点击暂停按钮
                    butFlag = 2;// 可以继续
                    mToolRecordPlay.setImageResource(R.drawable.tool_play_layer);
                    recordBoardView.setIsPause(true);
                } else if (butFlag == 2) {// 点击继续按钮
                    butFlag = 1;// 可以暂停
                    mToolRecordPlay.setImageResource(R.drawable.tool_pause_layer);
                    recordBoardView.setIsPause(false);
                }
                break;
            case R.id.tool_comfirm:
                if (butFlag == 1 || butFlag == 2) {// 防止直接点击崩溃
                    butFlag = 0;// 可以暂停
                    mToolRecordPlay.setImageResource(R.drawable.tool_record_layer);
                    mToolFirm.setClickable(false);
                    mToolFirm.setEnabled(false);
                    mToolCancel.setClickable(false);
                    mToolCancel.setEnabled(false);
                    recordBoardView.endRecord();
                }

                break;
            case R.id.tool_cancel:
                if (butFlag == 1 || butFlag == 2) {// 防止直接点击崩溃
                    butFlag = 0;// 可以暂停
                    mToolRecordPlay.setImageResource(R.drawable.tool_record_layer);
                    mToolFirm.setClickable(false);
                    mToolFirm.setEnabled(false);
                    mToolCancel.setClickable(false);
                    mToolCancel.setEnabled(false);
                }
                break;


        }
    }

    Integer[] colorSum=new Integer[]{R.drawable.tool_picker_black_selector,R.drawable.tool_picker_blue_selector,R.drawable.tool_picker_brown_selector,R.drawable.tool_picker_green_selector,R.drawable.tool_picker_green2_selector,R.drawable.tool_picker_yellow1_selector,R.drawable.tool_picker_violet_selector,R.drawable.tool_picker_red_selector,R.drawable.tool_picker_red1_selector,R.drawable.tool_picker_yellow2_selector,R.drawable.tool_picker_yellow_selector,R.drawable.tool_picker_white_selector};
    Integer[] colorToolBar=new Integer[]{R.drawable.tool_t_black_layer,R.drawable.tool_t_blue_layer,R.drawable.tool_t_brown_layer,R.drawable.tool_t_green_layer,R.drawable.tool_t_green2_layer,R.drawable.tool_t_yellow1_layer,R.drawable.tool_t_violet_layer,R.drawable.tool_t_red_layer,R.drawable.tool_t_red1_layer,R.drawable.tool_t_yellow2_layer,R.drawable.tool_t_yellow_layer,R.drawable.tool_t_white_layer};
    ColorGridViewAdapter mColorGridAapter;
    AlertDialog mDialog;

    public void showColorPixelDialog(){
        View view=LayoutInflater.from(this).inflate(R.layout.colorpixel_dialog,null);
        GridView gird= (GridView) view.findViewById(R.id.grid);
        SeekBar seekBar= (SeekBar) view.findViewById(R.id.seek);
        final TextView textView= (TextView) view.findViewById(R.id.text);
        seekBar.setMax(20);
        seekBar.setProgress((int)mPenWeight);
        textView.setText(getString(R.string.tool_pen_pixels)+((int)mPenWeight)+"px");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPenWeight=Math.max(2,progress);
                textView.setText(getString(R.string.tool_pen_pixels)+((int)mPenWeight)+"px");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mColorGridAapter=new ColorGridViewAdapter(this,colorSum);

        mColorGridAapter.setSelecterColor(mPenColor);

        mToolColorPicker.setImageResource(colorToolBar[mColorGridAapter.getSelecterItem()]);
        gird.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mColorGridAapter.setSelecterItem(position);
                mPenColor=mColorGridAapter.getSelecterColor();
            }
        });
        gird.setAdapter(mColorGridAapter);
        mDialog=new AlertDialog.Builder(RecordBoardActivity.this).setView(view).setCancelable(true).setTitle(R.string.colorpixel_dialog_title).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialog.dismiss();
            }
        }).show();
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mToolColorPicker.setImageResource(colorToolBar[mColorGridAapter.getSelecterItem()]);
            }
        });
    }

    int saveRubber=50;
    public void showCleanDialog(){
        View view=LayoutInflater.from(this).inflate(R.layout.clean_dialog,null);
        SeekBar seekBar= (SeekBar) view.findViewById(R.id.seek);
        final TextView textView= (TextView) view.findViewById(R.id.text);
        isRubber=saveRubber;
        seekBar.setMax(10);
        seekBar.setProgress(saveRubber/50);
        textView.setText(getString(R.string.tool_earser_pixels)+(saveRubber)+"px");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                saveRubber=progress*50;
                isRubber=saveRubber;
                textView.setText(getString(R.string.tool_pen_pixels)+(saveRubber)+"px");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mDialog=new AlertDialog.Builder(RecordBoardActivity.this).setView(view).setCancelable(true).setTitle(R.string.earser_dialog_title).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDialog.dismiss();
            }
        }).show();

    }



    boolean toolOpening=true;
    public void animToolLayout(boolean open){
        mToolParentLayout.setVisibility(View.VISIBLE);
        TranslateAnimation trans;
        if(open){
            trans=new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f  );
        }else{
            trans=new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,0.0f,
                    Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT,  -1.0f  );
        }
        trans.setDuration(400);
        trans.setFillAfter(true);
        trans.setInterpolator(new DecelerateInterpolator());
        toolOpening=open;
        trans.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!toolOpening){
                    mToolParentLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mToolLayout.startAnimation(trans);
    }


    public boolean isScreenLanscape() {

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation ; //获取屏幕方向

        if(ori == mConfiguration.ORIENTATION_LANDSCAPE){
            return true;//横屏
        }else if(ori == mConfiguration.ORIENTATION_PORTRAIT){
            return false;//竖屏
        }
        return false;
    }

    /**
     * Try to return the absolute file path from the given Uri
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

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
    } //非0时即为橡皮擦 具体数字代表橡皮擦宽度

    @Override
    public boolean getIsPressure() {
        return false;
    }

    @Override
    public boolean getIsHorizontal() {
        return isScreenLanscape();
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
    public String getNewNoteName() {
        return null;
    }

    @Override
    public boolean onEvent(WhiteBoardView.BoardEvent boardEvent, Object o) {
        Log.e("===choose======", "boardEvent"+boardEvent);
        switch (boardEvent) {
            case TRAILS_COMPLETE:
//                try {
//                    getPenServiceBinder().setPageInfo(recordBoardView.getBlockIndex() + 1, recordBoardView.getBlockCount());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;

            case TRAILS_LOADING:
                recordBoardView.toBlock(mCurrentID);

                break;
            case BOARD_AREA_COMPLETE: //白板区域加载完成
                recordBoardView.beginBlock();

                break;
            case ERROR_DEVICE_TYPE: //检测到连接设备更换

                break;
            case ERROR_SCENE_TYPE: //横竖屏更换
                break;
            case ON_TRAILS:
                CLog.i(""+((TrailsEntity)o).getTrails());
                isEdit=true;
                isTrailEdit=true;
                break;
        }
        return true;
    }

    @Override
    public boolean onMessage(String s, Object o) {
        return false;
    }

    @Override
    public void onPageInfoUpdated(String s) {

    }

    /*
    *录制时必须实现的方法
     */
    @Override
    public int getRecordLevel() {
        mSettingEntity = new SettingEntity(this);
        return mSettingEntity.getVideoQualityValue(); //录制质量
    }

    @Override
    public void onRecordButClick(int i) {
        switch (i) {
            case RecordBoardView.EVENT_CONFIRM_EXT_CLICK:
                break;
        }

    }

    @Override
    public void onRecordError(int i) {

    }

    /**
    *接收录制中的各种状态进行处理
     */
    @Override
    public boolean onRecordState(RecordState recordState, String s) {
        switch (recordState) {
            case START:
                break;
//            case CANCEL:
            case END:
                break;
            case PAUSE:
                break;
            case CONTINUE:
                break;
            case SAVING:
                break;
            case CODING:
                break;
            case COMPLETE:
                break;
            case ERROR:
                break;
            case RESISTANCE:
                recordBoardView.startRecord();
                break;
        }
        return true;
    }

    @Override
    public boolean onRecordTimeChange(Date date) {
    // 显示时间
        return true;
    }

    @Override
    public void getRecordVideoName(String s) {
        Log.e("test","getRecordVideoName :"+s);
    }


    @Override
    public void onStateChanged(int i, String s) {
        switch (i) {
            case RemoteState.STATE_CONNECTED:
                break;
            case RemoteState.STATE_DEVICE_INFO: //当出现设备切换时获取到新设备信息后执行的
                recordBoardView.setIsTouchWrite(false);
//                checkDeviceConn();
                break;
            case RemoteState.STATE_DISCONNECTED://设备断开
                recordBoardView.setIsTouchWrite(true);
                break;
        }
    }

    @Override
    public void onPenServiceError(String s) {

    }

    @Override
    public void onPenPositionChanged(int deviceType, int x, int y, int presure, byte state) {
        super.onPenPositionChanged(deviceType, x, y, presure, state);
        Log.e("=======onPenPositionChanged=======","x:"+x);
        if(isRubber==0) {// isRubber==0  现在没用橡皮察，止选择橡皮擦的时候，不小心触碰笔，绘制笔迹。
//            DevicePoint p = DevicePoint.obtain(deviceType, x, y, presure, state);
//            recordBoardView.drawLine(p);
            DeviceType type = DeviceType.toDeviceType(deviceType);
            recordBoardView.drawDevicePoint(type,x,y,presure,state);
        }
    }

    private int currentPage = 0;
    @Override
    public void onPageInfo(int currentPage, int totalPage) {
        this.currentPage=currentPage;
    }

    @Override
    public void onPageNumberAndCategory(int pageNumber, int category) {
        CLog.d("插入页码："+pageNumber+" 插入的页码类别："+category);
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
    public void onRobotKeyEvent(int e) {
        super.onRobotKeyEvent(e);
        switch (e) {
            case 0x03:
                onEventFrontPage();
                break;
            case 0x04:
                onEventNextPage();
                break;
            case 0x05:
                recordBoardView .insertBlock(currentPage);
                break;
        }

    }


    /**
     * 用于响应设备按钮事件的翻页
     */
    private void onEventFrontPage() {
            if (recordBoardView.isFirstBlock()) {
                recordBoardView.lastBlock();
            } else {
                recordBoardView.frontBlock();
            }
    }

    /**
     * 用于响应设备按钮事件的翻页
     */
    private void onEventNextPage() {
            if (recordBoardView.isLastBlock()){
                recordBoardView.firstBlock();
            } else {
                recordBoardView.nextBlock();
            }
    }

    private Bitmap loadBitmapFromView(View v) {
        v.setDrawingCacheEnabled(true);
        v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        v.setDrawingCacheBackgroundColor(Color.WHITE);
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

//        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);
        v.destroyDrawingCache();
        return bmp;
    }
}
