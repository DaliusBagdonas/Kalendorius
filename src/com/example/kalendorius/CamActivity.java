package com.example.kalendorius;


import java.io.File;
import java.io.FileInputStream;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CamActivity extends Activity {
	
    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    
    static SharedPreferences prefs;
    
    Uri imageUri = null;
    static TextView imageDetails = null;
    public static ImageView showImg = null;
    CamActivity CameraActivity = null;
    private static String fileName;
    
    final static private String APP_KEY = "smp3e9cfxv9rv9u";
    final static private String APP_SECRET = "zwyvujr12e7y6ey";
    final static private AccessType ACCESS_TYPE = AccessType.DROPBOX;  
    private static DropboxAPI<AndroidAuthSession> mDBApi;
    public static String Path = "";
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam);
        
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, ACCESS_TYPE);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startAuthentication(CamActivity.this);
     
        CameraActivity = this;
        
        prefs = this.getSharedPreferences("DUOMENYS", Context.MODE_PRIVATE);
        
        imageDetails = (TextView) findViewById(R.id.imageDetails);
        showImg = (ImageView) findViewById(R.id.showImg);
         
        final Button photo = (Button) findViewById(R.id.capture);
         
        photo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {        
                
                Intent iin= getIntent();
                Bundle b = iin.getExtras();
                if (b!=null)
                {
                    String j =(String) b.get("data");                            
                    fileName=j;
                }
                
                ContentValues values = new ContentValues();   
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
                                  
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                 
               Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
               intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);                  
               intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);                  
               startActivityForResult( intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                                 
            }   
             
        });
    }    
        @Override
        protected void onActivityResult( int requestCode, int resultCode, Intent data)
           {
               if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                    
                   if ( resultCode == RESULT_OK) {
                       String imageId = convertImageUriToFile( imageUri,CameraActivity);
                                                
                   } else if ( resultCode == RESULT_CANCELED) {
                       Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                   } else {
                       Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
                   }
               }
           }
        String date;
        
        public static String convertImageUriToFile ( Uri imageUri, Activity activity )  {
        		
               Cursor cursor = null;
               int imageID = 0;
               
               try {
                
                   String [] proj={
                                    MediaStore.Images.Media.DATA,
                                    MediaStore.Images.Media._ID,
                                    MediaStore.Images.Thumbnails._ID,
                                    MediaStore.Images.ImageColumns.ORIENTATION
                                  };
                    
                   cursor = activity.managedQuery(imageUri, proj, null, null, null);
                    
                   int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                   int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
                   int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                   int size = cursor.getCount();
                                        
                   if (size == 0) {
                       imageDetails.setText("No Image");
                   }
                   else
                   {
                       int thumbID = 0;
                       if (cursor.moveToFirst()) {
                            
                           imageID = cursor.getInt(columnIndex);
                           thumbID = cursor.getInt(columnIndexThumb);
                           Path = cursor.getString(file_ColumnIndex);
                                                       
                           String CapturedImageDetails = "Nuotraukos detales: \n\n"
                        		   							 +"Pavadinimas: "+fileName+"\n"
                                                             +"PaveikslelioID: "+imageID+"\n"
                                                             +"Saugojimo vieta: "+Path;
                            
                           imageDetails.setText( CapturedImageDetails );
                           prefs.edit().putInt(fileName, 1).commit();
                            
                       }
                   }   
               } finally {
                   if (cursor != null) {
                	   cursor = null;
                   }
               }
               

   			try {
   				Thread thread = new Thread(new Runnable(){
   				    @Override
   				    public void run() {
   				        try {
   				        	File file = new File(CamActivity.Path);
   				        	FileInputStream inputStream = new FileInputStream(file);
   				        	Entry response = mDBApi.putFile("/fileName.jpg", inputStream,
   			   				        file.length(), null, null);
   				        } catch (Exception e) {
   				            e.printStackTrace();
   				        }
   				    }
   				});
   				
   				thread.start(); 
   			} catch (Exception e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}                                
               return ""+imageID;          
           }
        
        protected void onResume() {
            super.onResume();

            if (mDBApi.getSession().authenticationSuccessful()) {
                try {
                   mDBApi.getSession().finishAuthentication();
                   AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();
               }catch (IllegalStateException e) {
                   Log.i("DbAuthLog", "Error authenticating", e);
                }
            }
        }
        
    }