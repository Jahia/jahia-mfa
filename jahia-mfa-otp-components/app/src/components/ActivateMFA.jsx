import React, {useEffect } from 'react';


import {useMutation } from '@apollo/client';

import {activateMFAQuery} from '../graphQL/MFAmanagement.gql';
import {Button, Typography} from "@jahia/moonstone";

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
        <Typography>Activate MFA</Typography>

        <div><Button label="Activate MFA" onClick={activateMFA}/></div>
        <div>
            <Button label="Previous" onClick={previous}/>
        </div>
    </div>
);
};

export default ActivateMFA;
