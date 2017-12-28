
/*******************************************************************
*
*   Source File: Database.java
*   Description: Java Source file for AQR application
*   Date:        Sept 28 2017
*
********************************************************************/

package game.aqr;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import game.aqr.Tile;

public interface Database {

    //database Methods
    public byte[] getCurrentState ();
    public boolean setCurrentState (byte[] data);

    //board Methods
    public boolean playBoardTile (Tile.LocationConst loc, Constants.CompanyId id);

    public Tile.Piece getBoardTile (Tile.LocationConst loc);

    //company Methods
    public String getCompanyName (Constants.CompanyId company);

    public void setCompanyStatus (Constants.CompanyId company, Constants.CompanyStatus Status);

    public Constants.CompanyStatus getCompanyStatus (Constants.CompanyId company);

    public void setCompanySize (Constants.CompanyId company, int Size);

    public int getCompanySize (Constants.CompanyId company);

    public long getCompanyPrice (Constants.CompanyId company);

    public void setCompanyStockCount (Constants.CompanyId company, int stockCount, Constants.ModifierType modifier);

    public int getCompanyStockCount (Constants.CompanyId company);

    public boolean isCompanyStockAvailable (Constants.CompanyId company);

    //player Methods
    public boolean isPlayerActive (int playerId);

    public void setPlayerName (int playerId, String name);

    public String getPlayerName (int playerId);

    public void setPlayerCash (int playerId, long cash, Constants.ModifierType modifier);

    public long getPlayerCash (int playerId);

    public void setPlayerProfit (int playerId, long profit, Constants.ModifierType modifier);

    public long getPlayerProfit (int playerId);

    public void setPlayerStockCount (int playerId, Constants.CompanyId company, int stockCount, Constants.ModifierType modifier);
    public int getPlayerStockCount (int playerId, Constants.CompanyId company);

    public void updatePlayerIncomeStats (int playerId, long subTot);

    public void updatePlayerGNPStats (int playerId, long gnp);
};


class DatabaseImpl implements Database {

    protected class PlayerStats {

        public long gnp;
        public long profit;
        public long netWorth;
        public long income;

        public PlayerStats () {

            gnp = 0;
            profit = 0;
            netWorth = 0;
            income = 0;
        }
    };

    protected class PlayerStruct {

        public boolean active;
        public String name;
        public Constants.StockOrder stockCount;
        public long cash;
        public long profit;

        public int statIndex;
        public PlayerStats[] stats;

        public PlayerStruct () {

            active = false;
            name = "";
            stockCount = new Constants.StockOrder ();//int [Constants.MAX_COMPANIES + 1];
            cash = Constants.CASH_START;
            profit = Constants.PROFIT_START;
            statIndex = 0;
            stats = new PlayerStats[Constants.MAX_NUM_TURNS];

            for (int ix = 0; ix < Constants.MAX_NUM_TURNS; ix++) {

                stats[ix] = new PlayerStats ();

                stats[ix].gnp = 0;
                stats[ix].profit = 0;
                stats[ix].netWorth = 0;
                stats[ix].income = 0;
            }
        }
    };

    protected class CompanyStruct {

        public String name;
        public Constants.CompanyStatus status;
        public int size;
        public int stockCount;
        public long value;

        public CompanyStruct () {

            name = "";
            status = Constants.CompanyStatus.Closed;
            size = 0;
            stockCount = 25;
            value = 0;
        }
    };

    private PlayerStruct[] __player;

    private CompanyStruct[] __company;

    private Tile.Piece[][] __board;

    private Tile.Piece __lastTilePlayed; //Last Tile played on board

    //Impl Methods
    public DatabaseImpl () {

        __player = new PlayerStruct[Constants.MAX_PLAYERS];
        __company = new CompanyStruct[Constants.CompanyId.values ().length];
        __board = new Tile.Piece[Constants.MAX_COL + 1][Constants.MAX_ROW + 1];
        __lastTilePlayed = null;

        for (Constants.CompanyId id : Constants.CompanyId.values ()) {

            __company[id.ordinal ()] = new CompanyStruct ();

            switch (id) {

                case CON:
                    __company[id.ordinal ()].name = "Continental";
                    break;

                case IMP:
                    __company[id.ordinal ()].name = "Imperial";
                    break;

                case AMR:
                    __company[id.ordinal ()].name = "American";
                    break;

                case FST:
                    __company[id.ordinal ()].name = "Festival";
                    break;

                case WWD:
                    __company[id.ordinal ()].name = "WorldWide";
                    break;

                case LUX:
                    __company[id.ordinal ()].name = "Luxor";
                    break;

                case TWR:
                    __company[id.ordinal ()].name = "Tower";
                    break;

                default:
                    __company[id.ordinal ()].name = "";
                    break;
            }
        }

        //Clear the board of all tiles
        for (int col = 0; col <= Constants.MAX_COL; col++) {

            for (int row = 0; row <= Constants.MAX_ROW; row++) {

                __board[col][row] = new Tile.Piece (col, row);
                __board[col][row].setCompanyId (Constants.CompanyId.UNDEF);
            }
        }

        //Initialize all player data
        for (int ix = 0; ix < Constants.MAX_PLAYERS; ix++) {

            __player[ix] = new PlayerStruct ();

            __player[ix].active = false;
            __player[ix].name = "";
            __player[ix].cash = Constants.CASH_START;
            __player[ix].profit = Constants.PROFIT_START;
            __player[ix].stockCount.clear ();
        }
    }

