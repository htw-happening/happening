package blue.happening.chat.bluetooth.bluetoothEDR.BtPackage;

/**
 * Abstraction of a conveyable piece of information
 */
public class BtPackage implements Comparable<BtPackage> {

    // For Package Creation and Parsing
    public static final int PACKAGE_TYPE_SIZE = 1;
    public static final int PACKAGE_ID_SIZE = 4;
    public static final int PACKAGE_TIMESTAMP_SIZE = 8;
    public static final int PACKAGE_AUTHOR_SIZE = 16;
    public static final int PACKAGE_CHUNKSIZE_SIZE = 4;

    public static final int HEADER_SIZE = PACKAGE_TYPE_SIZE +
            PACKAGE_TIMESTAMP_SIZE +
            PACKAGE_ID_SIZE +
            PACKAGE_AUTHOR_SIZE +
            PACKAGE_CHUNKSIZE_SIZE;

    // the fields
    private byte type;
    private int id;
    private long timestamp;
    private String author;
    private int chunksize;
    private byte[] content;

    /**
     * Constructor
     * @param type the type corresponding to BtPackageType
     * @param id an identifier (should be unique)
     * @param timestamp the timestamp (i.e. current)
     * @param author the author
     * @param chunksize the chunksize (can be 0)
     * @param content the payload
     */
    public BtPackage(byte type, int id, long timestamp, String author, int chunksize, byte[] content) {
        this.type = type;
        this.id = id;
        this.timestamp = timestamp;
        this.author = author;
        this.chunksize = chunksize;
        this.content = content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public int getChunksize(){ return chunksize; }

    public byte[] getContent() {
        return content;
    }

    public int getContentSize (){
        return content.length;
    }

    public int getPackageSize(){
        return HEADER_SIZE + content.length;
    }

    /**
     * Conversion of content bytes to a UTF-8 String
     * @return UTF-8 String of Content
     */
    public String getContentAsUTF8String(){
        return BtPackageParser.bytesToString(content);
    }

    public int size(){
        return BtPackage.HEADER_SIZE + this.getContentSize();
    }

    @Override
    public boolean equals(Object o) {
//        Log.d(TAG, this.author +" ==? "+((BtPackage) o).author);
        if(this.id == ((BtPackage) o).id && this.author.equals(((BtPackage) o).author)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "type: "+type +
                ", id: "+id+
                ", startTimestamp: " +timestamp+
                ", author: "+author+
                ", chunksize: "+chunksize+
                ", real ContentSize: "+getContentSize();
    }


    /**
     * Information, if this package should be forwared or not
     * @return forwardeble stare
     */
    public boolean isForwardable(){
        return true; //TODO
    }


    @Override
    public int compareTo(BtPackage another) {
        //TODO
        return this.getId() - another.getId(); //--> Both Audio Packages
    }
}
