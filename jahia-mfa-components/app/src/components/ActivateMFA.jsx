import React, {useEffect } from 'react';


import {useLazyQuery } from '@apollo/client';

import {activateMFAQuery} from '../graphQL/MFAmanagement.gql';

const ActivateMFA = ({ setForm, formData, navigation, headers }) => {
    const { previous,go} = navigation;

    const [activateMFA, { data, loading}  ] = useLazyQuery(activateMFAQuery, {
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
        if (data && data.activateMFA && !loading) {
            go('success')
        }
    }, [data, loading]);




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
