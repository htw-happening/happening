package blue.happening.chat.bluetooth.bluetoothEDR.BtPackage;

import blue.happening.chat.bluetooth.bluetoothEDR.Misc.Constants;

/**
 * Wrap around implementation of a cache.
 * Used for a history check in the handler.
 */
public class BtPackageCache {

    private BtPackage[] elements;
    private int indexPointer;

    public BtPackageCache() {
        this.elements = new BtPackage[Constants.CACHESIZE];
        this.indexPointer = 0;
    }

    /**
     * Is this specific package already contained in the cache.
     * @param o the package
     * @return true, if cache contains the package
     */
    public boolean isInCache (BtPackage o){
        for (int i = 0; i < elements.length; i++) {
            if(elements[i] != null) {
                if (o.equals(elements[i])) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Try to insert a Package into the cache
     * @param o the package to insert
     * @return true, if package was not in cache
     */
    public boolean insertInCache(BtPackage o){
        if(!isInCache(o)){
            //o.setContent(new byte[0]); //Todo - saving memory!?
            elements[indexPointer] = o;
            incrementIndexPointer();
            return true;
        }
        return false;
    }

    /**
     * incrementing index pointer for wrap around array
     */
    private void incrementIndexPointer(){
        if(indexPointer >= Constants.CACHESIZE - 1){
            indexPointer = 0;
        }
        else{
            indexPointer++;
        }
    }
}
