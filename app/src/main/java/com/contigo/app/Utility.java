package com.contigo.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class Utility
	{

		public static final int PROFILE_PIC = 900;
		public static final int VISITING_CARD = 901;
		public static File Copy_sourceLocation;
		public static File Paste_Target_Location;
		public static File MY_IMG_DIR, Default_DIR;
		public static Uri uri;
		public static Intent pictureActionIntent = null;
		public static final int CAMERA_PICTURE = 1;
		public static final int GALLERY_PICTURE = 2;
		public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
		public static final int CONNECTION_PORT = 4122;
		public static final int REQUEST_LOCATION_SERVICES = 1902;
		public static final String STATE_SENT = "sent";
		public static final String STATE_RECV = "received";



		public static String Get_Random_File_Name()
			{
				final Calendar c = Calendar.getInstance();
				int myYear = c.get(Calendar.YEAR);
				int myMonth = c.get(Calendar.MONTH);
				int myDay = c.get(Calendar.DAY_OF_MONTH);
				String Random_Image_Text = "" + myDay + myMonth + myYear + "_" + Math.random();
				return Random_Image_Text;
			}

		// Copy your image into specific folder 
		public static File copyFile(File current_location, File destination_location)
			{
				Copy_sourceLocation = new File("" + current_location);
				Paste_Target_Location = new File("" + destination_location + "/" + Utility.Get_Random_File_Name() + ".jpg");

				Log.v("Purchase-File", "sourceLocation: " + Copy_sourceLocation);
				Log.v("Purchase-File", "targetLocation: " + Paste_Target_Location);
				try
					{
						// 1 = move the file, 2 = copy the file
						int actionChoice = 2;
						// moving the file to another directory
						if (actionChoice == 1)
							{
								if (Copy_sourceLocation.renameTo(Paste_Target_Location))
									{
										Log.i("Purchase-File", "Move file successful.");
									} else
									{
										Log.i("Purchase-File", "Move file failed.");
									}
							}

						// we will copy the file
						else
							{
								// make sure the target file exists
								if (Copy_sourceLocation.exists())
									{

										InputStream in = new FileInputStream(Copy_sourceLocation);
										OutputStream out = new FileOutputStream(Paste_Target_Location);

										// Copy the bits from instream to outstream
										byte[] buf = new byte[1024];
										int len;

										while ((len = in.read(buf)) > 0)
											{
												out.write(buf, 0, len);
											}
										in.close();
										out.close();

										Log.i("copyFile", "Copy file successful.");

									} else
									{
										Log.i("copyFile", "Copy file failed. Source file missing.");
									}
							}

					} catch (NullPointerException e)
					{
						Log.i("copyFile", "" + e);

					} catch (Exception e)
					{
						Log.i("copyFile", "" + e);
					}
				return Paste_Target_Location;
			}

		// 	decode your image into bitmap format 
		public static Bitmap decodeFile(File f)
			{
				try
					{
						BitmapFactory.Options o = new BitmapFactory.Options();
						o.inJustDecodeBounds = true;
						BitmapFactory.decodeStream(new FileInputStream(f), null, o);

						// Find the correct scale value. It should be the power of 2.
						final int REQUIRED_SIZE = 70;
						int width_tmp = o.outWidth, height_tmp = o.outHeight;
						int scale = 1;
						while (true)
							{
								if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
									break;
								width_tmp /= 2;
								height_tmp /= 2;
								scale++;
							}

						BitmapFactory.Options o2 = new BitmapFactory.Options();
						o2.inSampleSize = scale;
						return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
					} catch (FileNotFoundException e)
					{
						Log.e("decodeFile", "" + e);
					}
				return null;
			}

		// Create New Dir (folder) if not exist 
		public static File Create_MY_IMAGES_DIR()
			{
				try
					{
						// Get SD Card path & your folder name
						MY_IMG_DIR = new File(Environment.getExternalStorageDirectory(), "/My_Image/");

						// check if exist 
						if (!MY_IMG_DIR.exists())
							{
								// Create New folder 
								MY_IMG_DIR.mkdirs();
								Log.i("path", ">>.." + MY_IMG_DIR);
							}
					} catch (Exception e)
					{
						// TODO: handle exception
						Log.e("Create_MY_IMAGES_DIR", "" + e);
					}
				return MY_IMG_DIR;
			}



		public String storeImage(Bitmap image, Context c, int imageType) {
			File pictureFile = getOutputMediaFile(c, imageType);
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions: ");// e.getMessage());
				return null;
			}
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				image.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.close();
				return pictureFile.getPath();
			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}

			return null;
		}

		public String storeImageAsync(final Bitmap image, Context c, int imageType) {
			final File pictureFile = getOutputMediaFile(c, imageType);
			if (pictureFile == null) {
				Log.d(TAG,
						"Error creating media file, check storage permissions: ");// e.getMessage());
				return null;
			}

				new Thread(new Runnable() {
					@Override
					public void run() {
						FileOutputStream fos = null;
						try {
							fos = new FileOutputStream(pictureFile);
							image.compress(Bitmap.CompressFormat.PNG, 90, fos);
							fos.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}).start();

				return pictureFile.getPath();


		}

		/** Create a File for saving an image or video */
		public   File getOutputMediaFile(Context c, int imageType){
			// To be safe, you should check that the SDCard is mounted
			// using Environment.getExternalStorageState() before doing this.

			File mediaStorageDir;

			if (imageType == PROFILE_PIC)
			{
				mediaStorageDir = new File(Environment.getExternalStorageDirectory()
						+ "/Android/data/"
						+ c.getPackageName()
						+ "/files/ContiGo/ProfilePics");
			} else {
				mediaStorageDir = new File(Environment.getExternalStorageDirectory()
						+ "/Android/data/"
						+ c.getPackageName()
						+ "/files/ContiGo/VisitingCards");
			}



			// This location works best if you want the created images to be shared
			// between applications and persist after your app has been uninstalled.

			// Create the storage directory if it does not exist
			if (! mediaStorageDir.exists()){
				if (! mediaStorageDir.mkdirs()){
					return null;
				}
			}
			// Create a media file name
			String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
			File mediaFile;
			String mImageName="CG_"+ timeStamp +".jpg";
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
			return mediaFile;
		}



		public static boolean checkAndRequestPermissions(Activity context) {
			int permissionCAMERA = ContextCompat.checkSelfPermission(context,
					Manifest.permission.CAMERA);


			int storagePermission = ContextCompat.checkSelfPermission(context,


					Manifest.permission.READ_EXTERNAL_STORAGE);

			int storagePermission2 = ContextCompat.checkSelfPermission(context,


					Manifest.permission.WRITE_EXTERNAL_STORAGE);

			int contactsReadPermission = ContextCompat.checkSelfPermission(context,


					Manifest.permission.READ_CONTACTS);

			int contactsWritePermission = ContextCompat.checkSelfPermission(context,


					Manifest.permission.WRITE_CONTACTS);

			int coarseLocationPermission = ContextCompat.checkSelfPermission(context,


					Manifest.permission.ACCESS_COARSE_LOCATION);

			int fineLocationPermission = ContextCompat.checkSelfPermission(context,


					Manifest.permission.ACCESS_FINE_LOCATION);

			int getAccountsPermission = ContextCompat.checkSelfPermission(context,


					Manifest.permission.GET_ACCOUNTS);



			List<String> listPermissionsNeeded = new ArrayList<>();
			if (storagePermission != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
			}
			if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.CAMERA);
			}
			if (contactsReadPermission != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
			}
			if (contactsWritePermission != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.WRITE_CONTACTS);
			}
			if (coarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
			}
			if (fineLocationPermission != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
			}
			if (storagePermission2 != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
			}
			if (getAccountsPermission != PackageManager.PERMISSION_GRANTED) {
				listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
			}
			if (!listPermissionsNeeded.isEmpty()) {
				ActivityCompat.requestPermissions(context,


						listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_CAMERA);
				return false;
			}

			return true;
		}

		public static void displayPromptForEnablingGPS(
				final Activity activity)
		{

			final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
			new MaterialStyledDialog.Builder(activity)
					.setTitle("Location Services Required!")
					.setDescription(Html.fromHtml("Enable either GPS or any other location service to find nearby devices.Click OK to go to location services settings and enable it to let you do so."))
					.setHeaderColor(R.color.dialog_header)
					.setIcon(R.drawable.failed)
					.setNegativeText("Cancel")
					.setPositiveText("Ok")
					.onPositive(new MaterialDialog.SingleButtonCallback() {
						@Override
						public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
							activity.startActivityForResult(new Intent(action), REQUEST_LOCATION_SERVICES);


						}
					})
					.withDialogAnimation(true)
					.show();


		}
	}
