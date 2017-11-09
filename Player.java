/*******************************************************************
*
*   Source File: Player.java
*   Description: Java Source file for AQR application
*   Date:        Fri Setp 29 2017
*
********************************************************************/

package game.aqr;

import java.lang.Object; //Pair
import javafx.util.Pair;
import java.util.*; //HashTable, Stack
import java.util.concurrent.atomic.AtomicReference;

import game.aqr.Constants;
import game.aqr.Tile;
import game.aqr.Database;
import game.aqr.Board;
import game.aqr.Company;

// Global Variables
//extern   GameBoard *board;
//extern   Company   *_company[MAXCOMPS];
//extern   Player    *players[MAXPLAYERS];
//extern   int        currentPlayer;

public class Player {

    private Runtime __runtime;
    private Database __data;
    private Board __board;

    private AtomicReference<Constants.CompanyId> __compRef;
    private Constants.CompanySet __defunctSet;

    protected Tile.Location[] _tiles;

    public Player (Runtime runtime, Database data, Board board) {

        __runtime = runtime;
        __data = data;
        __board = board;

        __compRef = new AtomicReference<Constants.CompanyId> (Constants.CompanyId.BLK);
        __defunctSet = new Constants.CompanySet ();


        _tiles = new Tile.Location[Constants.MAX_NUM_TILES];

        setup ();
    }

    // Class Methods
/* Pending move to an interaction class
    protected boolean _checkTiles () {

        boolean result = false;

        Constants.TileStatus status = Constants.TileStatus.Unplayable;
        Constants.CompanyId company = Constants.CompanyId.UNDEF;
        Constants.CompanySet defunctCompanies = new Constants.CompanySet ();

        defunctCompanies.clear ();

        for (int ix = 0; ix < Constants.MAX_NUM_TILES; ix++) {

            if (_tiles[ix] != null && __board != null) {

                status = __board.checkTile (_tiles[ix], company, defunctCompanies);

                if (status == Constants.TileStatus.Obsolete) {

                    //Check out newly acquired tile
                    //_tiles[ix].copy (__board.getATile ()); __tileQueue.getNextTile ();

                    ix--;
                }
                else if (status != Constants.TileStatus.Unplayable) { result = true; }
            }
        }

        return result;
    }
*/// Pending

    protected boolean _canBuy (int playerId) {

        boolean result = false;

        long price = 0;
        long minPrice = 1500;

        for (Constants.CompanyId id : Constants.CompanyId.values ()) {

            if (__data.isCompanyStockAvailable (id)) {

                price = __data.getCompanyPrice (id);

                if (price > 0 && price < minPrice) { minPrice = price; }

                result = true;
            }
        }

        if (__data.getPlayerCash (playerId) >= minPrice) { result = true; }

        return result;
    }

    public void setup () {

        if (__runtime.getGameMode () != Constants.Mode.Manual) {

            for (int ix = 0; ix < Constants.MAX_NUM_TILES; ix++) {

                //_tiles[ix].copy (__board.getATile ()); __tileQueue.getNextTile ();
            }
        }
        else {

            //_tiles[0].copy (__board.getATile ()); __tileQueue.getNextTile ();
        }
    }


    public Constants.TileStatus playTile (Integer playerId, Tile.LocationConst loc) {

        Constants.TileStatus status = Constants.TileStatus.Obsolete;

        __compRef.set (Constants.CompanyId.BLK);
        __defunctSet.clear ();

        status = __board.checkTile (loc, __compRef, __defunctSet);

        System.out.println ("DEBUG: checkTile status: " + status.ordinal ());

        return status;
    }

    public Constants.CompanyId getTileAddCompanyId () { return __compRef.get(); }

    public Constants.CompanySet getTileDefunctSet () { return __defunctSet; }

    public boolean isValidOrder (Integer playerId, Constants.StockOrder order) {

        boolean result = true;

        long totalCost = 0;

        for (Constants.CompanyId id : Constants.CompanyId.values ()) {

            if (order.get (id) > 0) {

                totalCost += __data.getCompanyPrice (id) * order.get (id);

                if (__data.getCompanyStatus (id) == Constants.CompanyStatus.Closed ||
                    __data.getCompanyStockCount (id) - order.get (id) < 0) {

                    result = false;

                    break; //Break the loop
                }
            }   
        }

        if (totalCost > __data.getPlayerCash (playerId)) { result = false; }

        return result;
    }

