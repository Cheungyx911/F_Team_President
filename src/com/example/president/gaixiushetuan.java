package com.example.president;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.List;


import com.example.util.F_GetBitmap;
import com.example.util.NetInfoUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class gaixiushetuan extends Activity{
	String id = null;
	String photopath = "tianjia999";
	String path;
	ProgressDialog pd;
	TextView fanhui = null;
	TextView baocun = null;
	EditText name = null;
	EditText kouhao = null;
	EditText detail = null;
	byte[] temp;
	byte[] temp2;
	Bitmap bit;
	Bitmap bit2;
	ImageView touxiang = null;
	ImageView zhupicture=null;
	RelativeLayout changephoto;
	RelativeLayout myselfmain_picture;
	private List<String[]> shetuanmessage = null;
	String message[][] = null;
	String mes="";
	String shetuanname, shetuankouhao, shetuanjieshao,image1,image2;
	static Uri uri;
	Bitmap cameraBitmap;
	Bitmap bm = null;
	byte all_image[]=null;
	String shetuanid="";
	boolean d1=false;
	boolean d2=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.shetuanxiugai);
		pd = new ProgressDialog(this);
		pd.setMax(100);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCancelable(false);
		pd.setMessage("?????????????????????........"); 
		Intent intent = getIntent();
		shetuanid=intent.getStringExtra("id");
		photopath=shetuanid;
		touxiang=(ImageView)findViewById(R.id.set_user_image_2);
		changephoto=(RelativeLayout)findViewById(R.id.myselfmain_2);
		name=(EditText)findViewById(R.id.shetuanname_11);
		kouhao=(EditText)findViewById(R.id.shetuankouhao_3);
		detail=(EditText)findViewById(R.id.shetuanjianjie_3);
		//myselfmain_picture=(RelativeLayout)findViewById(R.id.myselfmain_picture);
		fanhui = (TextView) findViewById(R.id.myselftool_text1_2);
		fanhui.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bit != null && !bit.isRecycled()) {
					bit.recycle();
					bit = null;
				}
				if (cameraBitmap != null && !cameraBitmap.isRecycled()) {
					cameraBitmap.recycle();
					cameraBitmap = null;
				}
				if (bit2 != null && !bit2.isRecycled()) {
					bit2.recycle();
					bit2 = null;
				}
				if (bm != null && !bm.isRecycled()) {
					bm.recycle();
					bm = null;
				}
				System.gc();
				finish();
			}
		});
		baocun = (TextView) findViewById(R.id.myselftool_text3_2);
		baocun.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				thread_set th = new thread_set();
				th.start();
				try {
					th.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Builder b = new AlertDialog.Builder(gaixiushetuan.this);
				b.setTitle("????????????");
				b.setMessage("???????????????");// ????????????
				b.setPositiveButton// ????????????????????????
						("??????", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								String picFilePath = path;
								BitmapFactory.Options options = new BitmapFactory.Options();
								options.inDither = false; /* ??????????????????????????? */
								options.inPreferredConfig = null; /* ??????????????????????????????????????? */
								options.inSampleSize = 1; /* ?????????????????????????????? */
							    bm = BitmapFactory.decodeFile(picFilePath, options);
								finish();
							}
						});
				b.create().show();
			}
		});
		changephoto.setOnClickListener(new View.OnClickListener() {
			@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("image/*");
					startActivityForResult(intent, 0);
				}
			});
		thread_get gg=new thread_get();
		gg.start();
		try{
			gg.join();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	Handler mHandler=new Handler()
	{
		 @Override
		public void handleMessage(Message msg)
		{
			switch(msg.what)
			{
			case 0:pd.dismiss();			
			break;
			case 1:pd.show();
			
			break;
			}
		}
	};
	private void crop(Uri uri) {
		// // ??????????????????
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		//?????????????????????1:1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		//intent.putExtra("outputFormat", "PNG");// ????????????
		intent.putExtra("noFaceDetection", true);// ??????????????????
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 1);
	}
	@SuppressWarnings("deprecation")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) { // ????????? RESULT_OK ?????????????????????????????????
			return;
		}
		@SuppressWarnings("unused")
		// ?????????????????????ContentProvider??????????????? ????????????ContentResolver??????
		ContentResolver resolver = getContentResolver();
		// ??????????????????????????????Activity??????????????????????????? ??????-- 0?????? ??????????????? 1???????????????????????????????????????
		if (requestCode == 0) {
			try {
				Uri originalUri = data.getData();
				//crop(originalUri);// ???????????????uri
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);
				touxiang.setImageBitmap(bm);
				String[] proj = { MediaColumns.DATA };
				// ?????????android????????????????????????????????????????????????Android??????
				Cursor cursor = managedQuery(originalUri, proj, null, null,
						null);
				// ?????????????????? ??????????????????????????????????????????????????
				int column_index = cursor
						.getColumnIndexOrThrow(MediaColumns.DATA);
				// ????????????????????? ???????????????????????????????????????????????????
				cursor.moveToFirst();
				// ???????????????????????????????????????
				path = cursor.getString(column_index);
				File tempFile =new File( path.trim());
				photopath = tempFile.getName();
				//photopath=photopath.substring(0,photopath.length() - 4);
				photopath=shetuanid;
				//photopath=Constant.userName;
                thread_insert hs=new thread_insert();
                hs.start();
                try{
                	hs.join();
                }catch(Exception e){
                	e.printStackTrace();
                }
			} catch (IOException e) {
			}

		}
