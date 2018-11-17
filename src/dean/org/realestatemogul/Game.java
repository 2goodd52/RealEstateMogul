package dean.org.realestatemogul;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.text.DecimalFormat;

import dean.org.realestatemogul.entity.Building;

/**
 * Created by Dean on 13/01/2018.
 */

/**
 * Class that handles the actual Game
 */
public class Game {

    private Context context; // Context instance for use with getting resources

    private long money; // The players money
    private int[] ownedProperties = new int[6]; // Array holding amount of properties of each type the player owns
    private boolean coinPressed = false; // Whether or not the coin has been pressed, used to display different image if so
    private long lastPayment = 0; // A timer to ensure that the player gets paid every second (game runs at 50 frames per second)

    /**
     * Constructor for creating the Game class.
     * @param context The context passed through to this class, for use with getting resources.
     */
    public Game(final Context context)
    {
        this.context = context;
        loadAssets();
    }

    /**
     * Method that is called every iteration of the game loop
     * from the GameView class. Used to update the status of the game.
     */
    public void process()
    {
        if((System.currentTimeMillis() - lastPayment) >= 1000)
        {
            for (int index = 0; index < ownedProperties.length; index++)
            {
                if(ownedProperties[index] < 1)
                    continue;
                money += (ownedProperties[index] * Building.values()[index].getIncome());
            }
            lastPayment = System.currentTimeMillis();
        }
    }

    /**
     * Method for rendering the game to the underlying Canvas object.
     * @param canvas The canvas that the game information and assets should be rendered onto.
     * @param paint The paint object that is used for rendering, such as setting colours and font sizes.
     */
    public void render(final Canvas canvas, final Paint paint)
    {
        canvas.drawBitmap(background, 0, 0, paint);
        canvas.drawBitmap(coinPressed ? coinDown : coin, 349, 1365, paint);
        paint.setTextSize(30f);
        final String wealthText = "Total wealth: £" + formatNumber((int) money);
        final String incomeText = "Income per second: £" + formatNumber(getIncomePerSecond());
        canvas.drawText(wealthText, 100, 272, paint);
        canvas.drawText(incomeText, 800 - paint.measureText(incomeText), 272, paint);

        for (int index = 0; index < Building.values().length; index++)
        {
            final Building building = Building.values()[index];
            int baseX = propertyOffsetX;
            int baseY = propertyOffsetY + (propertyHeight * index);
            paint.setTextSize(30f);
            canvas.drawText(building.getName(), (baseX + (100 - (paint.measureText(building.getName()) / 2))), baseY + 50, paint);
            canvas.drawBitmap(BitmapFactory.decodeResource(context.getResources(), building.getResourceId()), baseX + 68, baseY + 65, paint);
            canvas.drawText("£" + formatNumber(building.getIncome()) + "/s", (baseX + (100 - paint.measureText("£" + formatNumber(building.getIncome()) + "/s") / 2)), baseY + 160, paint);
            canvas.drawText(building.getDescription(), (baseX + (350 - paint.measureText(building.getDescription()) / 2)), baseY + 100, paint);

            paint.setTextSize(20f);
            canvas.drawText("Price: £" + formatNumber(building.getCost(ownedProperties[index])), baseX + 565, baseY + 75, paint);
            canvas.drawText("Owned: " + formatNumber(ownedProperties[index]), baseX + 565, baseY + 115, paint);
            canvas.drawBitmap(money >= building.getCost(ownedProperties[index]) ?
                              buyOver : buyUnder, baseX + 750, baseY + 60, paint);

        }

    }

    /**
     * Handles what happens when the user presses down on the game screen,
     * such as buying properties.
     * @param touchX The x coordinate of the users press.
     * @param touchY The y coordinate of the users press.
     */
    public void handlePress(final int touchX, final int touchY)
    {
        final Rect coin = new Rect(349, 1365, 549, 1565);
        if(coin.contains(touchX, touchY))
        {
            money++;
            coinPressed = true;
            return;
        }
        for (int index = 0; index < Building.values().length; index++)
        {
            int baseX = propertyOffsetX;
            int baseY = propertyOffsetY + (propertyHeight * index);
            final Rect buy = new Rect(baseX + 750, baseY + 60, baseX + 750 + 75, baseY + 60 + 50);
            if(buy.contains(touchX, touchY))
            {
                if(money >= Building.values()[index].getCost(ownedProperties[index]))
                {
                    money -= Building.values()[index].getCost(ownedProperties[index]);
                    ownedProperties[index]++;
                    break;
                }
            }
        }
    }

    /**
     * Handles what happens when the user releases their finger from the screen.
     * @param touchX The x coordinate of the users press.
     * @param touchY The y coordinate of the users press.
     */
    public void handleRelease(final int touchX, final int touchY)
    {
        coinPressed = false;
    }

    /**
     * Loads drawable images that are used multiple times into memory to avoid loading every time
     * that they're needed.
     */
    private void loadAssets()
    {
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        coin = BitmapFactory.decodeResource(context.getResources(), R.drawable.coin);
        coinDown = BitmapFactory.decodeResource(context.getResources(), R.drawable.coinpressed);
        buyUnder = BitmapFactory.decodeResource(context.getResources(), R.drawable.buyunder);
        buyOver = BitmapFactory.decodeResource(context.getResources(), R.drawable.buyover);
    }

    /**
     * Returns the total amount of income per second the users properties generate.
     * @return the amount per second all properties will generate.
     */
    public int getIncomePerSecond()
    {
        int earnings = 0;
        for (int index = 0; index < ownedProperties.length; index++) {
            if (ownedProperties[index] < 1)
                continue;
            earnings += (ownedProperties[index] * Building.values()[index].getIncome());
        }
        return earnings;
    }

    /**
     * Formats numbers by adding commas between different values.
     * @param number The number to be formatted.
     * @return The formatted number as a String.
     */
    private String formatNumber(final int number)
    {
        return new DecimalFormat("###,###,###,###,###,###,###").format(number);
    }

    /**
     * Loads the players data from the database
     * @param manager The instance of the database manager
     */
    public void load(final DatabaseManager manager)
    {
        final Cursor data = manager.getAllEntries();
        Log.d("Entries", "There are " + data.getCount() + " entries in table.");
        if(data.getCount() == 0)
            return;
        if(data.moveToFirst())
        {
            money = data.getInt(1);
            ownedProperties[0] = data.getInt(2);
            ownedProperties[1] = data.getInt(3);
            ownedProperties[2] = data.getInt(4);
            ownedProperties[3] = data.getInt(5);
            ownedProperties[4] = data.getInt(6);
            ownedProperties[5] = data.getInt(7);
        }
    }

    /**
     * Save the players data to the database
     * @param manager The instance of database manager
     */
    public void save(final DatabaseManager manager)
    {
        if(!manager.insertEntry((int) money, ownedProperties[0], ownedProperties[1], ownedProperties[2], ownedProperties[3], ownedProperties[4], ownedProperties[5]))
            manager.updateEntry((int) money, ownedProperties[0], ownedProperties[1], ownedProperties[2], ownedProperties[3], ownedProperties[4], ownedProperties[5]);
    }

    /**
     * Declaration of assets
     */
    private Bitmap background;
    private Bitmap coin;
    private Bitmap coinDown;
    private Bitmap buyUnder;
    private Bitmap buyOver;

    /**
     * Used to determine X, Y positions of properties in the list
     */
    private int propertyOffsetX = 16;
    private int propertyOffsetY = 290;
    private int propertyHeight = 175;

}