    //Database Methods
    public byte[] getCurrentState () {

        byte[] result = null;
        ArrayList<Byte> data = new ArrayList<Byte>();

        try {

            byte elem = 0;
            byte lpTile = (byte) 0xFF;

            //Add the tiles of the board
            for (int col = 1; col <= Constants.MAX_COL; col++) {

                for (int row = 1; row <= Constants.MAX_ROW; row++) {

                    int val = __board[col][row].getCompanyId ().ordinal ();

                    if (val <= 0) { //UNDEF

                        elem = 0x0;
                    }
                    else if (val <= 8) { //Valid Company

                        elem = (byte) (0x1 << (val - 1));
                    }
                    else { //MAX

                        elem = (byte) 0xFF;
                    }

                    data.add (elem);

                    //Last Played Tile
                    if (__board[col][row].getHighLight ()) { lpTile = (byte) (((col - 1) * Constants.MAX_ROW) + row); }
                }
            }

            //Add Last Played Tile
            data.add (lpTile);

            //Add Company Sizes
            for (Constants.CompanyId id : Constants.CompanyId.values ()) {

                switch (id) {

                    case UNDEF:
                    case BLK:
                    case MAX:
                        data.add ((byte) 0xFF);
                        data.add ((byte) 0xFF);
                        break;
                    default:
                        data.add ((byte) getCompanySize (id));
                        data.add ((byte) getCompanyStatus (id).ordinal ());
                        break;
                }
            }

            //Pack the data into a byte[]
            int ix = 0;
            result = new byte[data.size ()];

            for (Byte bt: data) {

                result[ix++] = bt.byteValue ();
            }
        }
        catch (Exception ex) {

            result = null;
        }
        finally {
        }

        return result;
    }

    public boolean setCurrentState (byte[] data) {

        boolean result = false;

        try {

System.out.print ("DEBUG: Unpack[");
            //Add data to board
            for (int ix = 0; ix < (Constants.MAX_TILES); ix++) {

                Constants.CompanyId comp = Constants.CompanyId.UNDEF;

                if (data[ix] == 0x0) {
                   //Already defined 
                }
                else if (data[ix] == (byte) 0xFF) {

                    comp = Constants.CompanyId.MAX;
                }
                else {

                    boolean found = false;
                    int jx = 0;

                    do {

                        if ((data[ix] & (0x1 << jx)) != 0) { found = true; }
                        else { jx++; }
                    } while (found == false && jx < 8);

                    comp = Constants.CompanyId.values()[jx + 1];

                }

                System.out.print (comp.ordinal () + ", ");
            }

            //Add Last Tile Played
System.out.println ("], LastPlayed[" + data[Constants.MAX_TILES] + "], ");

            int ix = Constants.MAX_TILES + 1;
System.out.print ("Company [");
            //Add Company Sizes
            for (Constants.CompanyId id : Constants.CompanyId.values ()) {

                switch (id) {

                    case UNDEF:
                    case BLK:
                    case MAX:
                        //Nothing to add
                        ix++; ix++;
                        break;
                    default:
System.out.print ("{" + id.toString () + "}(" + data[ix++] + ", " + data[ix++] + "), ");
                        break;
                }
            }

System.out.println ("]");
            result = true;
        }
        catch (Exception ex) {
        }
        finally {
        }

        return result;
    }

    //Board Methods
    public void clearBoard () {

        //Clear the board of all tiles
        for (int col = 0; col <= Constants.MAX_COL; col++) {

            for (int row = 0; row <= Constants.MAX_ROW; row++) {

                __board[col][row].setCompanyId (Constants.CompanyId.UNDEF);
            }
        }
    }

    public boolean playBoardTile (Tile.LocationConst loc, Constants.CompanyId id) {

        boolean result = false;

        Tile.Piece tile = __board[loc.getCol ()][loc.getRow ()];

        if (tile != null) {

            //Note:: need to figure how to draw this change
            if (__lastTilePlayed != null) { __lastTilePlayed.setHighLight (false); }

            __lastTilePlayed = tile;

            //Note:: need to figure how to draw this change
            tile.setHighLight (true);

            tile.setHidden (false);

            tile.setCompanyId (id);

            result = true;
        }

        return result;
    }

    public Tile.Piece getBoardTile (Tile.LocationConst loc) { return __board[loc.getCol ()][loc.getRow ()]; }

    //Company Methods
    public String getCompanyName (Constants.CompanyId company) { return __company[company.ordinal ()].name; }

    public void setCompanySize (Constants.CompanyId company, int size) { __company[company.ordinal ()].size = size; }

    public int getCompanySize (Constants.CompanyId company) { return __company[company.ordinal ()].size; }

