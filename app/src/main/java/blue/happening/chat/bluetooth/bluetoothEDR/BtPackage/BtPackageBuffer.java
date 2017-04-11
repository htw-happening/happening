package blue.happening.chat.bluetooth.bluetoothEDR.BtPackage;

import blue.happening.chat.bluetooth.bluetoothEDR.Connection.BluetoothService;

/**
 * The replacement for the buffer of the bluetoothsocket.
 * Also provides a dynamic buffering for different payloads.
 */
public class BtPackageBuffer {

    private BtPackageHandler packageHandler;
    private byte[] headerBufferBytes;
    private int headerIndexPointer;
    private byte[] contentBufferBytes;
    private int contentIndexPointer;
    private boolean contentmode;

    /**
     * constructor
     * @param packageHandler the corresponding handler, where the generated packages are delivered
     */
    public BtPackageBuffer(BtPackageHandler packageHandler) {
        this.packageHandler = packageHandler;
        headerBufferBytes = new byte[BtPackage.HEADER_SIZE];
        headerIndexPointer = 0;
        contentIndexPointer = 0;
        contentmode = false;
    }

    /**
     * Filling the internal Buffer (either Header or Content)
     * @param b the byte to add
     * @param connection the corresponding connection
     */
    public void byteReceived(byte b, BluetoothService.Connection connection) {
        if (!contentmode) {
            //add to header
            addToHeader(b,connection);
        } else {
            //add to content
            addToContent(b,connection);
        }
    }

    /**
     * Filling HeaderBuffer. If content is available, go to content mode.
     * Automatically checks if Package is ready to process
     * @param b the byte to add
     * @param connection the corresponding connection
     */
    private void addToHeader(byte b, BluetoothService.Connection connection) {
        headerBufferBytes[headerIndexPointer] = b;
        headerIndexPointer++;

        if (headerIndexPointer >= BtPackage.HEADER_SIZE){
            //Header of Package is ready -- Check if content is available
            BtPackage btPackage = BtPackageParser.generateBtPackageFrom(headerBufferBytes);
            if (btPackage.getChunksize() == 0){
                //send to handler
                headerIndexPointer = 0;
                packageHandler.addToQueue(btPackage, connection);
            }
            else{
                //go to contentMode
                headerIndexPointer= 0;
                contentmode = true;
                contentIndexPointer = 0;
                contentBufferBytes = new byte[btPackage.getChunksize()];
            }
        }
    }

    /**
     * Filling the buffer for Content. Automatically checks if package is ready to process
     * @param b the byte to add
     * @param connection the corresponding connection
     */
    private void addToContent(byte b, BluetoothService.Connection connection){
        contentBufferBytes[contentIndexPointer] = b;
        contentIndexPointer++;
        if (contentIndexPointer >= contentBufferBytes.length){
            //Ready to sent - Merge with Header
            contentmode = false;
            BtPackage btPackage = BtPackageParser.generateBtPackageFrom(headerBufferBytes);
            btPackage.setContent(contentBufferBytes);
            packageHandler.addToQueue(btPackage, connection);
        }
    }
}
