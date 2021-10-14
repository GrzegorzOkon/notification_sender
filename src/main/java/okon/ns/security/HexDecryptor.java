package okon.ns.security;

import javax.xml.bind.DatatypeConverter;

public class HexDecryptor {
    public static String convert(String hex) {
        byte[] bytes = DatatypeConverter.parseHexBinary(hex);
        return new String(bytes);
    }
}
