
/*******************************************************************
*
*   Source File: DummyInputQueue.java
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
import game.aqr.interact.Dummy;

public class DummyInputQueue extends Dummy {

    private Tile.Queue __tileQueue = null;

    public DummyInputQueue (Runtime runtime, Database data, Board board) {

        super (runtime, data, board);

        __tileQueue = board.getTileQueue ();
    }

    //Interaction Methods
    public Tile.LocationConst selectATile () {

        _showBoard ();

        if (_buffRead != null) {
            
            try {

                System.out.print ("Press Enter:");
                String ipt = _buffRead.readLine ();
            }
            catch (IOException e) { e.printStackTrace (); }
        }

        _selectedLoc = __tileQueue.getNextTile ();

        System.out.println ("    Tile: " + _selectedLoc.getCol () + "-" + _valToChar (_selectedLoc.getRow ()));

        return new Tile.LocationConst (_selectedLoc.getCol (), _selectedLoc.getRow ());
    }

    public void rejectSelectedTile (Constants.TileStatus status) {

        System.out.println ("    ----Rejected!");

        if (status == Constants.TileStatus.Unplayable) {

            __tileQueue.returnTile (_selectedLoc);
        }
    }

    public Constants.CompanyId selectACompany (Constants.CompanySet companies) {

        Constants.CompanyId result = Constants.CompanyId.UNDEF;

        for (Constants.CompanyId id : Constants.CompanyId.values ()) {

            if (companies.get(id) == true) { result = id; break; }
        }

        return result;
    }
};

