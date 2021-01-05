import React, { useState,useEffect } from 'react';
import {useQuery, useLazyQuery } from '@apollo/client';

import {activateMFAQuery, verifyMFAEnforcementQuery,verifyTokenQuery} from '../graphQL/MFAmanagement.gql';

const MFAManagement = ({ setForm, formData, navigation }) => {
    const { username, password } = formData;
    const headers = {   'Authorization': 'Basic ' + Buffer.from(username+":"+password).toString("base64"),
    'Content-Type': 'application/json'
    }

    const [activateMFA, activateMFAResults  ] = useLazyQuery(activateMFAQuery, {
        variables:{
            activate: 'true',
            provider: 'jahia-mfa-otp-provider'
        },
        context: {
            headers: headers
        }
    });

    const [deactivateMFA, deactivateMFAResults  ] = useLazyQuery(activateMFAQuery, {
        variables:{
            activate: 'false',
            provider: 'jahia-mfa-otp-provider'
        },
    context: {
            headers: headers
        }
    });

    const [verifyToken, verifyTokenResults  ] = useLazyQuery(verifyTokenQuery, {
        variables:{
            password: 'anne',
            provider: 'digitall',
            token: 'digitall'
        },
        context: {
            headers: headers
        }
    });
    const { previous, next } = navigation;
    const verifyMFAEnforcementResponse = useQuery(verifyMFAEnforcementQuery, {
        variables: {
            username: 'anne',
            sitekey: 'digitall'
        },
        context: {
            headers: headers
        }
    });

    if (verifyMFAEnforcementResponse)
        console.log(verifyMFAEnforcementResponse.data);
    if (deactivateMFA)
        console.log(deactivateMFA.data);
    if (activateMFA)
        console.log(activateMFA.data);

    if (verifyMFAEnforcementResponse.loading)
    return <div>LOADING</div>;
if (verifyMFAEnforcementResponse.data.verifyMFAEnforcement)
  return (
    <div className="form">
      <h3>Manage MFA</h3>
        <div><button onClick={deactivateMFA}>Deactivate MFA</button></div>
        <div><button onClick={verifyToken}>verify Token</button></div>
      <div>
        <button onClick={previous}>Previous</button>
          <button onClick={next}>Next</button>
      </div>
    </div>
  );
    if (!verifyMFAEnforcementResponse.data.verifyMFAEnforcement)
        return (
            <div className="form">
                <h3>Manage MFA</h3>
                <div><button onClick={activateMFA}>Activate MFA</button></div>
                <div><button onClick={verifyToken}>verify Token</button></div>
                <div>
                    <button onClick={previous}>Previous</button>
                    <button onClick={next}>Next</button>
                </div>
            </div>
        );
};

export default MFAManagement;
