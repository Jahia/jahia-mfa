import React, {useEffect } from 'react';

import {useMutation} from '@apollo/client';

import {activateMFAQuery} from '../graphQL/MFAmanagement.gql';

const DeactivateMFA = ({ setForm, formData, navigation }) => {
    const Buffer = require('buffer').Buffer
    const { go, previous } = navigation;
    const { username, password } = formData;
    const headers = {   'Authorization': 'Basic ' + Buffer.from(username+":"+password).toString("base64"),
    'Content-Type': 'application/json'
    }

    const [deactivateMFA ] = useMutation(activateMFAQuery, {
        variables:{
            activate: 'false',
            provider: 'jahia-mfa-otp-provider'
        },
        context: {
            headers: headers
        },
        onCompleted: data => {
            console.log(data);
            if (data && data.mfa.activateMFA) {
                console.log("goooo login");
                go('login')
            }
        }
    });

return (
    <div className="form">
        <h3>Deactivate MFA</h3>
        <div><button onClick={deactivateMFA}>Deactivate MFA</button></div>
        <div>
            <button onClick={previous}>Previous</button>
        </div>
    </div>
);
};

export default DeactivateMFA;
