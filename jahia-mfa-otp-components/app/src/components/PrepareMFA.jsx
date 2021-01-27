import React, {useEffect } from 'react';

import {useMutation} from '@apollo/client';

import {prepareMFAQuery} from '../graphQL/MFAmanagement.gql';
import {Button, Typography} from "@jahia/moonstone";

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
        <Typography>Prepare MFA</Typography>
        <div><Button label="Prepare MFA" onClick={prepareMFA}/></div>
        <div>
            <Button label="Previous" onClick={previous}></Button>
        </div>
    </div>
);
};

export default PrepareMFA;
