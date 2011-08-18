package com.ironiacorp.network;


public class UnsignedByte {
    public int b;
    //NOTE: this may not work.
    public UnsignedByte(int byteInt) {
            if (byteInt > 255) {
                    throw new NumberFormatException(
                                    "Too large number for unsigned byte");
            }
            b = byteInt;
//          if ( byteInt < 0 ) b += 256;
            //b = (byte) byteInt;

    }

    public UnsignedByte(byte aByte){
            b = aByte;
//          if (aByte < 0) b += 256;
    }
    public byte toByte(){
            if (b <128)
                    return (byte)b;
            else
                    return (byte) (b -256);
    }
    /**
     * just returns the integer value of the stored byte
     *
     * @return
     */
    public int toInt() {
//          return (int)b & 0xFF;
//          return b;
            return b;
//          if (b < 128){
//                  return b;
//          }else {
//                  return b -256;
//          }
    }

    @Override
    public String toString() {
            return toInt()+"";
    }



//  public byte toByte() {
//          return b;
//  }



}