    public long getCompanyPrice (Constants.CompanyId company) {

        long result = 0;

        int value = 0;

        switch (company) {

            case IMP:
            case CON:
                value = 200;
                break;
            case AMR:
            case WWD:
            case FST:
                value = 100;
                break;
        }

        int size = __company[company.ordinal ()].size;

        if (size == 2) { result = 200 + value; }
        else if (size == 3) { result = 300 + value; }
        else if (size == 4) { result = 400 + value; }
        else if (size == 5) { result = 500 + value; }
        else if (size > 5 && size < 11) { result = 600 + value; }
        else if (size > 10 && size < 21) { result = 700 + value; }
        else if (size > 20 && size < 31) { result = 800 + value; }
        else if (size > 30 && size < 41) { result = 900 + value; }
        else if (size > 40) { result = 1000 + value; }

        return result;
    }

    public boolean isCompanyStockAvailable (Constants.CompanyId company) {

        boolean result = false;

        if (__company[company.ordinal ()].status != Constants.CompanyStatus.Closed &&
            __company[company.ordinal ()].stockCount != 0) { result = true; }

        return result;
    }

    public void setCompanyStockCount (Constants.CompanyId company, int stockCount, Constants.ModifierType modifier) { 

        switch (modifier) {

            case Add:
                __company[company.ordinal ()].stockCount += stockCount;
                break;

            case Sub:
                __company[company.ordinal ()].stockCount -= stockCount;
                break;

            case Set:
            default:
                __company[company.ordinal ()].stockCount = stockCount;
                break;
        }
    }

    public int getCompanyStockCount (Constants.CompanyId company) { return __company[company.ordinal ()].stockCount; }

    public void setCompanyStatus (Constants.CompanyId company, Constants.CompanyStatus status) {

        __company[company.ordinal ()].status = status;
    }

    public Constants.CompanyStatus getCompanyStatus (Constants.CompanyId company) {

        return __company[company.ordinal ()].status;
    }

    //Player Methods
    public boolean isPlayerActive (int playerId) { return __player[playerId].active; }

    public void setPlayerName (int playerId, String name) {

        if (name.length () > 0)
        {
            __player[playerId].active = true;
            __player[playerId].name = name;
        }
        else {
            __player[playerId].active = false;
            __player[playerId].name = "";
        }
    }

    public String getPlayerName (int playerId) {

        String result = "Player" + playerId;

        if (__player[playerId].active) { result = __player[playerId].name; }

        return result;
    }

    public void setPlayerCash (int playerId, long cash, Constants.ModifierType modifier) {

        if (__player[playerId].active) {

            switch (modifier) {

                case Add:
                    __player[playerId].cash += cash;
                    break;

                case Sub:
                    __player[playerId].cash -= cash;
                    break;

                case Set:
                default:
                    __player[playerId].cash = cash;
                    break;
            }
        }
    }

    public long getPlayerCash (int playerId) {

        long result = 0;

        if (__player[playerId].active) { result = __player[playerId].cash; }

        return result;
    }

    public void setPlayerProfit (int playerId, long cash, Constants.ModifierType modifier) {

        if (__player[playerId].active) {

            switch (modifier) {

                case Add:
                    __player[playerId].profit += cash;
                    break;

                case Sub:
                    __player[playerId].profit -= cash;
                    break;

                case Set:
                default:
                    __player[playerId].profit = cash;
                    break;
            }
        }
    }

    public long getPlayerProfit (int playerId) {

        long result = 0;

        if (__player[playerId].active) { result = __player[playerId].profit; }

        return result;
    }

    public void setPlayerStockCount (int playerId, Constants.CompanyId company, int stockCount, Constants.ModifierType modifier) {

        if (__player[playerId].active) {

            switch (modifier) {

                case Add:
                    __player[playerId].stockCount.incr (company, stockCount);
                    break;

                case Sub:
                    __player[playerId].stockCount.decr (company, stockCount);
                    break;

                case Set:
                default:
                    __player[playerId].stockCount.set (company, stockCount);
                    break;
            }
        }
    }

    public int getPlayerStockCount (int playerId, Constants.CompanyId company) {

        int result = 0;

        if (__player[playerId].active) { result = __player[playerId].stockCount.get (company); }

        return result;
    }

    public void updatePlayerIncomeStats (int playerId, long subTot) {

        if (subTot < 0) { 

            int prevStatIndex = __player[playerId].statIndex;

            __player[playerId].statIndex++;

            __player[playerId].stats[__player[playerId].statIndex].netWorth = __player[playerId].cash;

            __player[playerId].stats[__player[playerId].statIndex].income =
                __player[playerId].stats[prevStatIndex].income;
        }
        else {

            __player[playerId].statIndex++;

            __player[playerId].stats[__player[playerId].statIndex].netWorth = subTot;

            __player[playerId].stats[__player[playerId].statIndex].income = __player[playerId].cash;
        }
    }

    public void updatePlayerGNPStats (int playerId, long gnp) {

        __player[playerId].stats[__player[playerId].statIndex].gnp = gnp;

        __player[playerId].stats[__player[playerId].statIndex].profit = __player[playerId].profit;
    }
};
