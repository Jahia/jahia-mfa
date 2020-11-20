package org.jahia.modules.mfa.otp.actions;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
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

public class RetrieveQRCode extends Action {

    private static final int QRCODE_SIZE = 200;
    public static final String MIXIN_MFA_OTP = "jmix:MFAOTP";
    public static final String PROP_SECRET_KEY = "secretKey";

    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource,
            JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

        final JCRUserNode user = jcrSessionWrapper.getUserNode();
        if (JahiaMFAOtpProvider.isActivated(user)) {
            String password = null;
            if (parameters.containsKey(MFAConstants.PARAM_PASSWORD)) {
                final List<String> passwordValues = parameters.get(MFAConstants.PARAM_PASSWORD);
                if (!passwordValues.isEmpty()) {
                    password = passwordValues.get(0);
                }
            }

            final String oTPKey = JahiaMFAOtpProvider.decryptTotpSecretKey(
                    user.getNode(MFAConstants.NODE_NAME_MFA).getPropertyAsString(Constants.PROP_SECRET_KEY),
                    password,
                    user.getUUID());
            final QRCodeWriter qrCodeWriter = new QRCodeWriter();
            final BitMatrix bitMatrix = qrCodeWriter.encode(
                    String.format("otpauth://totp/jahiaOTP?secret=%s", oTPKey),
                    BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
            final String base64Image = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("QRCODE", base64Image);
            return new ActionResult(HttpServletResponse.SC_OK, null, jsonObject);

        } else {
            return null;
        }
    }

}
