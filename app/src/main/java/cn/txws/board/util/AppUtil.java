/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.txws.board.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cn.robotpen.model.DevicePoint;
import cn.txws.board.R;

public class AppUtil {
    public final static String SAVEDIR = Environment.getExternalStorageDirectory()+ "/cn.txws.board/photo/";
    private static List<List<DevicePoint>> mDevicePoints = new ArrayList<>();

    public static List<List<DevicePoint>> getDevicePoints(){
        return mDevicePoints;
    }
    public static void putDevicePoints(List<DevicePoint> devicePoints){
//        mDevicePoints.clear();
        List<DevicePoint> array=new ArrayList<>();
        array.addAll(devicePoints);
        mDevicePoints.add(array);
    }

    public static void sharePictrue(Context context,String path) {
        Intent intent=new Intent(Intent.ACTION_SEND);
        Uri uri=Uri.fromFile(new File(path));
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setType("image/*");
        context.startActivity(Intent.createChooser(intent,context.getString(R.string.share_title)));
    }

    public static void sharePictrues(Context context, ArrayList<Uri> paths) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setAction("android.intent.action.SEND_MULTIPLE");
        intent.setType("image/*");
//        intent.putExtra("Kdescription", kdescription);
        intent.putExtra(Intent.EXTRA_STREAM, paths);
        context.startActivity(intent);

    }

    public static void sharePictruesString(Context context,List<String> paths) {
        ArrayList<Uri> uris=new ArrayList<Uri>();
        for(String path:paths){
            Uri uri=Uri.fromFile(new File(path));
            uris.add(uri);
        }
        sharePictrues(context,uris);
    }

}
