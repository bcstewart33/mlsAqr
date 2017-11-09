/*******************************************************************
*
*   Source File: Runtime.java
*   Description: Jave Source file for AQR application
*   Date:        Thu Sept 29 2017
*
********************************************************************/

package game.aqr;

import game.aqr.Constants;

// Runtime Methods
public class Runtime {

    private Constants.Type __gameType;
    private Constants.Mode __gameMode;
    private Constants.PayMode __gamePayMode;
    private int __numPlayers;
    private long __gnp;
    private boolean __endGame;
    private boolean __gameOver;
    private int __buttons;

    private Constants.TextBlock __textData;

    public Runtime ()
    {
        __gameType = Constants.Type.Champion;
        __gameMode = Constants.Mode.Manual;
        __gamePayMode = Constants.PayMode.FirstOnly;
        __numPlayers = 4;
        __gnp = 2400;
        __endGame = false;
        __gameOver = false;
        __buttons = Constants.MAX_TILES;
        __textData = new Constants.TextBlock();
    }

    void setGameType (Constants.Type type) { __gameType = type; }

    Constants.Type getGameType () { return __gameType; }

    void setGameMode (Constants.Mode mode) { __gameMode = mode; }

    Constants.Mode getGameMode () { return __gameMode; }

    void setGamePayMode (Constants.PayMode payMode) { __gamePayMode = payMode; }

    Constants.PayMode getGamePayMode () { return __gamePayMode; }

    void setNumPlayers (int numPlayers) { __numPlayers = numPlayers; }

    int getNumPlayers () { return __numPlayers; }

    void setTextData (Constants.TextBlock data) {

        for (int ix = 0; ix <= Constants.MAX_PLAYERS; ix++) {

            __textData.set (ix, data.get (ix));
        }
    }

    void getTextData (Constants.TextBlock data) {

        for (int ix = 0; ix <= Constants.MAX_PLAYERS; ix++) {

            data.set (ix, __textData.get (ix));
        }
    }

    void setGNP (long value) { __gnp = value; }

    long getGNP () { return __gnp; }

    void setEndGame (boolean value) { __endGame = value; }

    boolean getEndGame () { return __endGame; }

    void setGameOver (boolean value) { __gameOver = value; }

    boolean getGameOver () { return __gameOver; }

    void setButtons (int value) { __buttons = value; }

    int getButtons () { return __buttons; }
};