    public void fillOrder (Integer playerId, Constants.StockOrder order) {

        for (Constants.CompanyId id : Constants.CompanyId.values ()) {

            if (order.get (id) > 0) {

                __data.setCompanyStockCount (id, order.get (id), Constants.ModifierType.Sub);

                __data.setPlayerCash (
                    playerId, (__data.getCompanyPrice (id) * order.get (id)), Constants.ModifierType.Sub);

                __data.setPlayerStockCount (playerId, id, order.get (id), Constants.ModifierType.Add);

                order.set (id, 0);
            }
        }
    }

    public void defunctStock (
        Integer playerId,
        Pair<Constants.CompanyId, Integer> defunctPair,
        Pair<Constants.CompanyId, Integer> gainingPair) {

        int tradeOrder = ((2 * gainingPair.getValue ()) + defunctPair.getValue ());

        if (__data.getPlayerStockCount (playerId, defunctPair.getKey ()) >= tradeOrder &&
            __data.getCompanyStockCount (gainingPair.getKey ()) >= gainingPair.getValue ()) {

            __data.setPlayerStockCount (
                playerId, gainingPair.getKey (), gainingPair.getValue (), Constants.ModifierType.Add);

            __data.setCompanyStockCount (gainingPair.getKey (), gainingPair.getValue (), Constants.ModifierType.Sub);

            __data.setPlayerCash (
                playerId, __data.getCompanyPrice (defunctPair.getKey ()) * defunctPair.getValue (), Constants.ModifierType.Add);

            __data.setPlayerStockCount (playerId, defunctPair.getKey (), tradeOrder, Constants.ModifierType.Sub);

            __data.setCompanyStockCount (defunctPair.getKey (), tradeOrder, Constants.ModifierType.Add);
        }
    }

    public void updateStats (Integer playerId) {

        if (__runtime.getGameOver ()) {

            // Update Final Graph Statictics
            __data.updatePlayerIncomeStats (playerId, -1);
        }
        else {

            // Update Graph Statictics
            long subTot = __data.getPlayerCash (playerId);

            for (Constants.CompanyId id : Constants.CompanyId.values ()) {

                subTot += __data.getCompanyPrice (id) * __data.getPlayerStockCount (playerId, id);
            }

            __data.updatePlayerIncomeStats (playerId, subTot);
        }

        __data.updatePlayerGNPStats (playerId, __runtime.getGNP ());
    }
};

//public void showStock (COMPS cmp);
//public void show ();
//public void showHideStock (COMPS cmp, int onoff);

/*
void Show()
{
}

void ShowStock(COMPS cmp)
{
}

void ShowHideStock(COMPS cmp, int onoff)
{
}

void BuyStock()
{
   if(CanBuy()){
      do{
         //WINDOW: Get Stock Order
      } while(!OkOrder());
   }
   else {
      //Sound a beep
      //POPUPWINDOW: Button to <continue>
   }
   UpdateGraph();
}


void PlayTile()
{
   int ix=0;
   TILESTATUS result=UNPLAYABLE;

   do {
      if(inMode==MANUAL_GAME){
	 //WINDOW: Input Piece Played
         //Tiles[ix]=new TILE();
      }
      else if(ChkTiles()){
	 //WINDOW: ix=Select Tile to Play(Tiles)
	 GetApplication().ExecDialog(new TPlayTileDlg(this, DLG_PLAYTILE, currentPlayer));
      }
      result=board.PlayTile(tiles[ix]);
   } while(result == UNPLAYABLE || result == OBSOLETE);
}

COMPS SelectStrtComp(SETCOMPS cps)
{
   //POPUPWINDOW: return Selected company from cps
}

COMPS SelectDefComp(SETCOMPS df)
{
   //POPUPWINDOW: return Selected company from df
}

*/
