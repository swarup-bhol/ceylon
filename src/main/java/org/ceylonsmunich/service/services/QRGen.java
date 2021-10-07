package org.ceylonsmunich.service.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
//import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QRGen {

    public String generate(String content) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 100, 100);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        byte[] encode = Base64.getEncoder().encode(pngOutputStream.toByteArray());
        String base64Img=encode.toString();
//        BASE64Encoder encoder = new BASE64Encoder();
//        String base64Img = encoder.encode(pngOutputStream.toByteArray());
        return "data:image/png;base64,"+base64Img.replaceAll("\r|\n","");
    }
}
