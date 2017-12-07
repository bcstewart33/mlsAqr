
/*******************************************************************
*
*   Source File: Session.java
*   Description: Java Source file for AQR application
*   Date:        Mon Sept 28 2017
*
********************************************************************/

package game.aqr;

import java.lang.Math;
import java.util.*; //HashTable, Stack

import game.aqr.*;
import game.aqr.interact.*;

//#include "gameCompany.h"
//#include "gameBoard.h"
//#include "gamePlayer.h"

//#include "gameDatabase.h"
//#include "gameRuntimeTile.h"

public class Session {

    private Runtime __runtime;
    private DatabaseImpl __data;

    private Company __companyExt;
    private Board __boardExt;
    private Player __playerExt;

    private Integer __currentPlayerId;

    private Constants.TextBlock __text;

    private Map<Integer, Interaction> __interactMap;

    //Impl Methods
    public Session () {

        __runtime = new Runtime ();
        __data = new DatabaseImpl ();
        //Initialize database

        __boardExt = new Board (__data);
        __companyExt = new Company (__data);
        __playerExt = new Player (__runtime, __data, __boardExt);

        //Do once engine is initialized
        __interactMap = new HashMap<Integer, Interaction> ();

        __currentPlayerId = -1;
        __text = null;
    }

    public void finalize () {

        if (__runtime != null) { __runtime = null; }
        if (__data != null) { __data = null; }

        if (__companyExt != null) { __companyExt = null; }
        if (__boardExt != null) { __boardExt = null; }

        if (__playerExt != null) { __playerExt = null; }
        if (__interactMap != null) { __interactMap = null; }
    }

    public void setup () {

        System.out.println ("### Setup Game ###");

        if (__runtime.getGameMode () == Constants.Mode.Open ||
            __runtime.getGameMode () == Constants.Mode.Closed)
        {
            //Tile.Queue tileQueue = new Tile.Queue ();

            //__boardExt.setupTiles (tileQueue);
        }

        __runtime.setNumPlayers (1);
        //Interaction itx = new DummyInputQueue (__runtime, __data, __boardExt);
        Interaction itx = new DummyInputTile (__runtime, __data, __boardExt);
        __interactMap.put (0, itx);
    }

    public void run () {

        System.out.println ("### Start Game ###");

        do {

            __currentPlayerId++; if (__currentPlayerId >= __runtime.getNumPlayers ()) { __currentPlayerId = 0; }

            Interaction itx = __interactMap.get (__currentPlayerId);

            if (itx != null) { _takeATurn (itx); }

        } while (!__runtime.getGameOver ());
    }

    public void end () {

        if (__runtime.getGameMode () == Constants.Mode.Manual ||
            __runtime.getGameMode () == Constants.Mode.Network) {

            __data.clearBoard ();
        }

        System.out.println ("### Game Over!! ###");
    }

    protected void _checkForGameEnd () {

        if (__boardExt.canGameEnd() || __companyExt.canGameEnd ()) {

            System.out.println ("DEBUG: Declare Game Over");
            __runtime.setGameOver (true);
        }
    }

    protected void _takeATurn (Interaction itx) {

        //Play Tile
        boolean tileIsInvalid = false;

        do {

            Tile.LocationConst loc = itx.selectATile ();
            Constants.TileStatus status = __playerExt.playTile (__currentPlayerId, loc);

            Constants.CompanyId company = Constants.CompanyId.UNDEF; //__playerExt.
            Constants.CompanySet comps = new Constants.CompanySet ();

            tileIsInvalid = false;

            switch (status) {

                case Unplayable:
                case Obsolete:
                    System.out.println ("DEBUG: Unplayable/Obsolete");
                    //Send message to interaction to deny the selected tile
                    itx.rejectSelectedTile (status);
                    tileIsInvalid = true;
                    break;
                case DefunctCompany:
                    comps = __playerExt.getTileDefunctSet ();
                    Constants.CompanySet selectComps = __companyExt.getDefunctSelectionSet (comps);

                    System.out.print ("DEBUG: Defunct Companies [");
                    for (Constants.CompanyId id : Constants.CompanyId.values ()) {

                        if (comps.get (id) == true) { System.out.print (id.ordinal () + ", "); }
                    }
                    System.out.println ("]");

                    Constants.CompanyId gainComp = itx.selectACompany (selectComps);
                    System.out.println ("DEBUG: Selected Company " + gainComp.ordinal ());

                    //Defunct all non-selected companies
                    for (Constants.CompanyId id : Constants.CompanyId.values ()) {

                        if (comps.get (id) == true && id != gainComp) {

                            System.out.println ("DEBUG: Defunct Company: " + id.ordinal ());
                            _defunct (id, gainComp);
                        }
                    }

                    __data.playBoardTile (loc, Constants.CompanyId.BLK);
                    __boardExt.changeTiles (gainComp, loc);
                    break;
                case StartCompany:
                    System.out.println ("DEBUG: Start Company ");

                    __companyExt.getStartSelectionSet (comps);

                    company = itx.selectACompany (comps);
                    System.out.println ("DEBUG: Selected Company: " + company.ordinal ());

                    _startCompany (company);
                    __data.playBoardTile (loc, Constants.CompanyId.BLK);
                    __boardExt.changeTiles (company, loc);
                    break;
                case AddCompany:
                    company = __playerExt.getTileAddCompanyId ();
                    System.out.println ("DEBUG: AddCompany " + company.ordinal ());

                    __data.playBoardTile (loc,  Constants.CompanyId.BLK);
                    __boardExt.changeTiles (company, loc);
                    break;
                case Playable:
                default :
                    System.out.println ("DEBUG: Playable");
                    __data.playBoardTile (loc, Constants.CompanyId.BLK);
                    break;
            }
        } while (tileIsInvalid == true);

        _checkForGameEnd ();

        //Buy Stock

        //Update Player Stats
        __playerExt.updateStats (__currentPlayerId);
    }

