import React, {useEffect } from 'react';

import {useMutation} from '@apollo/client';

import {prepareMFAQuery} from '../graphQL/MFAmanagement.gql';

const PrepareMFA = ({formData, navigation, headers }) => {
    const {previous,  go } = navigation;
    const {password } = formData;

    const [prepareMFA ] = useMutation(prepareMFAQuery, {
        variables:{
            password: password,
            provider: 'jahia-mfa-otp-provider'
        },
        context: {
            headers: headers
        },
        onCompleted: data => {
            if (data && data.mfa && data.mfa.prepareMFA ) {
                console.log("goooo MFA QR CODE");
                go('viewQRCode')
            }
        }
    });

return (
    <div className="form">
        <h3>Prepare MFA</h3>
        <div><button onClick={prepareMFA}>Prepare MFA</button></div>
        <div>
            <button onClick={previous}>Previous</button>
        </div>
    </div>
);
};

export default PrepareMFA;
