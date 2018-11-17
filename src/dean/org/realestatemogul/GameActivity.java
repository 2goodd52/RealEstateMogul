package dean.org.realestatemogul;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * Main activity class
 */
public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameView = new GameView(this);
        new Thread(gameView).start();
        setContentView(gameView);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d("Debug", "Saving");
        gameView.onDestroy();

    }

    
}
