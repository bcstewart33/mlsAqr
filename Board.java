
/*******************************************************************
*
*   Source File: Board.java
*   Description: Java Source file for AQR application
*   Date:        Fri Sept 29 2017
*
********************************************************************/

package game.aqr;

import java.util.*; //HashTable, Stack
import java.util.concurrent.atomic.AtomicReference;

import game.aqr.Constants;
import game.aqr.Database;
import game.aqr.Tile;

public class Board {

    enum Direction {
        LFT,
        RHT,
        TOP,
        BOT;
    };

    class UndoElement {

        private Hashtable<Integer, Integer> table = null;

        public UndoElement () {

            table = new Hashtable<Integer, Integer> ();
        }
    };

    Database __data = null;

    Tile.Queue __tileQueue = null;
    Stack __undoStack = null;

    Tile.Location[] __adj = new Tile.Location[4];      //Adjacency array
    Tile.Location[] __nextadj = new Tile.Location[3];  //Next to the adjacency points array


    public Board (Database data)
    {
        __data = data;
        __undoStack = new Stack<Tile.Location> ();

        for (int ix = 0; ix < 4; ix++) { __adj[ix] = new Tile.Location (); }
        for (int jx = 0; jx < 3; jx++) { __nextadj[jx] = new Tile.Location (); }
    }

    //Private Methods
    private boolean __isTileUndefined (Tile.Piece tile)
    {
        boolean result = true;

        if (tile != null) { if (tile.getCompanyId () != Constants.CompanyId.UNDEF) { result = false; } }

        return result;
    }

