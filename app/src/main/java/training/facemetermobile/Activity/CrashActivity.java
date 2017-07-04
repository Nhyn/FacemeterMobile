package training.facemetermobile.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import training.facemetermobile.R;

/**
 * Created by agrfiqie6136 on 19/10/2016.
 */

public class CrashActivity extends Activity {

    @BindView(R.id.btn_crash) Button btnCrash;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crash_activity);
        ButterKnife.bind(this);

        btnCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new RuntimeException("This is a crash");
            }
        });
        ;

    }
}

