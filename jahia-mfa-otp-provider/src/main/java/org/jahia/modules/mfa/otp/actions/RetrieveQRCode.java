package org.jahia.modules.mfa.otp.actions;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.otp.provider.Constants;
import org.jahia.modules.mfa.otp.provider.JahiaMFAOtpProvider;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;
import org.slf4j.Logger;

public  class RetrieveQRCode extends Action{
    public static final String MIXIN_MFA_OTP = "jmix:MFAOTP";
    public static final String PROP_SECRET_KEY = "secretKey";
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(RetrieveQRCode.class);


    @Override public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource,
            JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        JCRUserNode user = jcrSessionWrapper.getUserNode();
        if (JahiaMFAOtpProvider.isActivated(user)){
            String password = null;
            if (parameters.containsKey(MFAConstants.PARAM_PASSWORD)) {
                final List<String> passwordValues = parameters.get(MFAConstants.PARAM_PASSWORD);
                if (!passwordValues.isEmpty()) {
                    password = passwordValues.get(0);
                }
            }

            String OTPkey =
                    JahiaMFAOtpProvider.decryptTotpSecretKey(user.getNode(MFAConstants.NODE_NAME_MFA).getPropertyAsString(Constants.PROP_SECRET_KEY),password,user.getUUID());
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode("otpauth://totp/jahiaOTP?secret="+OTPkey, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
            String base64Image = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("QRCODE", base64Image);
            return new ActionResult(HttpServletResponse.SC_OK, null, jsonObject);

        }else{
            return null;
        }
    }


}