    private boolean __isTileAHotel (Tile.LocationConst location) {

        //Determine if the given tile is a hotel

        boolean result = false;

        if (__data != null) {

            Tile.Piece tile = __data.getBoardTile (location);

            if (tile != null && tile.getCompanyId () != Constants.CompanyId.BLK) { result = true; }
        }

        return result;
    }
    // This routine determines if the given point will start a
    // company on the board.
    private boolean __willTileStartCompany (Tile.LocationConst location) {

        boolean result = true;

        for (int ix = 0; ix < 4; ix++) { __adj[ix].clear (); }
        for (int jx = 0; jx < 3; jx++) { __nextadj[jx].clear (); }

        int col = location.getCol ();
        int row = location.getRow ();

        //Get all adjacent points
        if (col == 1) { ; }
        else {

            __adj[Direction.LFT.ordinal ()].setCol (col - 1);
            __adj[Direction.LFT.ordinal ()].setRow (location.getRow ());
        }

        if (col == Constants.MAX_COL) { ; }
        else {

            __adj[Direction.RHT.ordinal ()].setCol (col + 1);
            __adj[Direction.RHT.ordinal ()].setRow (location.getRow ());
        }

        if (row == 1) { ; }
        else {

            __adj[Direction.TOP.ordinal ()].setCol (location.getCol ());
            __adj[Direction.TOP.ordinal ()].setRow (row - 1);
        }

        if (row == Constants.MAX_ROW) { ; }
        else {

            __adj[Direction.BOT.ordinal ()].setCol (location.getCol ());
            __adj[Direction.BOT.ordinal ()].setRow (row + 1);
        }

//System.out.print ("DEBUG: board.willTile [" + col + ", " + row + "] -> adjacent[");
//for (int ix = 0; ix < 4; ix++) {        
//    System.out.print ("(" + __adj[ix].getCol () + ", " + __adj[ix].getRow () + ")");
//}
//System.out.println ("]");

        // If the given tile is company then exit & return FALSE
        if (__isTileAHotel (location)) {

            result = false;
        }
        else {
            // If any of the adjacent cells is in a company then exit and
            // return FALSE
            for (Direction ix : Direction.values ()) {

                if (__isTileAHotel (__adj[ix.ordinal ()])) {

                    result = false; break;
                }
            }

            if (result) {

                result = false;

                // Continue Checking now that you know all points are not
                // part of a company
                if (!result && __adj[Direction.LFT.ordinal ()].getCol () != 0) {

                    __nextadj[0].copy (__adj[Direction.LFT.ordinal ()]); __nextadj[0].decrCol (1);
                    __nextadj[1].copy (__adj[Direction.LFT.ordinal ()]); __nextadj[1].decrRow (1);
                    __nextadj[2].copy (__adj[Direction.LFT.ordinal ()]); __nextadj[2].incrRow (1);

                    if (!__isTileAHotel (__nextadj[0]) &&
                        !__isTileAHotel (__nextadj[1]) &&
                        !__isTileAHotel (__nextadj[2])) { result = true; }
                }

                if (!result && __adj[Direction.RHT.ordinal ()].getCol () != 0) {

                    __nextadj[0].copy (__adj[Direction.RHT.ordinal ()]); __nextadj[0].incrCol (1);
                    __nextadj[1].copy (__adj[Direction.RHT.ordinal ()]); __nextadj[1].decrRow (1);
                    __nextadj[2].copy (__adj[Direction.RHT.ordinal ()]); __nextadj[2].incrRow (1);

                    if (!__isTileAHotel (__nextadj[0]) &&
                        !__isTileAHotel (__nextadj[1]) &&
                        !__isTileAHotel (__nextadj[2])) { result = true; }
                }

                if (!result && __adj[Direction.TOP.ordinal ()].getCol () != 0) {

                    __nextadj[0].copy (__adj[Direction.TOP.ordinal ()]); __nextadj[0].decrCol (1);
                    __nextadj[1].copy (__adj[Direction.TOP.ordinal ()]); __nextadj[1].incrCol (1);
                    __nextadj[2].copy (__adj[Direction.TOP.ordinal ()]); __nextadj[2].decrRow (1);

                    if (!__isTileAHotel (__nextadj[0]) &&
                        !__isTileAHotel (__nextadj[1]) &&
                        !__isTileAHotel (__nextadj[2])) { result = true; }
                }

                if (!result && __adj[Direction.BOT.ordinal ()].getCol () != 0) {

                    __nextadj[0].copy (__adj[Direction.BOT.ordinal ()]); __nextadj[0].decrCol (1);
                    __nextadj[1].copy (__adj[Direction.BOT.ordinal ()]); __nextadj[1].incrCol (1);
                    __nextadj[2].copy (__adj[Direction.BOT.ordinal ()]); __nextadj[2].incrRow (1);

                    if (!__isTileAHotel (__nextadj[0]) &&
                        !__isTileAHotel (__nextadj[1]) &&
                        !__isTileAHotel (__nextadj[2])) { result = true; }
                }
            }
        }

//System.out.println ("DEBUG:      .willTile Result: " + (result ? "T" : "F"));
        return result;
    }

    private void __storeUndoTile (Tile.Piece tile) {

        //Not sure what to do here yet??
        //UndoElement *elm = new UndoElement ();

        //elm->table.add (*tile);
    }

    private void __drawTile (Tile.Piece tile) {

        //if (window) {

        //    window->DrawTile (NULL, tile);
        //}
    }

    // Class Methods
    /* Why do this here, should be in main window class
    public void setTileDlg (PTWindowsObject PT) {

       if (_impl.window && PT) {

          _impl.window->SetTileDlg (PT);
       }
    }
    */

    public void initializeTileQueue () {

        if (__tileQueue == null) { __tileQueue = new Tile.Queue (); }
    }

    public Tile.Queue getTileQueue () { return __tileQueue; }

