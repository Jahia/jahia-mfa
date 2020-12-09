import React, { useState,useEffect } from 'react';

import ItemForm from "./ItemForm";
import StateDrop from "./StateDrop";
import {useQuery, useLazyQuery } from '@apollo/client';

import {activateMFAQuery, verifyMFAEnforcementQuery,verifyTokenQuery} from '../graphQL/MFAmanagement.gql';

const DeactivateMFA = ({ setForm, formData, navigation }) => {
    const { go } = navigation;
    const { username, password } = formData;
    const headers = {   'Authorization': 'Basic ' + Buffer.from(username+":"+password).toString("base64"),
    'Content-Type': 'application/json'
    }

    const [deactivateMFA, { data, loading, error }  ] = useLazyQuery(activateMFAQuery, {
        variables:{
            activate: 'false',
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
            console.log("goooo activateMFA");
            go('activateMFA')
        }
    }, [data, loading]);



    const { previous, next } = navigation;

return (
    <div className="form">
        <h3>Dectivate MFA</h3>
        <div><button onClick={deactivateMFA}>Dectivate MFA</button></div>
        <div>
            <button onClick={previous}>Previous</button>
            <button onClick={next}>Next</button>
        </div>
    </div>
);
};

export default DeactivateMFA;
