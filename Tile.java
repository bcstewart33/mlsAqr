/*******************************************************************
*
*   Source File: Tile.java
*   Description: Java Source file for AQR application
*   Date:        Fri Sept 29 2017
*
********************************************************************/

package game.aqr;

import java.lang.Math;
import java.util.Random;

import game.aqr.Constants;

public abstract class Tile {

    public static class LocationConst {

        protected int _col;
        protected int _row; //{1..MAX_COL} {1..MAX_ROW}

        public LocationConst () { _col = 0; _row = 0; }

        public LocationConst (int col, int row)
        {
            if (isValid (col, row)) { 

                _col = col;
                _row = row;
            }
            else { _col = 0; _row = 0; }
        }

        public static boolean isValid (int col, int row) {

            boolean result = false;

            if (col > 0 && col <= Constants.MAX_COL &&
                row > 0 && row <= Constants.MAX_ROW) { result = true; }

            return result;
        }

        public int getCol () { return _col; }
        public int getRow () { return _row; }
    };


    public static class Location extends LocationConst {

        public Location () { super (); }

        public Location (Location loc) {

            _col = loc.getCol ();
            _row = loc.getRow ();
        }

        public Location (int col, int row) {

            _col = col;
            _row = row;
        }

        // Methods
        public boolean isValid () { return isValid (_col, _row); }

        public boolean isEqual (Location loc) {

            boolean result = false;

            if (_col == loc.getCol () && _row == loc.getRow ()) { result = true; }

            return result;
        }

        public void copy (int col, int row) {

            _col = col;
            _row = row;
        }

        public void copy (Location fmLoc) {

            _col = fmLoc.getCol ();
            _row = fmLoc.getRow ();
        }

        public void incrCol (int val) { _col += val; }
        public void decrCol (int val) { _col -= val; }
        public void setCol (int val) { if (val > 0 && val <= Constants.MAX_COL) { _col = val; } }

        public void incrRow (int val) { _row += val; }
        public void decrRow (int val) { _row -= val; }
        public void setRow (int val) { if (val > 0 && val <= Constants.MAX_ROW) { _row = val; } }

        public int myX (int val) {

            if (val < 1) { val = 1; }

            return (((val * 8) - 8) + 4);
        }

        public int myY (int val) {

            return ((val * 8) - 8);
        }

        //void drawBitmap (
        //   HDC DC,
        //   HBITMAP Objn,
        //   const int Col,
        //   const int Row);

        // Replace Beep functions with MessageBeep(xxx) function
        //int Beep;     Need to find an equivalant routine using C
        //int HiBeep;   Need to find an equivalant routine using C

        //KeyBoard input function should already be done with CIN
        //int InKBD (var FunKey: Boolean; var Key: Integer);

        // Graphic Routines, Should Already be done
        //void *GetScrn (int x1, int y1, int x2, int y2);
        //void PutScrn (void *p, int x1, int y1, int x2, int y2);

        // Popup Window Routines
        //void Continue (int x, int y, int mnubar);
        //void ShowErr (char *s);
        //void ShowWinners (DWORD c, DWORD bkc, char* head,
        //                 char *s1, char *s2,
        //                 char *s3, char *s4,
        //                 char *s5, char *s6);

        // Draw Routines
        //void DrawMoney(long cash);
        //void ShowGameOver();
    };

    public static class Piece extends Location {

        Constants.CompanyId __companyId;

        boolean __hidden;
        boolean __highLight;

        int __xoff;
        int __yoff;

        public Piece (int col, int row) {

            super (col, row);

            __companyId = Constants.CompanyId.UNDEF;
            __hidden = false;
            __highLight = false;
            __xoff = 0;
            __yoff = 0;
        }

        public Piece (Location loc) {

            super (loc.getCol (), loc.getRow ());

            __companyId = Constants.CompanyId.UNDEF;
            __hidden = false;
            __highLight = false;
            __xoff = 0;
            __yoff = 0;
        }

        //Class Methods
        public int rangeTo (Piece distTile) {

            return (Math.abs (getCol () - distTile.getCol ()) + Math.abs (getRow () - distTile.getRow ()) - 1);
        }

