package blue.happening.chat.bluetooth.bluetoothEDR.BtPackage;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Serialization and Deserialization of BtPackages
 */
public class BtPackageParser {

    public static final String TAG = BtPackageParser.class.getSimpleName();
    public static final boolean d = false;

    /**
     * Serializes the given BtPackage into a byte array
     * @param btPackage the package to serialize
     * @return the serialized form as an byte array
     */
    public static byte[]  generateByteArrayFrom(BtPackage btPackage){

        byte type = btPackage.getType();
        byte[] id = ByteBuffer.allocate(BtPackage.PACKAGE_ID_SIZE).putInt(btPackage.getId()).array();
        byte[] timestamp = ByteBuffer.allocate(BtPackage.PACKAGE_TIMESTAMP_SIZE).putLong(btPackage.getTimestamp()).array();
        byte[] author = btPackage.getAuthor().getBytes();
        byte[] chunksize = ByteBuffer.allocate(BtPackage.PACKAGE_CHUNKSIZE_SIZE).putInt(btPackage.getChunksize()).array();
        byte[] content = btPackage.getContent();

        byte[] output = new byte[BtPackage.HEADER_SIZE + btPackage.getChunksize()];

        if (d) Log.d(TAG, "Generated tyoe ByteArray: "+ type);
        if (d) Log.d(TAG, "Generated id ByteArray: "+ Arrays.toString(id));
        if (d) Log.d(TAG, "Generated author ByteArray: "+ Arrays.toString(author));
        if (d) Log.d(TAG, "Generated content ByteArray: "+ Arrays.toString(content));
        if (d) Log.d(TAG, "Generated startTimestamp ByteArray: "+ Arrays.toString(timestamp));

        //Type
        output[0] = type;

        //ID
        for(int i = 0; i < BtPackage.PACKAGE_ID_SIZE; i++){
            output[i+BtPackage.PACKAGE_TYPE_SIZE] = id[i];
        }

        //Timestamp
        for(int i = 0; i < BtPackage.PACKAGE_TIMESTAMP_SIZE; i++){
            output[ i+ BtPackage.PACKAGE_TYPE_SIZE + BtPackage.PACKAGE_ID_SIZE] = timestamp[i];
        }

        //Author
        if(author.length > BtPackage.PACKAGE_AUTHOR_SIZE){
            //its to long
            for(int i = 0; i < BtPackage.PACKAGE_AUTHOR_SIZE; i++){
                output[i + BtPackage.PACKAGE_TYPE_SIZE + BtPackage.PACKAGE_ID_SIZE + BtPackage.PACKAGE_TIMESTAMP_SIZE] = author[i];
            }
        }
        else{
            //fits or is to short
            for(int i = 0; i < author.length; i++){
                output[i + BtPackage.PACKAGE_TYPE_SIZE + BtPackage.PACKAGE_ID_SIZE + BtPackage.PACKAGE_TIMESTAMP_SIZE] = author[i];
            }
        }

        //chunksize
        for(int i = 0; i < BtPackage.PACKAGE_CHUNKSIZE_SIZE; i++){
            output[i + BtPackage.PACKAGE_TYPE_SIZE + BtPackage.PACKAGE_ID_SIZE + BtPackage.PACKAGE_TIMESTAMP_SIZE + BtPackage.PACKAGE_AUTHOR_SIZE ] = chunksize[i];
        }

        //Content
        for(int i = 0; i < content.length; i++){
            output[i + BtPackage.PACKAGE_TYPE_SIZE + BtPackage.PACKAGE_ID_SIZE + BtPackage.PACKAGE_TIMESTAMP_SIZE + BtPackage.PACKAGE_AUTHOR_SIZE + BtPackage.PACKAGE_CHUNKSIZE_SIZE] = content[i];
        }

        if (d) Log.d(TAG, "Final ByteArray: "+ Arrays.toString(output));
        return output;
    }

