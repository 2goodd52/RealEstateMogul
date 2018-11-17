package dean.org.realestatemogul.entity;

import dean.org.realestatemogul.R;

/**
 * Created by Dean on 13/01/2018.
 */

/**
 * Enum for handling different properties within the game.
 */
public enum Building {

    TENT(100, 1, R.drawable.tent, "A nice sturdy tent."),
    CARAVAN(1100, 8, R.drawable.caravan, "A rusty caravan."),
    FLAT(12000, 47, R.drawable.flat, "A small flat."),
    HOUSE(130000, 260, R.drawable.house, "A nice house."),
    MANSION(1400000, 1400, R.drawable.mansion, "A stunning mansion."),
    CASTLE(20000000, 7800, R.drawable.castle, "A spectacular castle.");

    private int baseCost;
    private int baseIncome;
    private int resourceId;
    private String description;

    private Building(final int baseCost, final int baseIncome, final int resourceId, final String description)
    {
        this.baseCost = baseCost;
        this.baseIncome = baseIncome;
        this.resourceId = resourceId;
        this.description = description;
    }

    public int getCost(final int current)
    {
        return (int) Math.ceil(baseCost * (Math.pow(1.15, current)));
    }

    public int getIncome()
    {
        return baseIncome;
    }

    public String getName()
    {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }

    public int getResourceId()
    {
        return resourceId;
    }

    public String getDescription()
    {
        return description;
    }

}