        public void setCompanyId (Constants.CompanyId val) { __companyId = val; }
        public Constants.CompanyId getCompanyId () { return __companyId; }

        public void setHighLight (boolean val) { __highLight = val; }
        public boolean getHighLight () { return __highLight; }

        public void setHidden (boolean val) { __hidden = val; }
        public boolean getHidden () { return __hidden; }
    }; //Piece

    /* Unused C++ methods
    //Object Methods
    classType isA () const {

       return objectClass;
    }

    String nameOf () const {

       return "aqr::Tile";
    }

    hashValueType hashValue () const {

       return hashValueType ((_impl.loc.col * aqr::MaxColumns) + _impl.loc.row);
    }

    int isEqual (const Object &SourceTile) const {

       int result (FALSE);

       if (((aqr::Tile &) SourceTile)._impl.loc.col == _impl.loc.col &&
           ((aqr::Tile &) SourceTile)._impl.loc.row == _impl.loc.row) {

          result = TRUE;
       }

       return result;
    }

    void printOn (ostream &outputStream) const {

       const BufSize = 20;

       char temp[BufSize];

       ostrstream os (temp, BufSize);

       os << "[" << _impl.loc.col << ", " << _impl.loc.row << "]";

       outputStream << temp;
    }
    */ //Unused

    public static class Queue {

        private Location[] __queue;
        private int __queueTop; //Top of queue

        public Queue () {

            int ix = 0;
            int col = 0;
            int row = 0;
            Location tmp = new Location ();
            Random random = new Random ();

            __queue = new Location[Constants.MAX_TILES];
            __queueTop = Constants.MAX_TILES - 1;         //Top of queue

            //Make the playable tiles
            for (ix = 0; ix < Constants.MAX_TILES; ix++) {

                col = (ix / Constants.MAX_ROW) + 1;
                row = (ix % Constants.MAX_ROW) + 1;

                __queue[ix] = new Location (col, row);
            }

            //Place Tiles in Random order
            for (ix = Constants.MAX_TILES - 1; ix > 0; ix--) { 

                int select = random.nextInt (ix + 1);

                if (select != ix) {

                    tmp.copy (__queue[select]);
                    __queue[select].copy (__queue[ix]);
                    __queue[ix].copy (tmp);
                }   
            }
        }

        public void finalize () {

            for (int ix = 0; ix < Constants.MAX_TILES; ix++) { __queue[ix] = null; }
        }

        public void shutdown () {

            if (__queue != null) { //gameMode != MANUAL or NETWORK

                //All the tiles are pointed to by Tiles array
                //Remove all tiles in the game
                for (int ix = Constants.MAX_TILES - 1; ix >= 0; ix--) {

                    if (__queue[ix] != null) { __queue[ix] = null; }
                }
            }
        }

        public boolean isEmpty () {

            boolean result = false;

            if (__queueTop >= 0) { result = true; }

            return result;
        }

        public Location getNextTile () {

            Location tmp = new Location ();

            if (__queue[0] != null) { //inMode == MANUAL_GAME

                if (__queueTop >= 0) { tmp.copy (__queue[__queueTop--]); }
            }

            return tmp;
        }

        public void returnTile (Location loc) {

            int select = 0;
            int fndTile = Constants.MAX_TILES;
            Random random = new Random ();

            // Find returned Tile in Tiles queue
            for (int ix = __queueTop + 1; ix < Constants.MAX_TILES; ix++) {

                if (loc.isEqual (__queue[ix])) { fndTile = ix; ix = Constants.MAX_TILES; }
            }

            if (fndTile == Constants.MAX_TILES) {

                System.out.println ("Returned Tile not found in Tile queue...");
                return;
            }

            //Swap Last Taken Tile w/ Found Tile
            __queue[fndTile].copy (__queue[__queueTop + 1]);

            //Choose a new tile from available tiles to
            select = random.nextInt (__queueTop + 1);

            //Swap choosen tile with returned tile
            __queue[__queueTop + 1].copy (__queue[select]);
            __queue[select].copy (loc);

            __queueTop++;
        }
    }; //Queue

}; //Tile


