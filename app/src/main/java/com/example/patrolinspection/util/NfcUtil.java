package com.example.patrolinspection.util;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

public class NfcUtil
{
    public static String getID(Intent intent){
        if (intent == null)
            return null;

        String action = intent.getAction();
        LogUtil.e("NfcActivityUtil",action);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(action) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            return getHex(tag.getId());
        }
        return null;
    }

    private static String getHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString().toUpperCase();
    }
}
