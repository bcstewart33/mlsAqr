
/*******************************************************************
*
*   Source File: Dummy.java
*   Description: Java Source file for AQR application
*   Date:        Fri Setp 29 2017
*
********************************************************************/

package game.aqr.interact;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicReference;

import game.aqr.Constants;
import game.aqr.Runtime;
import game.aqr.Tile;
import game.aqr.Database;
import game.aqr.Board;
import game.aqr.Company;
import game.aqr.Interaction;

public class Dummy implements Interaction {

    private static final char[] CHARS = {'-', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
    private static final char[] COMP = {'.', 'C', 'I', 'A', 'F', 'W', 'L', 'T', 'X', '?'};

    private BufferedReader __buffRead;
    private Runtime __runtime;
    private Database __data;
    private Board __board;

    private Tile.Queue __tileQueue;
    private Tile.Location __selectedLoc;

    public Dummy (Runtime runtime, Database data, Board board) {

        __buffRead = new BufferedReader (new InputStreamReader(System.in));

        __runtime = runtime;
        __data = data;
        __board = board;

        __Instantiate ();
    }

    public void finalize () {

        try { if (__buffRead != null) { __buffRead.close (); } }
        catch (IOException e) { e.printStackTrace (); }
    }

    private void __Instantiate () {

        __tileQueue = new Tile.Queue ();

        __selectedLoc = new Tile.Location ();
    }

    private char __valToChar (int val) {

        if (val < 0 || val > 9) { val = 0; }

        return CHARS[val];
    }

    private void __showBoard () {

        //Display Board Tiles
        for (int row = 1; row <= Constants.MAX_ROW; row++) {

            for (int col = 1; col <= Constants.MAX_COL; col++) {

                Tile.LocationConst loc = new Tile.LocationConst (col, row);

                Tile.Piece tile = __data.getBoardTile (loc);

                if (tile != null) {

                    String prefix = " ";
                    String postfix = " ";
                    if (tile.getHighLight ()) { prefix = "["; postfix = "]"; }

                    System.out.print (prefix + COMP[(tile.getCompanyId ()).ordinal ()] + postfix);
                }
            }

            System.out.println ();
        }

        System.out.println ();

        //Display Company Size
        System.out.print ("| ");
        for (Constants.CompanyId id : Constants.CompanyId.values ()) {

            switch (id) {

                case UNDEF:
                case BLK:
                case MAX:
                    //Do not allow selection of these companies
                    break;
                default:    
                    int size = __data.getCompanySize (id);

                    System.out.print (COMP[id.ordinal ()] + "-" + size + " | ");
                    break;
            }
        }

        System.out.println ();

        System.out.println ();
    }

    //Interaction Methods
    public Tile.LocationConst selectATile () {

        __showBoard ();
/*
        if (__buffRead != null) {
            
            try {

                System.out.print ("Press Enter:");
                String ipt = __buffRead.readLine ();
            }
            catch (IOException e) { e.printStackTrace (); }
        }
*/
        __selectedLoc = __tileQueue.getNextTile ();

        System.out.println ("    Tile: " + __selectedLoc.getCol () + "-" + __valToChar (__selectedLoc.getRow ()));

        return new Tile.LocationConst (__selectedLoc.getCol (), __selectedLoc.getRow ());
    }

    public void rejectSelectedTile (Constants.TileStatus status) {

        System.out.println ("    ----Rejected!");

        if (status == Constants.TileStatus.Unplayable) {

            __tileQueue.returnTile (__selectedLoc);
        }
    }

    public Constants.CompanyId selectACompany (Constants.CompanySet companies) {

        Constants.CompanyId result = Constants.CompanyId.UNDEF;

        for (Constants.CompanyId id : Constants.CompanyId.values ()) {

            if (companies.get(id) == true) { result = id; break; }
        }

        return result;
    }

    public Constants.StockOrder orderStock () {

        Constants.StockOrder result = new Constants.StockOrder ();

        return result;
    }
};

