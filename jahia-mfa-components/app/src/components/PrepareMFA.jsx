import React, {useEffect } from 'react';

import {useLazyQuery } from '@apollo/client';

import {prepareMFAQuery} from '../graphQL/MFAmanagement.gql';

const PrepareMFA = ({formData, navigation, headers }) => {
    const {previous,  go } = navigation;
    const {password } = formData;

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
