import React from "react";
import { useForm, useStep } from "react-hooks-helper";
import LoginForm from "./LoginForm";
import MFAManagement from "./MFAManagement";
import Review from "./Review";
import Submit from "./Submit";
import ActivateMFA from "./ActivateMFA";
import DeactivateMFA from "./DeactivateMFA";
import ViewQRCode from "./ViewQRCode";
import "./styles.css";
import {useQuery} from "@apollo/client";
import {verifyMFAEnforcementQuery} from "../graphQL/MFAmanagement.gql";


const steps = [
  { id: "login" },
  { id: "manageMFA" },
  {id: "activateMFA"},
  {id: "deactivateMFA"},
  {id: "viewQRCode"},
  { id: "review" },
  { id: "submit" }
];

const defaultData = {
  username: "anne",
  password: "password",
  nickName: "Jan",
  address: "200 South Main St",
  city: "Anytown",
  state: "CA",
  zip: "90505",
  email: "email@domain.com",
  phone: "+61 4252 454 332"
};

const MultiStepForm = () => {
  var Buffer = require('buffer').Buffer
  const [formData, setForm] = useForm(defaultData);
  const { step, navigation } = useStep({ initialStep: 0, steps });
  const { id } = step;
  const headers = {   'Authorization': 'Basic ' + Buffer.from(defaultData.username+":"+defaultData.password).toString("base64"),
    'Content-Type': 'application/json'
  }

  const props = { formData, setForm, navigation };
  const verifyMFAEnforcementResponse = useQuery(verifyMFAEnforcementQuery, {
    variables: {
      username: 'anne',
      sitekey: 'digitall'
    },
    context: {
      headers: headers
    }
  });

  switch (id) {
    case "login":
      return <LoginForm {...props} />;
    case "manageMFA":
      if (verifyMFAEnforcementResponse.data.verifyMFAEnforcement)
        return <DeactivateMFA {...props} />;
      else
        return <ActivateMFA {...props} />;
    case "activateMFA":
      return <ActivateMFA {...props} />;
    case "deactivateMFA":
      return <DeactivateMFA {...props} />;
    case "viewQRCode":
      return <ViewQRCode {...props} />;
    case "review":
      return <Review {...props} />;
    case "submit":
      return <Submit {...props} />;
    default:
      return null;
  }
};

export default MultiStepForm;
