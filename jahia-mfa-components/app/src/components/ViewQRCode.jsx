
import ItemForm from "./ItemForm";
import React from 'react';
import {useQuery, useLazyQuery } from '@apollo/client';

import {
    verifyTokenQuery,
    retrieveQRCodeQuery,
} from '../graphQL/MFAmanagement.gql';

const ViewQRCode = ({ setForm, formData, navigation }) => {
    const Buffer = require('buffer').Buffer

    let logo = null;
    const { go } = navigation;
    const {token, username, password } = formData;
    const { previous, next } = navigation;
    const headers = {   'Authorization': 'Basic ' + Buffer.from(username+":"+password).toString("base64"),
    'Content-Type': 'application/json'
    }

    const retrieveQRCodeResponse = useQuery(retrieveQRCodeQuery, {
        variables: {
            password: password
        },
        context: {
            headers: headers
        },
        fetchPolicy: 'no-cache'
    });


    const [verifyToken, verifyTokenResponse  ] = useLazyQuery(verifyTokenQuery, {
        variables:{
            password: password,
            provider: 'jahia-mfa-otp-provider',
            token: token
        },
        context: {
            headers: headers
        },
    });

    if (retrieveQRCodeResponse.loading)
        return (<div>LOADING</div>);

    if (retrieveQRCodeResponse.data.mfaOTP.retrieveQRCode){
        logo = "data:image/jpg;base64,"+retrieveQRCodeResponse.data.mfaOTP.retrieveQRCode
    }

    if (verifyTokenResponse.data){
        if (verifyTokenResponse.data.mfa.verifyToken){
            go('activateMFA');
        }
    }

return (
    <div className="form">
        <h3>QR Code</h3>
        <img id="qrimage"  src={logo} width="200" height="200"/>
        <div>
            <ItemForm
                label="QR Token"
                name="token"
                onChange={setForm}
                value={token}
            />
            <div><button onClick={verifyToken}>Verify Token</button></div>
            <button onClick={previous}>Previous</button>
        </div>
    </div>
);
};

export default ViewQRCode;
