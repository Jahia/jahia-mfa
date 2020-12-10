import React, { useState,useEffect } from 'react';

import ItemForm from "./ItemForm";
import StateDrop from "./StateDrop";
import {useQuery, useLazyQuery } from '@apollo/client';

import {activateMFAQuery, prepareMFAQuery,verifyMFAEnforcementQuery,verifyTokenQuery} from '../graphQL/MFAmanagement.gql';

const ActivateMFA = ({ setForm, formData, navigation }) => {
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

    const [activateMFA, { data, loading, error }  ] = useLazyQuery(activateMFAQuery, {
        variables:{
            activate: 'true',
            provider: 'jahia-mfa-otp-provider'
        },
        context: {
            headers: headers
        },
        fetchPolicy: 'cache-and-network'
    });

    useEffect(() => {
        console.log(data);
        if (data && data.activateMFA && !loading) {
            console.log("goooo deactivateMFA");
            go('deactivateMFA')
        }
    }, [data, loading]);


    const { previous, next } = navigation;

return (
    <div className="form">
        <h3>Activate MFA</h3>
        <div><button onClick={prepareMFA}>Prepare MFA</button></div>
        <div><button onClick={activateMFA}>Activate MFA</button></div>
        <div>
            <button onClick={previous}>Previous</button>
            <button onClick={next}>Next</button>
        </div>
    </div>
);
};

export default ActivateMFA;
