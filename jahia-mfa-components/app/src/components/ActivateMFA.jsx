import React, {useEffect } from 'react';


import {useMutation } from '@apollo/client';

import {activateMFAQuery} from '../graphQL/MFAmanagement.gql';

const ActivateMFA = ({ setForm, formData, navigation, headers }) => {
    const { previous,go} = navigation;

    const [activateMFA] = useMutation(activateMFAQuery, {
        variables:{
            activate: 'true',
            provider: 'jahia-mfa-otp-provider'
        },
        context: {
            headers: headers
        },
        onCompleted: data => {
            if (data && data.mfa && data.mfa.activateMFA) {
                go('success')
            }
        }
    });

return (
    <div className="form">
        <h3>Activate MFA</h3>
        <div><button onClick={activateMFA}>Activate MFA</button></div>
        <div>
            <button onClick={previous}>Previous</button>
        </div>
    </div>
);
};

export default ActivateMFA;
