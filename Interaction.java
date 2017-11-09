/*******************************************************************
*
*   Source File: Interaction.java
*   Description: Java Source file for AQR application
*   Date:        Fri Setp 29 2017
*
********************************************************************/

package game.aqr;

import game.aqr.Constants;
import game.aqr.Tile;

public interface Interaction {

    //input Methods
    public Tile.LocationConst selectATile ();
    public void rejectSelectedTile (Constants.TileStatus status);
    public Constants.CompanyId selectACompany (Constants.CompanySet companies);
    public Constants.StockOrder orderStock ();
};