//		if (requestCode == 1) {
//			cameraBitmap = (Bitmap) data.getExtras().get("data");
//			super.onActivityResult(requestCode, resultCode, data);
//			touxiang.setImageBitmap(cameraBitmap);
//		}
	}
	class thread_insert extends Thread {
		@Override
		public void run() {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] data1 = baos.toByteArray();
			NetInfoUtil.insertpic(data1,photopath+".png"); 
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			all_image = NetInfoUtil.getPicture(photopath+".png");
			F_GetBitmap.setInSDBitmap(all_image, photopath+".png");
		}
	}
	private class thread_get extends Thread{
		@Override
		public void run(){
			shetuanmessage=NetInfoUtil.getshetuanmessagebyid(shetuanid);
			message = new String[shetuanmessage.size()][shetuanmessage.get(0).length];
			for (int i = 0; i < shetuanmessage.size(); i++) {
				for (int j = 0; j < shetuanmessage.get(i).length; j++) {
					message[i][j] = shetuanmessage.get(i)[j];
				}
			}
			name.setText(message[0][0]);
			kouhao.setHint(message[0][2]);
			detail.setHint(message[0][1]);
			image1=message[0][3]+".png";
			image2=message[0][4]+".png";
			for(int i=0;i<1;i++)
			{
			if (F_GetBitmap.isEmpty(image1)) {
				temp = NetInfoUtil.getPicture(image1);
				F_GetBitmap.setInSDBitmap(temp, image1);
				InputStream input = null;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				input = new ByteArrayInputStream(temp);
				@SuppressWarnings({ "unchecked", "rawtypes" })
				SoftReference softRef = new SoftReference(
						BitmapFactory.decodeStream(input, null, options));
				bit = (Bitmap) softRef.get();
			} else {
				bit = F_GetBitmap.getSDBitmap(image1);// ????????????????????BitMap??????????????????????????????
				if (F_GetBitmap.bitmap != null
						&& !F_GetBitmap.bitmap.isRecycled()) {
					F_GetBitmap.bitmap = null;
				}
			 }
			}
			touxiang.setImageBitmap(bit);
		}
	}
	private class thread_set extends Thread{
		@Override
		public void run(){
			shetuanname = name.getText().toString();
			mes = shetuanname;
			mes = mes +"<#>" + shetuanid;
			if (!kouhao.getText().toString().equals("")) {
				shetuankouhao = kouhao.getText().toString();
			} else {
				shetuankouhao = kouhao.getHint().toString();
			}
			mes=mes+"<#>"+shetuankouhao;
			if (!detail.getText().toString().equals("")) {
				shetuanjieshao = detail.getText().toString();
			} else {
				shetuanjieshao = detail.getHint().toString();
			}
			mes=mes+"<#>"+shetuanjieshao;
			NetInfoUtil.insertshetuanmessage(mes);
		}
	}
}
