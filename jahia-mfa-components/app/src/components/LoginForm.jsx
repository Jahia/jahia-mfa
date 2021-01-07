import React, {useEffect} from "react";

import ItemForm from "./ItemForm";
import {useLazyQuery} from "@apollo/client";
import {verifyMFAStatusQuery} from "../graphQL/MFAmanagement.gql";

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
        if (data && data.verifyMFAStatus && !loading) {
            go('deactivateMFA')
        }
        if (data && !data.verifyMFAStatus && !loading) {
            go('prepareMFA')
        }
    }, [data, loading]);


    if (loading) return <p>Loading ...</p>;

    return (

    <div className="form">
        <div>
      <ItemForm
        label="Username"
        name="username"
        value={username}
        onChange={setForm}
      />
      <ItemForm
        label="Password"
        name="password"
        value={password}
        onChange={setForm}
      />
        </div>
        <button onClick={verifyMFAStatus}>Login</button>
    </div>
  );
};

export default LoginForm;
