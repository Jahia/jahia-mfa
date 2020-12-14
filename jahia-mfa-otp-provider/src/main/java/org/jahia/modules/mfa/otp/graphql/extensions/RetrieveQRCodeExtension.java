package org.jahia.modules.mfa.otp.graphql.extensions;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import graphql.annotations.annotationTypes.*;
import org.jahia.modules.graphql.provider.dxm.DXGraphQLProvider;
import org.jahia.modules.mfa.MFAConstants;
import org.jahia.modules.mfa.graphql.extensions.Utils;
import org.jahia.modules.mfa.otp.provider.Constants;
import org.jahia.modules.mfa.otp.provider.JahiaMFAOtpProvider;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.decorator.JCRUserNode;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@GraphQLTypeExtension(DXGraphQLProvider.Query.class)
public class RetrieveQRCodeExtension {
    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveQRCodeExtension.class);
    private static final int QRCODE_SIZE = 200;

    @GraphQLField
    @GraphQLName("retrieveQRCode")
    @GraphQLDescription("Retrieve OTP QR Code")
    public static String RetrieveQRCodeExtension(
            @GraphQLName(MFAConstants.PARAM_PASSWORD) @GraphQLDescription("password") @GraphQLNonNull String password)
            throws RepositoryException, WriterException, IOException, JSONException {
        final JSONObject jsonObject = new JSONObject();
        LOGGER.info("retrieving OPT QR Code");
        final JCRUserNode userNode = Utils.getUserNode(JCRSessionFactory.getInstance().getCurrentUser());
        if (JahiaMFAOtpProvider.isActivated(userNode) && password!=null) {
            final String oTPKey = JahiaMFAOtpProvider.decryptTotpSecretKey(userNode.getNode(MFAConstants.NODE_NAME_MFA).getPropertyAsString(Constants.PROP_SECRET_KEY),
                    password, userNode.getUUID());
            final QRCodeWriter qrCodeWriter = new QRCodeWriter();
            final BitMatrix bitMatrix = qrCodeWriter
                    .encode(String.format("otpauth://totp/jahiaOTP?secret=%s", oTPKey), BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE);
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "png", byteArrayOutputStream);
            final String base64Image = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            jsonObject.put("QRCODE", base64Image);
            return base64Image;
        }
     return "";
    }
}

