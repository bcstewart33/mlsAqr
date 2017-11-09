/*******************************************************************
*
*   Source File: Constants.java
*   Description: Interface for all game classes
*   Date:        Mon Sept 28 2017
*
********************************************************************/

package game.aqr;

public abstract class Constants {

    public static final int MAX_COL = 12;
    public static final int MAX_ROW = 9;

    public static final int MAX_COMPANIES = 7;
    public static final int MAX_PLAYERS = 6;

    public static final int MAX_TILES = MAX_COL * MAX_ROW;

    public static final int MAX_DEFUNCT_SIZE = 10;
    public static final int MAX_RANGE = 99;

    public static final int CASH_START = 6000;
    public static final int PROFIT_START = 10000;

    public static final int MAX_NAME_LENGTH = 15;
    public static final int MAX_NUM_TURNS = 54;
    public static final int MAX_NUM_TILES = 6;

    public enum Type {

        Champion,
        Playoff
    };

    public enum Mode {

        Open,
        Closed,
        Manual,
        Network,
    };

    public enum PayMode {

        FirstOnly,
        FirstSecond
    };

    public enum CompanyId {

        UNDEF,
        CON,
        IMP,
        AMR,
        FST,
        WWD,
        LUX,
        TWR,
        BLK,
        MAX
    };

    public enum CompanyStatus {

        Closed,
        Open,
        Safe
    };

    public enum TileStatus {

        Unplayable,
        Playable,
        Obsolete,
        DefunctCompany,
        StartCompany,
        AddCompany
    };

    public enum PlayerType {

        Unknown,
        Human,
        Computer,
    };

    public enum ModifierType {

        Set,
        Add,
        Sub
    };

    public static class CompanySet {

        protected boolean [] _set = new boolean [CompanyId.values ().length];

        public CompanySet () { clear (); }

        public void set (CompanyId id, boolean val) { _set[id.ordinal ()] = val; } 
        public boolean get (CompanyId id) { return _set[id.ordinal ()]; } 

        public void clear () {

            for (CompanyId id : CompanyId.values ()) { _set[id.ordinal ()] = false; }
        }
    };

    public static class StockOrder {

        protected int [] _set = new int [CompanyId.values ().length];

        public StockOrder () { ; }

        public void set (CompanyId id, int val) { _set[id.ordinal ()] = val; } 
        public void incr (CompanyId id, int val) { _set[id.ordinal ()] += val; } 
        public void decr (CompanyId id, int val) { _set[id.ordinal ()] -= val; } 
        public int get (CompanyId id) { return _set[id.ordinal ()]; } 
        public int get (int ix) {

            int result = -1;

            if (ix >= 0 && ix < CompanyId.values ().length) { result = _set[ix]; }

            return result;
        } 

        public void clear () {

            for (CompanyId id : CompanyId.values ()) { _set[id.ordinal ()] = 0; }
        }
    };

    public static class SelectContainer {

        private static final int MAX_SIZE = 10;
        private String [][] __arry = new String[MAX_PLAYERS + 1][MAX_SIZE];

        public SelectContainer () { clear (); }

        public void set (int ix, int iy, String val) {

            if (ix >= 0 && ix <= MAX_PLAYERS && iy >= 0 && iy < MAX_SIZE) { __arry[ix][iy] = val; }
        } 

        public String get (int ix, int iy) {

            String result = "";

            if (ix >= 0 && ix <= MAX_PLAYERS && iy >= 0 && iy < MAX_SIZE) { result = __arry[ix][iy]; }

            return result;
        }

        public void clear ()
        {
            for (int ix = 0; ix <= MAX_PLAYERS; ix++) {

                for (int iy = 0; iy <= MAX_SIZE; iy++) { __arry[ix][iy] = ""; }
            }
        }
    }

    public static class TextBlock {

        private String [] __arry = new String[MAX_PLAYERS + 1];

        public TextBlock () { clear (); }

        public void set (int ix, String val) {

            if (ix >= 0 && ix <= MAX_PLAYERS) { __arry[ix] = val; }
        } 
        public String get (int ix) {

            String result = "";

            if (ix >= 0 && ix <= MAX_PLAYERS) { result = __arry[ix]; }

            return result;
        }

        public void clear ()
        {
            for (int ix = 0; ix <= MAX_PLAYERS; ix++) { __arry[ix] = ""; }
        }
    };
};
