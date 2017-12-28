/*******************************************************************
*
*   Source File: Dummy.java
*   Description: Interface for all game classes
*   Date:        Mon Sept 28 2017
*
********************************************************************/

package game.aqr;

import game.aqr.Constants;
import game.aqr.Session;

public class Dummy {

    public static void main (String [] args)
    {
        Session session = new Session ();

        Constants.Mode mode = Constants.Mode.Open;
        Integer numPlayers = 1;

        if (args.length >= 1) {

            switch (args[0]) {
                case "Manual" :
                    mode = Constants.Mode.Manual;
                    break;
                default:
                    break;
            }
        }

        if (args.length >= 2) {

            if (args[1].matches("[0-9]+")) { //IsNumber

                Integer np = Integer.parseInt (args[1]);

                if ((np > 0 && np <= Constants.MAX_PLAYERS)) { numPlayers = np; }
            }
        }

        session.setup (mode, numPlayers);
        session.run ();
        session.end ();
    }
};