    /**
     * Deserialization of a BtPackage
     * @param bytes the raw byte representation of a BtPackage
     * @return the deserialized form
     */
    public static BtPackage generateBtPackageFrom(byte[] bytes){
        if (d) Log.d(TAG, "starting to read form BytesArray");
        if (d) Log.d(TAG, "Got following ByteArray: "+ Arrays.toString(bytes));

        byte type = bytes[0];
        byte[] idBytes = new byte[BtPackage.PACKAGE_ID_SIZE];
        byte[] timestampBytes = new byte[BtPackage.PACKAGE_TIMESTAMP_SIZE];
        byte[] authorBytes = new byte[BtPackage.PACKAGE_AUTHOR_SIZE];
        byte[] chunksizeBytes = new byte[BtPackage.PACKAGE_CHUNKSIZE_SIZE];


        //ID
        for(int i = 0; i < BtPackage.PACKAGE_ID_SIZE; i++){
            idBytes[i] = bytes[i + BtPackage.PACKAGE_TYPE_SIZE];
        }
        //Timestamp
        for(int i = 0; i < BtPackage.PACKAGE_TIMESTAMP_SIZE; i++){
            timestampBytes[i] = bytes[i + BtPackage.PACKAGE_TYPE_SIZE + BtPackage.PACKAGE_ID_SIZE];
        }
        //Author
        for(int i = 0; i < BtPackage.PACKAGE_AUTHOR_SIZE; i++){
            authorBytes[i] = bytes[i + BtPackage.PACKAGE_TYPE_SIZE + BtPackage.PACKAGE_ID_SIZE + BtPackage.PACKAGE_TIMESTAMP_SIZE];
        }
        //Content
        for(int i = 0; i < BtPackage.PACKAGE_CHUNKSIZE_SIZE; i++){
            chunksizeBytes[i] = bytes[i + BtPackage.PACKAGE_TYPE_SIZE + BtPackage.PACKAGE_ID_SIZE + BtPackage.PACKAGE_TIMESTAMP_SIZE + BtPackage.PACKAGE_AUTHOR_SIZE];
        }

        if (d) Log.d(TAG, "Regenerated id ByteArray: "+ Arrays.toString(idBytes));
        if (d) Log.d(TAG, "Regenerated author ByteArray: "+ Arrays.toString(authorBytes));
        if (d) Log.d(TAG, "Regenerated startTimestamp ByteArray: "+ Arrays.toString(timestampBytes));
        if (d) Log.d(TAG, "Regenerated content ByteArray: "+ Arrays.toString(chunksizeBytes));


        int id = bytesToInt(idBytes);
        if (d) Log.d(TAG, "ID was: "+id);

        long timestamp = bytesToLong(timestampBytes);
        if (d) Log.d(TAG, "startTimestamp was: "+timestamp);

        String author = bytesToString(authorBytes);
        if (d) Log.d(TAG, "author was: "+author);

        int chunksize = bytesToInt(chunksizeBytes);
        if (d) Log.d(TAG, "chunksize was: "+ chunksize);

        return new BtPackage(type, id, timestamp, author,  chunksize, new byte[0]);
    }

    /**
     * Converting bytes to a propper String. Ignores empty parts of the input array.
     * @param bytes the byte representation of the String
     * @return the UTF-8 String
     */
    public static String bytesToString (byte[] bytes){
        int stringLength = bytes.length;
        for (int i = 0; i < bytes.length; i++){
            if(bytes[i] == 0){
                stringLength = i;
                break;
            }
        }
        byte[] shortend = new byte[stringLength];
        for (int i = 0; i < shortend.length; i++){
            shortend[i] = bytes[i];
        }
        return new String(shortend, Charset.defaultCharset());
    }

    /**
     * Converting bytes to a proper Integer
     * @param bytes byteform of an integer*
     * @return the integer of the bytes
     */
    public static int bytesToInt (byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.getInt();
    }

    /**
     * Convertring bytes to a proper Long
     * @param bytes byteform of a Long
     * @return the long of the bytes
     */
    public static long bytesToLong(byte[] bytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        return byteBuffer.getLong();
    }
}
