package com.hfad.starbuzz;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.BoolRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class DrinkActivity extends AppCompatActivity
{


   public static final String EXTRA_DRINKNO="drinkNo";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink);
        int drinkno=(Integer)getIntent().getExtras().get(EXTRA_DRINKNO);

        try
        {
            SQLiteOpenHelper starbuzzDatabaseHelper=new StarbuzzDatabaseHelper(this);
            SQLiteDatabase db=starbuzzDatabaseHelper.getWritableDatabase();
            Cursor cursor=db.query("DRINK",new String[] {"NAME","DESCRIPTION","IMAGE_RESOURCE_ID","FAVORITE"},"_ID=?",new String[] {Integer.toString(drinkno)},null,null,null);

            if(cursor.moveToFirst())
            {
                String nameText=cursor.getString(0);
                String descriptionText=cursor.getString(1);
                int photoId=cursor.getInt(2);
                boolean isFavorite=(cursor.getInt(3)==1);


                TextView name=(TextView)findViewById(R.id.name);
                name.setText(nameText);

                TextView description=(TextView)findViewById(R.id.description);
                description.setText(descriptionText);


                ImageView photo=(ImageView)findViewById(R.id.photo);
                photo.setImageResource(photoId);
                photo.setContentDescription(descriptionText);

                CheckBox favorite=(CheckBox)findViewById(R.id.favorite);
                favorite.setChecked(isFavorite);
            }
            cursor.close();
            db.close();


        }catch (SQLiteException e)
        {
            Toast toast=Toast.makeText(this,"database unavailable",Toast.LENGTH_SHORT);
            toast.show();
        }


    }

    public void onFavoriteClicked(View view) {
        int drinkNo = (Integer) getIntent().getExtras().get("drinkNo");
        new UpdateDrinkTask().execute(drinkNo);
    }
    private class UpdateDrinkTask extends AsyncTask<Integer,void,Boolean> {
        ContentValues drinkValues;
        protected void onPreExecute()
        {
            CheckBox favorite=(CheckBox)findViewById(R.id.favorite);
            drinkValues=new ContentValues();
            drinkValues.put("FAVOURITE",favorite.isChecked());
        }
        protected Boolean doInBackground(Integer... drinks)
        {
            int drinkNo=drinks[0];
            SQLiteOpenHelper starbuzzDatabaseHelper=new StarbuzzDatabaseHelper(DrinkActivity.this);
            try{
                SQLiteDatabase db=starbuzzDatabaseHelper.getWritableDatabase();
                db.update("DRINK",drinkValues,"_id=?",new String[] {Integer.toString(drinkNo)});
                db.close();
                return true;
        }catch (SQLiteException e)
            {
                return false;
            }


    }
    protected void onPostExecute(Boolean success)
    {
        if(!success)
        {
            Toast toast=Toast.makeText(DrinkActivity.this,"Database unavailable",Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    }


}
