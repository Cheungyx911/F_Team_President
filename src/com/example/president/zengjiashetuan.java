package com.example.president;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;


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
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class zengjiashetuan extends Activity{
	String id = null;
	String photopath = "tinajia999";
	String path;
	ProgressDialog pd;
	TextView fanhui = null;
	TextView baocun = null;
	EditText shetuaname = null;
	EditText shetuankouhao = null;
	EditText shetuanjieshao = null;
	
	byte[] temp;
	Bitmap bit;
	ImageView touxiang = null;
	private List<String[]> usermessage = null;
	String message[][] = null;
	String mes;
	String name, kouhao, jianjie;
	static Uri uri;
	Bitmap cameraBitmap;
	Bitmap bm = null;
	byte all_image[]=null;
	String maxid="";
	RelativeLayout changephoto;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zengjiashetuan);
		fanhui = (TextView) findViewById(R.id.myselftool_text1);
		fanhui.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bm != null && !bm.isRecycled()) {
					bm.recycle();
					bm = null;
				}
				if (cameraBitmap != null && !cameraBitmap.isRecycled()) {
					cameraBitmap.recycle();
					cameraBitmap = null;
				}
				System.gc();
				Intent it=new Intent(zengjiashetuan.this,shetuanmanger.class);
				finish();
				startActivity(it);
			}
		});
		baocun = (TextView) findViewById(R.id.myselftool_text3);
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
				Builder b = new AlertDialog.Builder(zengjiashetuan.this);
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
								Bitmap bm = BitmapFactory.decodeFile(picFilePath, options);
								
								Intent it=new Intent(zengjiashetuan.this,shetuanmanger.class);
								finish();
								startActivity(it);
							}
						});
				b.create().show();
			}
		});
		Intent intent = getIntent();
		maxid = intent.getStringExtra("maxid");
		shetuaname=(EditText)findViewById(R.id.shetuanname_2);
		shetuankouhao=(EditText)findViewById(R.id.shetuankouhao_2);
		shetuanjieshao=(EditText)findViewById(R.id.shetuanjianjie_2);
		touxiang=(ImageView)findViewById(R.id.set_user_image);
		changephoto=(RelativeLayout)findViewById(R.id.myselfmain_1);
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
	}
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
				int maxshetuan=Integer.parseInt(maxid)+1;
				photopath=(maxshetuan+"");
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
		if (requestCode == 1) {
			cameraBitmap = (Bitmap) data.getExtras().get("data");
			super.onActivityResult(requestCode, resultCode, data);
			touxiang.setImageBitmap(cameraBitmap);

		}

	}
	class thread_insert extends Thread {
		@Override
		public void run() {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] data1 = baos.toByteArray();
			NetInfoUtil.insertpic(data1,photopath+".png"); 
//			all_image = NetInfoUtil.getPicture(photopath+".png");
//			F_GetBitmap.setInSDBitmap(all_image, photopath+".png");
		}

	}
	private class thread_set extends Thread{
		@Override
		public void run(){
			name = shetuaname.getText().toString();
			mes = name;
			if (!shetuankouhao.getText().toString().equals("")) {
				kouhao = shetuankouhao.getText().toString();
			} else {
				kouhao = shetuankouhao.getHint().toString();
			}
			mes = mes + "<#>" + kouhao;
			if (!shetuanjieshao.getText().toString().equals("")) {
				jianjie = shetuanjieshao.getText().toString();
			} else {
				jianjie = shetuanjieshao.getHint().toString();
			}
			mes = mes + "<#>" + jianjie;
			
			
			//maxid=NetInfoUtil.GetShetuanMaxid();
			int maxidd=Integer.parseInt(maxid)+1;
			mes = mes +"<#>"+(maxidd+"");
			NetInfoUtil.zengjiashetuan(mes);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) // ???????????????
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent it=new Intent(zengjiashetuan.this,shetuanmanger.class);
			finish();
			startActivity(it);
		}
		return false;
	}
}