    public void changeTiles (Constants.CompanyId company, Tile.LocationConst location) {

        if (__data != null) {

//System.out.println ("DEBUG: changeTile: " + location.getCol () + "-" + location.getRow ());

            Tile.Piece tile = __data.getBoardTile (location);

            if (tile != null && tile.getCompanyId () != Constants.CompanyId.UNDEF && tile.getCompanyId () != company) {

//System.out.println ("DEBUG:             YES");

                Tile.Location loc = new Tile.Location ();

                tile.setCompanyId (company);

                //Note: need to do this with a message or callback
                //_impl.drawTile (tile);

                int companySize = __data.getCompanySize (company);

                __data.setCompanySize (company, ++companySize);

                if (companySize > Constants.MAX_DEFUNCT_SIZE) {

                    __data.setCompanyStatus (company, Constants.CompanyStatus.Safe);
                }

                if (location.getCol () != 1) { //Update tile to left

//System.out.println ("DEBUG:             -> LFT");
                    loc.copy (location.getCol () - 1, location.getRow ());

                    changeTiles (company, loc);
                }

                if (location.getCol () != Constants.MAX_COL) { //Update tile to right

//System.out.println ("DEBUG:             -> RHT");
                    loc.copy (location.getCol () + 1, location.getRow ());

                    changeTiles (company, loc);
                }

                if (location.getRow () != 1) { //Update tile above

//System.out.println ("DEBUG:             -> TOP");
                    loc.copy (location.getCol (), location.getRow () - 1);

                    changeTiles (company, loc);
                }

                if (location.getRow () != Constants.MAX_ROW) { //Update tile below

//System.out.println ("DEBUG:             -> BOT");
                    loc.copy (location.getCol (), location.getRow () + 1);

                    changeTiles (company, loc);
                }
            }
        }
    }


    /* - This is incomplete, implement it in a diff way without tileQueue
    public void PlaceTile (Tile.Piece *tile) {

       if (tile) {

          if (game.aqr.runtime::gameMode () = Constants.Mode.Manual ||
              game.aqr.runtime::gameMode () = Constants.Mode.Network) {

             int fndTile (Constants.MAX_TILES);

             // Unhilight the last played tile
             if (LastTile >= 0) {

                _impl.tileQueue[LastTile]->setHighLight (false);

                _impl.drawTile (_impl.tileQueue[LastTile]);
             }

             // Find the given tile in the tile queue
             for (int ix = _impl.queueTop + 1; ix < Constants.MAX_TILES; ix++) {

                if (tileQueue[ix] == tile) {

                   fndTile = ix;
                   ix = Constants.MAX_TILES;
                }
             }

             if (fndTile == Constants.MAX_TILES) {

                cerr << "Placed Tile not found in Tile queue..." << endl;
                exit (-1);
             }

             // Update last tile pointer
             LastTile = fndTile;
          }
        
          // Place the given tile on the board
          tile->setHighLight (true);

          tile->setHidden (false);

          int col;
          int row;

          tile->getValue (col, row);

          _impl.board[col][row] = tile;

          _impl.drawTile (tile);
       }
    }
    */

