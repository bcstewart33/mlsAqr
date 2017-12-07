
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

public abstract class Dummy implements Interaction {

    private static final char[] CHARS = {'-', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
    private static final char[] COMP = {'.', 'C', 'I', 'A', 'F', 'W', 'L', 'T', 'X', '?'};

    protected BufferedReader _buffRead;
    protected Runtime _runtime;
    protected Database _data;
    protected Board _board;

    protected Tile.Location _selectedLoc;

    protected Dummy (Runtime runtime, Database data, Board board) {

        _buffRead = new BufferedReader (new InputStreamReader(System.in));

        _runtime = runtime;
        _data = data;
        _board = board;

        _selectedLoc = new Tile.Location ();
    }

    public void finalize () {

        try { if (_buffRead != null) { _buffRead.close (); } }
        catch (IOException e) { e.printStackTrace (); }
    }

    protected char _valToChar (int val) {

        if (val < 0 || val > 9) { val = 0; }

        return CHARS[val];
    }

    protected void _showBoard () {

        //Display Board Tiles
        for (int row = 1; row <= Constants.MAX_ROW; row++) {

            for (int col = 1; col <= Constants.MAX_COL; col++) {

                Tile.LocationConst loc = new Tile.LocationConst (col, row);

                Tile.Piece tile = _data.getBoardTile (loc);

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
                    int size = _data.getCompanySize (id);

                    System.out.print (COMP[id.ordinal ()] + "-" + size + " | ");
                    break;
            }
        }

        System.out.println ();

        System.out.println ();
    }

    //Interaction Methods
    public Constants.StockOrder orderStock () {

        Constants.StockOrder result = new Constants.StockOrder ();

        return result;
    }
};

