/*******************************************************************
*
*   Source File: Game.java
*   Description: Interface for all game classes
*   Date:        Mon Sept 28 2017
*
********************************************************************/

package game.aqr;

import game.aqr.Session;

public class Game {

    public static void main (String [] args)
    {
        Session session = new Session ();

        session.setup ();
        session.run ();
        session.end ();
    }
};