    public Constants.TileStatus checkTile (
        Tile.LocationConst location,
        AtomicReference<Constants.CompanyId> company, //Pass by Ref Here
        Constants.CompanySet defunctCompanies) { //Pass by Ref Here

        Constants.TileStatus status = Constants.TileStatus.Unplayable;
        Tile.Piece[] adj = new Tile.Piece[4];

        if (__data != null) {

            // Determine if a place on board exits for tile
            if (__isTileUndefined (__data.getBoardTile (location))) {

                Tile.Location newTile = new Tile.Location ();

                //Get pointers to adjacent tiles on board
                if (location.getCol () > 1) {

                    newTile.copy (location.getCol () - 1, location.getRow ());

                    adj[Direction.LFT.ordinal ()] = __data.getBoardTile (newTile);
                }
                else { adj[Direction.LFT.ordinal ()] = null; }

                if (location.getCol () < Constants.MAX_COL) {

                    newTile.copy (location.getCol () + 1, location.getRow ());

                    adj[Direction.RHT.ordinal ()] = __data.getBoardTile (newTile);
                }
                else { adj[Direction.RHT.ordinal ()] = null; }

                if (location.getRow () > 1) {

                    newTile.copy (location.getCol (), location.getRow () - 1);

                    adj[Direction.TOP.ordinal ()] = __data.getBoardTile (newTile);
                }
                else { adj[Direction.TOP.ordinal ()] = null; }

                if (location.getRow () < Constants.MAX_ROW) {

                    newTile.copy (location.getCol (), location.getRow () + 1);

                    adj[Direction.BOT.ordinal ()] = __data.getBoardTile (newTile);
                }
                else { adj[Direction.BOT.ordinal ()] = null; }

                //Tile is playable
                //(a) if all adjacent board locations are empty
                if (__isTileUndefined (adj[Direction.LFT.ordinal ()]) &&
                    __isTileUndefined (adj[Direction.RHT.ordinal ()]) &&
                    __isTileUndefined (adj[Direction.TOP.ordinal ()]) &&
                    __isTileUndefined (adj[Direction.BOT.ordinal ()])) {

                    status = Constants.TileStatus.Playable;
                }
                else {

                    //Check each adjacent tile
                    for (Direction ix : Direction.values ()) {

                        if (__isTileUndefined (adj[ix.ordinal ()]) == false) {

                            Constants.CompanyId adjCompany = adj[ix.ordinal ()].getCompanyId ();

System.out.println ("DBG: adjComp: " + adjCompany.ordinal ());
System.out.println ("DBG: company " + company.get ().ordinal ());

                            if (adjCompany != company.get ()) {

                                if (company.get () == Constants.CompanyId.BLK) {

                                    company.set (adjCompany);
                                }
                                //(b) if new tile merges two safe comps
                                else if (__data.getCompanyStatus (company.get ()) == Constants.CompanyStatus.Safe &&
                                         __data.getCompanyStatus (adjCompany) == Constants.CompanyStatus.Safe) {

                                    status = Constants.TileStatus.Obsolete;

                                    //NOTE: do this in calling routine, not here
                                    // tile = getATile ();
                                }
                                //(c) if new tile defuncts a company
                                else if (adjCompany != Constants.CompanyId.BLK) {

                                    status = Constants.TileStatus.DefunctCompany;
                                }
                            }
                            //Add adjacent hotel name to list of defunc hotel names set
                            if (defunctCompanies != null && adjCompany != Constants.CompanyId.BLK) {

                                defunctCompanies.set (adjCompany, true);
                            }
                        } // if adj[ix.ordinal ()]
                    } // for Direction.LFT.ordinal () to Direction.BOT.ordinal ()

                    if (status == Constants.TileStatus.Unplayable) {

                        if (company.get () != null && company.get () == Constants.CompanyId.BLK) {

                            //(d) if new tile starts a company
                            for (Constants.CompanyId id : Constants.CompanyId.values ()) {

                                switch (id)
                                {
                                    case UNDEF:
                                    case BLK:
                                    case MAX:
                                        break;
                                    default:    
                                        if (__data.getCompanyStatus (id) == Constants.CompanyStatus.Closed) {

                                            status = Constants.TileStatus.StartCompany; break;
                                        }
                                        break;
                                }
                            }
                        }
                        else {

                            //(e) if new tile adds to an existing hotel chain
                            status = Constants.TileStatus.AddCompany;
                        }
                    }// if !status 
                }// tile is unplayable
            }//if place on board exists
        }

        return status;
    }

    public boolean canGameEnd () {

        boolean result = false;

        if (__tileQueue != null && __tileQueue.isEmpty ()) {

            System.out.println ("DEBUG: Tile Queue is empty");
            result = true;
        }
        else {

            Tile.Location loc = new Tile.Location ();

            //Determine if a company can be started
            for (int col = 1; col <= Constants.MAX_COL; col++) {

                for (int row = 1; row <= Constants.MAX_ROW; row++) {

                    loc.copy (col, row);

                    if (__willTileStartCompany (loc)) {

                        System.out.println ("DEBUG: Game Can End, no place to start new company");
                        result = true;

                        col = Constants.MAX_COL + 1;
                        row = Constants.MAX_ROW + 1;
                    }
                }
            }
        }   

        return result;
    }
};
