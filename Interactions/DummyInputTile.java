
/*******************************************************************
*
*   Source File: DummyInputTile.java
*   Description: Java Source file for AQR application
*   Date:        Fri Dec 07 2017
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

public class DummyInputTile extends Dummy {

    public DummyInputTile (Runtime runtime, Database data, Board board) {

        super ();

        _buffRead = new BufferedReader (new InputStreamReader(System.in));

        _runtime = runtime;
        _data = data;
        _board = board;

        __selectedLoc = new Tile.Location ();
    }

    private boolean __isValidInput (String ipt)
    {
        boolean result = false;

        if (ipt.length () >= 3) {

            String[] section = ipt.split ("-", -1);

//for (int ix = 0; ix < section.length; ix++) {            
//System.out.println ("DBG: section[" + ix + "] = " + section[ix]);
//}
            if (section.length >= 2) {

                boolean isNumber = section[0].matches("[0-9]+");
                boolean isCharacter = section[1].matches("[abcdefghiABCDEFGHI]");
//if (isNumber) {            
//System.out.println ("DBG: IsNumber Match!");
//}
//if (isCharacter) {
//System.out.println ("DBG: IsCharacter Match!");
//}
                if (isNumber && isCharacter) {

                    Integer col = Integer.parseInt (section[0]);

                    if ((col > 0 && col <= 12)) {

                        result = true;

                        String rowStr = section[1].toUpperCase ();
                        Integer CHAR_REF = (int) 'A';
                        Integer row = (int) rowStr.charAt(0);
//System.out.printf ("DBG: [%d:%d]\n", col, row-CHAR_REF+1);

                        __selectedLoc.setCol (col);
                        __selectedLoc.setRow (row-CHAR_REF+1);
                    }
                }
            }
        }

        return result;
    }


    //Interaction Methods
    public Tile.LocationConst selectATile () {

        _showBoard ();

        if (_buffRead != null) {

            boolean found = false;

            do {

                try {

                    System.out.print ("Press Enter:");
                    String iptStr = _buffRead.readLine ();

                    if (__isValidInput (iptStr)) { found = true; }
                    else { System.out.println ("ERROR Invalid Tile input: <1..12>-<A..I> only"); }
                }
                catch (IOException e) { e.printStackTrace (); }

            } while (!found);
        }

        System.out.println ("    Tile: " + __selectedLoc.getCol () + "-" + _valToChar (__selectedLoc.getRow ()));

        return new Tile.LocationConst (__selectedLoc.getCol (), __selectedLoc.getRow ());
    }

    public void rejectSelectedTile (Constants.TileStatus status) {

        System.out.println ("    ----Rejected!");
    }

    public Constants.CompanyId selectACompany (Constants.CompanySet companies) {

        Constants.CompanyId result = Constants.CompanyId.UNDEF;

        for (Constants.CompanyId id : Constants.CompanyId.values ()) {

            if (companies.get(id) == true) { result = id; break; }
        }

        return result;
    }
};

