import React, {useEffect} from "react";

import {useLazyQuery} from "@apollo/client";
import {verifyMFAStatusQuery} from "../graphQL/MFAmanagement.gql";
import {Typography, Input,Button,Separator} from '@jahia/moonstone';

const LoginForm = ({ setForm, formData, navigation, headers}) => {
  const { username, password } = formData;
  const { go } = navigation;

  const  [verifyMFAStatus, { data, loading, error }  ] = useLazyQuery(verifyMFAStatusQuery, {
        variables: {
            username: username,
            sitekey: 'digitall'
        },
        context: {
            headers: headers
        },
        fetchPolicy: 'no-cache'
    });


    useEffect(() => {
        console.log(data);
        if (data && data.mfaOTP.verifyMFAStatus && !loading) {
            go('deactivateMFA')
        }
        if (data && !data.mfaOTP.verifyMFAStatus && !loading) {
            go('prepareMFA')
        }
    }, [data, loading]);


    if (loading) return <p>Loading ...</p>;

    return (

    <div className="form">
        <Typography>Login Form</Typography>
        <div>
      <Input placeholder="Username"
        name="username"
        value={username}
        onChange={setForm}
      /></div>

       <div> <Input placeholder="Password"
        name="password"
        value={password}
        onChange={setForm}
      /></div>

        <Button label={'Login'} onClick={verifyMFAStatus}/>
    </div>
  );
};

export default LoginForm;
