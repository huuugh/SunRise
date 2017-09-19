package sunrise.hugh.com.sunrise;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SunRiseView sr = (SunRiseView) findViewById(R.id.sr);
        TextView tv_start = (TextView) findViewById(R.id.tv_start);

        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sr.setTimes("7:00","19:00","15:30");
            }
        });
    }
}
