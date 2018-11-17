package dean.org.realestatemogul;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Dean on 13/01/2018.
 */

/**
 * Class that is used as the game engine.
 */
public class GameView extends SurfaceView implements Runnable {

    private boolean isRunning = true; // Determines whether or not the game is running.

    private final int GAME_HEIGHT = 1600; // The height of the game
    private final int GAME_WIDTH = 900; // The width of the game
    private final int GAME_FRAME_DELAY = 20; // The delay between each game loop cycle, 20 * 50 = 1000ms (50fps)

    private Paint paint; // The paint object for rendering.
    private Canvas gameCanvas; // The Canvas object for rendering our game into
    private Bitmap gameBuffer; // The underlying bitmap that the game canvas draws to
    private Game game; // Instance of the Game class that controls the actual game

    private DatabaseManager databaseManager;

    /**
     * Constructor of GameView
     * @param context The application context
     */
    public GameView(final Context context)
    {
        super(context);
        paint = new Paint();
        databaseManager = new DatabaseManager(context);
        gameBuffer = Bitmap.createBitmap(GAME_WIDTH, GAME_HEIGHT, Bitmap.Config.ARGB_8888);
        gameCanvas = new Canvas(gameBuffer);
        game = new Game(context);
        game.load(databaseManager);
    }

    /**
     * The game loop itself, continuous loop of processing and rendering with sleep delays.
     */
    @Override
    public void run()
    {
        while (isRunning)
        {
            long now = System.currentTimeMillis();

            process();
            render();

            long sleepTime = (GAME_FRAME_DELAY - (System.currentTimeMillis() - now));
            if(sleepTime > 0)
            {
                try {
                    Thread.sleep(sleepTime);
                } catch(final InterruptedException ie) {
                    ie.printStackTrace();
                }
            }

        }
    }

    /**
     * Method for updating the game before rendering in each cycle.
     */
    public void process()
    {
        game.process();
    }

    /**
     * Renders the game onto the SurfaceView, the Canvas object that the game is rendered to
     * is scaled to the current devices screen size.
     */
    public void render()
    {
        gameCanvas.drawColor(SCREEN_COLOUR.toArgb()); //Clear the canvas
        game.render(gameCanvas, paint); //Render the game


        if (getHolder().getSurface().isValid())
        {
            final Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(Bitmap.createScaledBitmap(gameBuffer, getWidth(), getHeight(), true), 0, 0, paint); // Scale the game to the device screen size
            getHolder().unlockCanvasAndPost(canvas);
        }

    }


    /**
     * Used to monitor user touch events.
     * @param motionEvent The event that was fired when the user touched the screen.
     * @return true to consume the event and stop other potential classes using it.
     */
    @Override
    public boolean onTouchEvent(final MotionEvent motionEvent)
    {
        int touchX = (int) (motionEvent.getX() / (((double) getWidth() / (double) GAME_WIDTH))); // Scale the click down to the size of the game
        int touchY = (int) (motionEvent.getY() / (((double) getHeight() / (double) GAME_HEIGHT))); // Scale the click down to the size of the game
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            game.handlePress(touchX, touchY);
        else
        if(motionEvent.getAction() == MotionEvent.ACTION_UP)
            game.handleRelease(touchX, touchY);
        return true;
    }

    /**
     * Called when the activity is destroyed to save the game.
     */
    public void onDestroy()
    {
        game.save(databaseManager);
    }

    private final Color SCREEN_COLOUR = Color.valueOf(0xffdbf2fc); // Colour to use when clearing the screen

}
