import React, { useState,useEffect } from 'react';

import ItemForm from "./ItemForm";
import StateDrop from "./StateDrop";
import {useQuery, useLazyQuery } from '@apollo/client';

import {activateMFAQuery, prepareMFAQuery,verifyMFAEnforcementQuery,verifyTokenQuery} from '../graphQL/MFAmanagement.gql';

const PrepareMFA = ({ setForm, formData, navigation }) => {
    const Buffer = require('buffer').Buffer
    const { go } = navigation;
    const { username, password } = formData;
    const headers = {   'Authorization': 'Basic ' + Buffer.from(username+":"+password).toString("base64"),
    'Content-Type': 'application/json'
    }

    const [prepareMFA, prepareMFAResponse  ] = useLazyQuery(prepareMFAQuery, {
        variables:{
            password: password,
            provider: 'jahia-mfa-otp-provider'
        },
        context: {
            headers: headers
        },
        fetchPolicy: 'cache-and-network'
    });


    useEffect(() => {
        console.log(prepareMFAResponse.data);
        if (prepareMFAResponse.data && prepareMFAResponse.data.prepareMFA && !prepareMFAResponse.loading) {
            console.log("goooo MFA QR CODE");
            go('viewQRCode')
        }
    }, [prepareMFAResponse.data, prepareMFAResponse.loading]);


    const { previous, next } = navigation;

return (
    <div className="form">
        <h3>Prepare MFA</h3>
        <div><button onClick={prepareMFA}>Prepare MFA</button></div>
        <div>
            <button onClick={previous}>Previous</button>
            <button onClick={next}>Next</button>
        </div>
    </div>
);
};

export default PrepareMFA;
