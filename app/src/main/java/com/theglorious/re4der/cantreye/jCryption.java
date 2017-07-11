package com.theglorious.re4der.cantreye;

import java.math.BigInteger;

public class jCryption
{
    public jCryption() { }

    public static short[] hexStringToShortArray(String data, int resultSize)
    {
        short[] results = new short[resultSize];
        int i;
        int dataLength = data.length();
        for (i = 0; i < dataLength; i++) {
            if(i%4 == 0) results[i >> 2] = 0;
            results[i >> 2] += (short) (Character.digit(data.charAt(dataLength-i-1), 16) << ((i % 4) << 2));
        }

        return results;
    }

    public int trimmedCountOfShortArray(short[] x)
    {
        int result = x.length-1;
        while(result > 0 && x[result] == 0) result--;
        return result;
    }

    public byte[] byteArrayFromShortArray(short[] x)
    {
        byte[] result = new byte[x.length*2];
        for(int i = 0; i < result.length; i++)
        {
            int shift = (8 * (i % 2));
            result[result.length-1-i] = (byte)((x[(int)(i/2)] & (0x00FF << shift)) >> shift);
        }
        return result;
    }

    //identical function as encrypt in the Javascript jCryption library
    public String encrypt(String exponent, String modulus, String maxDigits, String M)
    {
        int biLength = Integer.parseInt(maxDigits);
        int charSum = 0;
        for(int i = 0; i < M.length(); i++) {charSum += (byte)(M.charAt(i));}

        String tag = "0123456789abcdef";
        String hex = "";
        hex += tag.charAt((charSum & 0xF0) >> 4);
        hex += tag.charAt(charSum & 0x0F);

        String taggedString = hex + M;

        short[] m = hexStringToShortArray(modulus, biLength);
        BigInteger biM = new BigInteger(byteArrayFromShortArray(m));
        int chunkSize = 2 * trimmedCountOfShortArray(m);

        int encryptSize = chunkSize*(1+(int)(taggedString.length()/chunkSize));
        short[] encrypt = new short[encryptSize];

        int i = 0;
        while(i < taggedString.length()) {encrypt[i] = (byte) taggedString.charAt(i); i++;}

        while(i < encryptSize){encrypt[i] = 0; i++;}

        int charCounter = 0;
        String encrypted = "";

        short[] e = hexStringToShortArray(exponent, biLength);
        BigInteger biE = new BigInteger(byteArrayFromShortArray(e));

        do {
            byte[] block = new byte[chunkSize];

            int j = 0;
            for(int k = charCounter; k < charCounter+chunkSize; j++, k++)
            {
                block[chunkSize-1-j] = (byte)(encrypt[k]);
            }
            BigInteger biBlock = new BigInteger(block);

            BigInteger crypt = biBlock.modPow(biE, biM);
            String text = crypt.toString(16);
            encrypted += text + " ";
            charCounter += chunkSize;
        } while(charCounter < encryptSize);

        return encrypted.substring(0, encrypted.length()-1);
    }
}
