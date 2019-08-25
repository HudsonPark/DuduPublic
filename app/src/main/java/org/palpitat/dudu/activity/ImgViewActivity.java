package org.palpitat.dudu.activity;

        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.os.Bundle;
        import android.os.Parcelable;
        import android.support.annotation.Nullable;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.palpitat.dudu.R;

        import java.util.ArrayList;

public class ImgViewActivity extends AppCompatActivity {
    private ImageView ivImg;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_img);

           //Intent intent = getIntent();
           //intent.getExtras();
           //ArrayList arr = intent.getParcelableArrayListExtra("이미지");
           // System.out.println(arr);

         //   Toast.makeText(getApplicationContext(),arr + "가출력", Toast.LENGTH_LONG).show();
         //   Log.d("테스트좀",arr +"가 출력");



    }
}
