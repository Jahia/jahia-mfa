import React, { useState,useEffect } from 'react';

import ItemForm from "./ItemForm";
import StateDrop from "./StateDrop";
import {useQuery, useLazyQuery } from '@apollo/client';

import {activateMFAQuery, verifyMFAEnforcementQuery,verifyTokenQuery, retrieveQRCodeQuery} from '../graphQL/MFAmanagement.gql';

const ViewQRCode = ({ setForm, formData, navigation }) => {
    const Buffer = require('buffer').Buffer
    let logo = null;
    const { go } = navigation;
    const { username, password } = formData;
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
        }
    });

    if (retrieveQRCodeResponse.loading)
        return <div>LOADING</div>;
    if (retrieveQRCodeResponse.data.retrieveQRCode){
        console.log(retrieveQRCodeResponse.data);
        logo = "data:image/jpg;base64,"+retrieveQRCodeResponse.data.retrieveQRCode

    }

return (
    <div className="form">
        <h3>QR Code</h3>
        <img id="qrimage"  src={logo} width="200" height="200"/>
        <div>
            <button onClick={previous}>Previous</button>
            <button onClick={next}>Next</button>
        </div>
    </div>
);
};

export default ViewQRCode;
