
/*******************************************************************
*
*   Source File: Company.java
*   Description: Java Source file for AQR application
*   Date:        Fri 29 Sept 2017
*
********************************************************************/

package game.aqr;

import game.aqr.Constants;
import game.aqr.Database;
import game.aqr.Tile;

public class Company {

    private Database __data = null;

    private Tile.Location __fromTileLocation;

    private Tile.Location __toTileLocation;

    public Company (Database data) {

        __data = data;
        __fromTileLocation = new Tile.Location ();
        __toTileLocation = new Tile.Location ();
    }

    private boolean __findNextCompanyTile (Constants.CompanyId company, Tile.Location location) {

        boolean result = false;

        if (__data != null) {

            boolean found = false;

            Tile.Location nextLocation = new Tile.Location (location);

            do {

                if (nextLocation.getRow () >= Constants.MAX_ROW) {

                    nextLocation.incrCol (1);
                    nextLocation.setRow (1);
                }
                else { nextLocation.incrRow (1); }

                Tile.Piece tile = __data.getBoardTile (nextLocation);

                if (tile != null) { if (tile.getCompanyId () == company) { found = true; } }

            } while (!found && nextLocation.getCol () <= Constants.MAX_COL);

            if (found) {

                location.copy (nextLocation);

                result = true;
            }
        }

        return result;
    }

    //Class Methods
    public void getStartSelectionSet (Constants.CompanySet comps) {

        for (Constants.CompanyId id : Constants.CompanyId.values ()) {

            switch (id) {

                case UNDEF:
                case BLK:
                case MAX:
                    //Do not allow selection of these companies
                    break;
                default:    
                    if (__data.getCompanyStatus (id) == Constants.CompanyStatus.Closed) { comps.set (id, true); }
                    break;
            }
        }
    }

    public boolean start (Constants.CompanyId company) {

        boolean result = false;

        if (__data != null) {

            Constants.CompanyStatus status = __data.getCompanyStatus (company);

            if (status == Constants.CompanyStatus.Closed) {

                __data.setCompanyStatus (company, Constants.CompanyStatus.Open);

                int stockCount = __data.getCompanyStockCount (company);

                if (stockCount > 0) { __data.setCompanyStockCount (company, 1, Constants.ModifierType.Sub); }

                result = true;
            }
        }

        return result;
    }

    public Constants.CompanySet getDefunctSelectionSet (Constants.CompanySet comps) {

        Constants.CompanySet result = new Constants.CompanySet ();
        int defCompSize = 0;
        int numDefComps = 0;

        for (Constants.CompanyId id : Constants.CompanyId.values ()) {

            switch (id) {

                case UNDEF:
                case BLK:
                case MAX:
                    //Do not allow selection of these companies
                    break;
                default:    
                    if (comps.get (id)) {

                        int compSize = __data.getCompanySize (id);

                        if (compSize > defCompSize) {

                            result.clear ();
                            result.set (id, true);
                            defCompSize = compSize;
                            numDefComps = 0;
                        }
                        else if (__data.getCompanySize (id) == defCompSize) {

                            result.set (id, true);
                            numDefComps++;
                        }
                    }
                    break;
            }
        }

        return result;
    }


    public boolean defunct (Constants.CompanyId company, Constants.CompanyId gainCompany) {

        boolean result = false;

        if (__data != null) {

            Constants.CompanyStatus status = __data.getCompanyStatus (company);
            Constants.CompanyStatus gainStatus = __data.getCompanyStatus (gainCompany);

//System.out.print ("DEBUG: companyExt.defunct, ");
//System.out.print (status.ordinal () + " | " + gainStatus.ordinal ());
//System.out.println ();

            if (status == Constants.CompanyStatus.Open && gainStatus != Constants.CompanyStatus.Closed) {

                int gainSize = __data.getCompanySize (gainCompany);
                int size = __data.getCompanySize (company);

                //Not necessary, done in board changeTile routine
                //__data.setCompanySize (gainCompany, size + gainSize);

                if (size + gainSize > Constants.MAX_DEFUNCT_SIZE) {

                    __data.setCompanyStatus (gainCompany, Constants.CompanyStatus.Safe);
                }

                __data.setCompanyStatus (company, Constants.CompanyStatus.Closed);
                __data.setCompanySize (company, 0);

//System.out.println ("DEBUG: _defunct::company: " + company.ordinal ());
//System.out.println ("DEBUG: New Defunct Company Size: " + __data.getCompanySize (company));
            }
        }

        return result;
    }

    public int rangeBetween (Constants.CompanyId fromCompany, Constants.CompanyId toCompany) {

        int result = Constants.MAX_RANGE;

        if (__data != null) {

            Constants.CompanyStatus fromStatus = __data.getCompanyStatus (fromCompany);

            Constants.CompanyStatus toStatus = __data.getCompanyStatus (toCompany);

            if (fromStatus != Constants.CompanyStatus.Closed &&
                toStatus != Constants.CompanyStatus.Closed &&
                fromCompany != toCompany) {

                int minRange = Constants.MAX_RANGE;

                __fromTileLocation.copy (1, 1);

                boolean validFromTile = false;

                do {

                    validFromTile = __findNextCompanyTile (fromCompany, __fromTileLocation);

                    if (validFromTile) {

                        Tile.Piece fromTile = __data.getBoardTile (__fromTileLocation);

                        __toTileLocation.copy (1, 1);

                        boolean validToTile = false;

                        do {

                            validToTile = __findNextCompanyTile (toCompany, __toTileLocation);

                            if (validToTile) {

                                Tile.Piece toTile = __data.getBoardTile (__toTileLocation);

                                int range = fromTile.rangeTo (toTile);

                                if (range < minRange) { minRange = range; }
                            }

                        } while (validToTile);
                    }

                } while (validFromTile);

                result = minRange;
            }
        }

        return result;
    }

    public boolean canGameEnd () {

        boolean result = false;
        boolean allSafe = true;
        int openCount = 0;

        if (__data != null) {

            //Determine if a company's size is 41 or over
            for (Constants.CompanyId id : Constants.CompanyId.values ()) {

                switch (id)
                {
                    case UNDEF :
                    case BLK :
                    case MAX :
                        break;
                    default :
                        if (__data.getCompanySize (id) > 40) { result = true; break; }
                        if (__data.getCompanyStatus (id) == Constants.CompanyStatus.Open) { allSafe = false; }
                        if (__data.getCompanyStatus (id) != Constants.CompanyStatus.Closed) { openCount++; }

                        break;
                }
            }

            if (result) {

                System.out.println ("DEBUG: Game Can End, Company 41 and over");
            }
            else if (!result && allSafe && openCount > 0) {

                System.out.println ("DEBUG: Game Can End, All Companies Safe");
                result = true;
            }
        }

        return result;
    }
};