    protected boolean _startCompany (Constants.CompanyId company) {

        boolean result = false;

        if (__companyExt != null && __data != null) {

            if (__companyExt.start (company)) {

                __data.setPlayerStockCount (__currentPlayerId, company, 1, Constants.ModifierType.Add);
            }
            else { ; } //Note: if company stockcount == 0, need way to capture this
        }

        return result;
    }

    protected boolean _defunct (
        Constants.CompanyId company,
        Constants.CompanyId gainCompany) {

        boolean result = false;

        if (__companyExt != null) {

            if (__companyExt.defunct (company, gainCompany)) {

                //if (__boardExt != null) { __boardExt.redraw (); }
            }
        }

        return result;
    }

    protected void _payWinners (Constants.CompanyId company) {

        final int START = -1;
        final int FIRST = 0;
        final int SECOND = 1;

        if (__data != null) {

            long firstReward = __data.getCompanyPrice (company) * 10;
            long secondReward = firstReward / 2;

            long firstPrize = 0;
            long secondPrize = 0;

            int [] playOrder = new int [Constants.MAX_PLAYERS];
            int tmp;

            int [] numWinners = new int [2]; Arrays.fill (numWinners, 0); //placement counters
            int linenum = 1;
            int ix = 0;

            //Add to the Global GNP
            __runtime.setGNP (firstReward + secondReward);

            //Setup text display array
            __text.clear (); __text.set (0, __data.getCompanyName (company) + " Hotels Defunct");

            //Setup sorting array
            for (ix = 0; ix < __runtime.getNumPlayers (); ix++) {

                playOrder[ix] = ix;
            }

            //Sort the players by number of stocks owned
            for (ix = 0; ix < __runtime.getNumPlayers (); ix++) {

                for( int jx = ix + 1; jx < __runtime.getNumPlayers (); jx++) {

                    if (__data.getPlayerStockCount (playOrder[jx], company) >
                            __data.getPlayerStockCount (playOrder[ix], company)) {

                        //swap the two
                        tmp = playOrder[ix];
                        playOrder[ix] = playOrder[jx];
                        playOrder[jx] = tmp;
                    }      
                }
            }

            int place = START;
            long numShares = 0;

            // Determine number of players in 1st & 2nd Place
            for (ix = 0; ix < __runtime.getNumPlayers (); ix++) {

                //determine number of 1st place winners
                int playerStockCount = __data.getPlayerStockCount (playOrder[ix], company);

                if (playerStockCount != numShares) {

                    numShares = playerStockCount;

                    if (place != SECOND && numShares != 0) {

                        numWinners[++place] = 1;
                    }
                    else { ix = __runtime.getNumPlayers () + 1; }
                }
                else { numWinners[place]++; }
            }

            double val;

            //Payoff the winners & setup the display strings
            if (numWinners[FIRST] > 1 || numWinners[SECOND] == 0 ||
                __runtime.getGamePayMode () == Constants.PayMode.FirstOnly) {

                val = (firstReward + secondReward) % numWinners[FIRST];

                firstPrize = (long) val;
            }
            else {

                firstPrize = firstReward;

                val = secondReward % numWinners[SECOND];

                secondPrize = (long) val;
            }

            for (ix = 0; ix < numWinners[FIRST] + numWinners[SECOND]; ix++) {

                if (ix < numWinners[FIRST]) {

                    __data.setPlayerCash (playOrder[ix], firstPrize, Constants.ModifierType.Add);

                    __data.setPlayerProfit (playOrder[ix], firstPrize, Constants.ModifierType.Add);

                    __text.set (linenum++, " " + __data.getPlayerName (playOrder[ix]) + "   " + firstPrize);
                }
                else {

                    __data.setPlayerCash (playOrder[ix], secondPrize, Constants.ModifierType.Add);

                    __data.setPlayerProfit (playOrder[ix], secondPrize, Constants.ModifierType.Add);

                    __text.set (linenum++, " " + __data.getPlayerName (playOrder[ix]) + "   " + secondPrize);
                }
            }

            __runtime.setTextData (__text);
        }
    }

    protected void _endGamePayOff (Constants.CompanyId company) {

        long payOffCash;
        int linenum = 1;

        if (__data != null) {

            //Setup text display array
            __text.clear ();
            __text.set (0, __data.getCompanyName (company) + " Stock PayOffs");

            //Pay Off each player & add to text display
            for (int ix = 0; ix < __runtime.getNumPlayers (); ix++) {

                if (__data.isPlayerActive (ix)) {

                    payOffCash =
                        __data.getPlayerStockCount (ix, company) * __data.getCompanyPrice (company);

                    __data.setPlayerCash (ix, payOffCash, Constants.ModifierType.Add);

                    __data.setPlayerStockCount (ix, company, 0, Constants.ModifierType.Set);

                    __text.set (linenum++, " " + __data.getPlayerName (ix) + "    " + payOffCash + ".00");
                }
            }

            __runtime.setTextData (__text);
        }
    }
};
