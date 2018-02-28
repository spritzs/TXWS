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

import cn.txws.board.R;

public class AppUtil {
    public final static String SAVEDIR = Environment.getExternalStorageDirectory()+ "/cn.txws.board/photo/";

    public static void sharePictrue(Context context,String path) {
        Intent intent=new Intent(Intent.ACTION_SEND);
        Uri uri=Uri.fromFile(new File(path));
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.setType("image/*");
        context.startActivity(Intent.createChooser(intent,context.getString(R.string.share_title)));
    }
}
